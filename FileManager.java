package application;

import javafx.util.Pair;

import javax.imageio.IIOException;
import java.awt.font.NumericShaper;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows saving/loading the farm data to disk, using CSV files.
 */
public class FileManager {
	/**
	 * Reads the CSV file from disk, then parses its contents.
	 *
	 * @param filename the name of the CSV file to load
	 * @return a farm object containing the data from the CSV file
	 */
	public Farm load(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		StringBuilder contents = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			contents.append(line);
			contents.append("\n");
		}

		bufferedReader.close();

		return parse(contents.toString());
	}

	/**
	 * Reads (non-recursively) all CSV files from the given directory, and combines their contents into a single Farm
	 * object.
	 *
	 * @param folderPath the path to the folder containing the CSVs
	 * @return a Farm containing data from all CSVs in that folder
	 * @throws Exception from IO or errors adding to the farm
	 */
	public Farm loadFolder(String folderPath) throws Exception {
		File file = new File(folderPath);

		// Check that the path refers to an existing folder
		if (!file.exists())
			throw new RuntimeException("Folder does not exist");
		if (!file.isDirectory())
			throw new RuntimeException("Path must refer to a folder, not a file");

		// Find all CSV files in the folder
		String[] csvFileNames = file.list((dir, fileName) -> fileName.endsWith(".csv"));

		// Load the CSVs into a single farm object
		Farm combined = new Farm();

		//noinspection ConstantConditions <-- tell IntelliJ not to worry about csvFileNames being null (it won't be)
		for (String csvFileName : csvFileNames) {
			// Load this single CSV file
			Farm farm = load(file.getPath() + "/" + csvFileName);

			// Add its contents into the combined farm
			for (Pair<Date, Farm.Details> pair : farm.getAllDetails()) {
				Date date = pair.getKey();
				Farm.Details details = pair.getValue();

				combined.add(date, details.getFarmID(), details.getMilkWeight() + "");
			}
		}

		return combined;
	}

	/**
	 * If the given pathname refers to a folder, calls {@link #loadFolder(String)} and reads all the CSV files in that
	 * folder to a farm. Otherwise, if the pathname refers to a normal file, calls {@link #load(String)} and reads the
	 * single CSV file specified by the path.
	 *
	 * @param pathname the path to the CSV file, or the folder of CSVs
	 * @return a farm containing the data
	 * @throws Exception from IO or from errors adding to the farm
	 */
	public Farm loadFileOrFolder(String pathname) throws Exception {
		File file = new File(pathname);

		if (file.isDirectory())
			return loadFolder(pathname);
		else
			return load(pathname);
	}

	/**
	 * Parse the given contents of a CSV file. Note that this method is for when you already have the contents of the
	 * CSV file as a String. If you only have the name of the file, use {@link #load(String)}, which will read the
	 * file from disk first.
	 *
	 * @param contents the contents of a CSV file.
	 * @return a farm object containing the data from the CSV file
	 */
	public Farm parse(String contents) {
		String[] lines = contents.trim().split("\\r?\\n"); // split by newline

		// We will store all data from the file in this Farm object
		Farm farm = new Farm();

		// Index of each column in the CSV file
		// E.g. if the first column was the date, then dateColumnIndex would be set to 0
		// These are needed so we know the position of each column
		int dateColumnIndex = -1;
		int farmIDColumnIndex = -1;
		int weightColumnIndex = -1;

		for (int i = 0; i < lines.length; i++) {
			//if (lines[i].isBlank()) continue;

			String[] columns = lines[i].split(",");

			if (i == 0) {
				// This is the first line, it contains the CSV file's columns
				// e.g. date,farm_id,weight

				// We want to record the index of the date column, the farm ID column, and the weight column so that we
				// know where these are for later rows

				for (int j = 0; j < columns.length; j++) {
					String column = columns[j];
					if (column.equals("date")) dateColumnIndex = j;
					else if (column.equals("farm_id")) farmIDColumnIndex = j;
					else if (column.equals("weight")) weightColumnIndex = j;
				}

				// Make sure the date, farm_id, and weight columns were all found
				if (dateColumnIndex == -1 || farmIDColumnIndex == -1 || weightColumnIndex == -1)
					throw new ParsingException("Missing one or more of these columns: date, farm_id, weight");
			} else {
				// This isn't the first line, so it will contain one data point for some farm
				String date = columns[dateColumnIndex];
				String farmID = columns[farmIDColumnIndex];
				String weightStr = columns[weightColumnIndex];

				// Ensure that weightStr is an integer
				// (note: because the farm ID can be any string, we don't check if it is an integer)

				try {
					if (!weightStr.equals("-")) {
						Integer.parseInt(weightStr);
					}
				} catch (NumberFormatException ex) {
					throw new ParsingException("Couldn't parse weight '" + weightStr + "' as an integer", ex);
				}

				// Add data-point for this day into the farm
				try {
					if (!weightStr.equals("-") && !date.equals("-")) {
						Date dateObj = Date.fromYYYYMD(date);
						System.out.println(weightStr);
						System.out.println("HERE");
						farm.add(dateObj, farmID, weightStr);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error adding data to farm", e);

				}
			}
		}

		// Return the farm object
		return farm;
	}

	/**
	 * Write the data stored in the Farm object to a CSV string. This string can be then saved as a CSV file.
	 *
	 * @param farm farm to serialize
	 * @return string of CSV contents
	 */
	public String serialize(Farm farm) {
		StringBuilder output = new StringBuilder();

		output.append("date,farm_id,weight\n");

		for (Pair<Date, Farm.Details> data : farm.getAllDetails()) {
			Date date = data.getKey();
			Farm.Details details = data.getValue();

			// Reformat the date - we want it to be in YYYY-M-D format
			String formattedDate = date.toYYYYMD();

			output.append(String.format("%s,%s,%s\n", formattedDate, details.getFarmID(), details.getMilkWeight()));
		}

		return output.toString();
	}

	/**
	 * Serializes the farm then writes it to the given file.
	 * 
	 * @param farm farm to save
	 * @param fileName file to save to
	 * @throws IOException when there is an error saving the file
	 * @see #serialize(Farm)
	 */
	public void save(Farm farm, String fileName) throws IOException {
		FileWriter fileWriter = new FileWriter(fileName, false);
		fileWriter.write(serialize(farm));
		fileWriter.close();
	}

	public static class ParsingException extends RuntimeException {
		public ParsingException(String message) {
			super(message);
		}

		public ParsingException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
