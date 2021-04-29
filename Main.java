package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.Farm.Details;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Class that creates the MilkWeights GUI. This class contains 
 * main and is not meant to be instantiated.
 */
public class Main extends Application {
	private static final String[] MONTHS = {
			"january", "february", "march", "april", "may", "june", "july", "august", "september", "november",
			"december"
	};

	// store any command-line arguments that were entered.
	// NOTE: this.getParameters().getRaw() will get these also
	private List<String> args;

	private static final int WINDOW_WIDTH = 520; //static width dimension for Scene
	private static final int WINDOW_HEIGHT = 439; //static height dimension for Scene
	private static final String APP_TITLE = "Milk Weights Program"; //static title for Scene
	private Farm farm; //global Farm object to be operated on
	private ArrayList<Details> displayFarms; //global displayFarms object to be operated on
	private TableView table = new TableView(); //global TableView for inputting report data
	private FileManager fm = new FileManager(); //global FileManager for reading and outputing data

	/**
	 * Inner class to format data with TableView
	 *
	 */
	public class DetailsFormat {
		private String farmID;
		private String month;
		private int milkWeight;
		private String totalWeight;

		/**
		 * Constructor for DetailsFormat object
		 * @param farmID
		 * @param month
		 * @param milkWeight
		 * @param totalWeight
		 */
		private DetailsFormat(String farmID, String month, int milkWeight, String totalWeight) {
			this.farmID = farmID;
			this.month = month;
			this.milkWeight = milkWeight;
			this.totalWeight = totalWeight;
		}

		/**
		 * FarmID accessor method
		 * @return farmID
		 */
		public String getFarmID() {
			return farmID;
		}

		/**
		 * Month accessor method
		 * @return month
		 */
		public String getMonth() {
			return month;
		}

		/**
		 * MilkWeight accessor method
		 * @return milkWeight
		 */
		public Integer getMilkWeight() {
			return milkWeight;
		}

		/**
		 * TotalWeight accessor method
		 * @return totalWeight
		 */
		public String getTotalWeight() {
			return totalWeight;
		}

		/**
		 * FarmID mutator method
		 * @return
		 */
		public void setFarmID(String f) {
			this.farmID = f;
		}

		/**
		 * Month mutator method
		 * @return
		 */
		public void setMonth(String m) {
			this.month = m;
		}

		/**
		 * MilkWeight mutator method
		 * @return
		 */
		public void setMilkWeight(Integer mW) {
			this.milkWeight = mW;
		}

		/**
		 * TotalWeight mutator method
		 * @return
		 */
		public void setTotalWeight(String tW) {
			this.totalWeight = tW;
		}
	}

	/**
	 * Parses user supplied path name and loads all data into Farm object
	 * 
	 * @param filename
	 * @param submit
	 * @throws Exception - thrown if FileManager cannot parse the file due to invalid input
	 */
	private void parse(String pathName) throws Exception {
		try {
			this.farm = fm.loadFileOrFolder(pathName);
		} catch (Exception e) {
			throw new Exception(); //to be dealt with elsewhere in the form of an Error alert
		}
	}

	/**
	 * Given a user-inputted month (e.g. "5" or "may"), returns the correct month in the "MM" format (e.g. "05") that
	 * can then be passed to the Farm class as a valid parameter.
	 *
	 * @param userInputMonth the user-inputted month string
	 * @return the month in the MM format
	 */
	private String fixMonth(String userInputMonth) {
		// Ignore whitespace at beginning and end of string, and ignore case
		userInputMonth = userInputMonth.trim().toLowerCase();

		// User inputted month as a single digit, e.g. "5", when it should be "05"
		if (userInputMonth.length() == 1) return "0" + userInputMonth;

		// User inputted month as the name of the month (possibly abbreviated)
		if (userInputMonth.length() >= 3) {
			for (int i = 0; i < MONTHS.length; i++) {
				if (MONTHS[i].startsWith(userInputMonth)) {
					// The user meant this month (MONTHS[i]) -- we should return the string value of (i+1)
					String monthStr = String.valueOf(i + 1);
					if (monthStr.length() == 1) monthStr = "0" + monthStr;
					return monthStr;
				}
			}
		}

		// The user either already entered a month in the MM format, or put the month in an invalid format
		return userInputMonth;
	}

	/**
	 * Processes Farm file into a report which the user selects. The program then
	 * adds that data into a TableView
	 *  
	 * @param report - type of report that user selects
	 * @param variables - contains TextField submitted input
	 * @param table
	 * @throws Exception - thrown if entered data is invalid
	 */
	private void displayReport(String report, ArrayList<TextField> variables) throws Exception{
		try {
			DisplayStats dStats = new DisplayStats(farm);
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> reportTotal;
			String tableMonth = "";
			boolean isFarmReport = false;
			boolean check = false;

			for (TextField t : variables) {
				keys.add(t.getText()); //Gets the text from each TextField
			}

			//Based on the report, the ArrayList WILL have the correct Strings, and we use these
			if (report.equals("Farm Report")) {
				String farmid = keys.get(0);
				String year = keys.get(1);

				reportTotal = dStats.farmReportResult(farmid, year);
				displayFarms = farm.farmReport(farmid, year);

				//Exception checking
				for (int i = 0; i < displayFarms.size(); i++) {
					if (displayFarms.get(i).getFarmID().equals(farmid)) {
						if (farm.getValues(new Date(1, 1, Integer.parseInt(year))).get(i) != null) { //placeholder date
							check = true;
							break;
						}
					}
				}
				if (!check) {
					throw new Exception(); //invalid user data
				}

				isFarmReport = true;
			} else if (report.equals("Monthly Report")) {
				String monthStr = fixMonth(keys.get(0));
				String yearStr = keys.get(1);
				int year = Integer.parseInt(yearStr);
				int month = Integer.parseInt(monthStr);

				reportTotal = dStats.monthlyReportResult(month, year);
				displayFarms = farm.monthlyReport(month, year);

				for (int i = 0; i < displayFarms.size(); i++) {
					if (farm.getValues(new Date(month, 1, year)).get(i) != null) { //placeholder date
						check = true;
						break;
					}
				}
				if (!check) {
					throw new Exception(); //invalid user data
				}

				tableMonth = Integer.toString(month);
			} else if (report.equals("Annual Report")) {
				String year = keys.get(0);

				reportTotal = dStats.annualReportResult(year);
				displayFarms = farm.annualReport(year);

				for (int i = 0; i < displayFarms.size(); i++) {
					if (farm.getValues(new Date(1, 1, Integer.parseInt(year))).get(i) != null) { //placeholder date
						check = true;
						break;
					}
				}
				if (!check) {
					throw new Exception(); //invalid user data
				}
			} else {
				String yearStr = keys.get(0);
				String startMonthStr = fixMonth(keys.get(1));
				String startDayStr = keys.get(2);
				String endMonthStr = fixMonth(keys.get(3));
				String endDayStr = keys.get(4);

				int year = Integer.parseInt(yearStr);
				int startMonth = Integer.parseInt(startMonthStr);
				int startDay = Integer.parseInt(startDayStr);
				int endMonth = Integer.parseInt(endMonthStr);
				int endDay = Integer.parseInt(endDayStr);

				reportTotal = dStats.dateRangeResult(year, startMonth, startDay, endMonth, endDay);
				displayFarms = farm.dateRange(year, startMonth, startDay, endMonth, endDay);

				for (int i = 0; i < displayFarms.size(); i++) {
					if (farm.getValues(new Date(startMonth, startDay, year)).get(i) != null) {
						check = true;
						break;
					}
				}
				if (!check || (startMonth > endMonth)) {
					throw new Exception(); //invalid user data
				}
				check = false;
				for (int i = 0; i < displayFarms.size(); i++) {
					if (farm.getValues(new Date(endMonth, endDay, year)).get(i) != null) {
						check = true;
						break;
					}
				}
				if (!check) {
					throw new Exception(); //invalid user data
				}
			}

			//Setting up TableView for report output
			ObservableList<DetailsFormat> data = FXCollections.observableArrayList();

			//Adding to TableView
			for (int i = 0; i < displayFarms.size(); i++) {
				Details d = displayFarms.get(i);
				if (isFarmReport) { //output ascending month order for Farm Report
					data.add(new DetailsFormat(d.getFarmID(), Integer.toString(d.getMonth()), 
							d.getMilkWeight(), reportTotal.get(i)));
				}
				else {
					data.add(new DetailsFormat(d.getFarmID(), tableMonth, d.getMilkWeight(), reportTotal.get(i)));
				}
			}
			table.setItems(data);

			TableColumn farmIDCol = new TableColumn("Farm ID");
			TableColumn monthCol = new TableColumn("Month");
			TableColumn milkWeightCol = new TableColumn("Milk Weight");
			TableColumn totalCol = new TableColumn("Total Weight");

			farmIDCol.setCellValueFactory(new PropertyValueFactory<DetailsFormat, String>("farmID"));
			monthCol.setCellValueFactory(new PropertyValueFactory<DetailsFormat, Integer>("month"));
			milkWeightCol.setCellValueFactory(new PropertyValueFactory<DetailsFormat, Integer>("milkWeight"));
			totalCol.setCellValueFactory(new PropertyValueFactory<DetailsFormat, Integer>("totalWeight"));

			table.getColumns().setAll(farmIDCol, monthCol, milkWeightCol, totalCol);
		} catch (Exception e) {
			throw new Exception();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//Save args example
		args = this.getParameters().getRaw();

		//Creating TextField for file input
		TextField fileInput = new TextField();
		fileInput.setMinWidth(170);
		fileInput.setPromptText("Input valid file or folder path");

		//Creating Button for instructions
		Button instructions = new Button("Instructions");
		instructions.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Alert confirm = new Alert(AlertType.CONFIRMATION, "Please enter the folder path or file path containing valid .csv document data"
						+ " into the text field. Then select a report type:\n\n"
						+ "\t1. Farm Report: Please enter a valid farm ID and year. \n"
						+ "\t2. Annual Report: Please enter a valid year. \n"
						+ "\t3. Monthly Report: Please enter a valid month and year. \n"
						+ "\t4. Date Range Report: Please enter a valid year, starting \n\tday, starting "
						+ "month, ending month, and ending year.\n\n"
						+ "Once all fields have been filled in, click the 'Submit' button to display the corresponding report. \n\n"
						+ "The 'Write' button allows the user to have their activity be output into a file. "
						+ "The 'Close' button allows the user"
						+ " to end the program. The 'Add', 'Edit', 'Remove', and 'Display' are included for display and do note perform "
						+ "legitimate computations. Enjoy the program!");
				confirm.show();
			}
		});

		//Creating Button for instructions
		Button close = new Button("Close");
		close.setMinWidth(78);
		close.setAlignment(Pos.BASELINE_LEFT);
		close.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		//Creating Button to write output file
		Button write = new Button("Write");
		write.setMinWidth(78);
		write.setAlignment(Pos.BASELINE_LEFT);
		write.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				try {
					fm.save(farm, "output");
				} catch (Exception e) {
					Alert warning = new Alert(AlertType.ERROR, "Something went wrong trying to write to the file!");
					warning.show();
				}
			}
		});

		//Creating ComboBox for report type selection
		ComboBox<String> reportComboBox = new ComboBox<String>();
		reportComboBox.getItems().addAll(
				"Farm Report",
				"Annual Report",
				"Monthly Report",
				"Date Range Report");
		reportComboBox.setValue("Select a report");
		reportComboBox.setOnAction((e) -> {
			Stage newWindow = new Stage();
			GridPane gPane = new GridPane();
			String select = reportComboBox.getSelectionModel().getSelectedItem();
			ArrayList<TextField> variables = new ArrayList<TextField>();

			//Set-up for various reports
			if (select.equals("Farm Report")) {
				gPane.add(new Label("Enter farm: "), 0, 0);
				TextField tFarm = new TextField();
				gPane.add(tFarm, 1, 0);

				gPane.add(new Label("Enter year: "), 0, 1);
				TextField tYear = new TextField();
				gPane.add(tYear, 1, 1);

				variables.add(tFarm);
				variables.add(tYear);
			}
			else if (select.equals("Annual Report")) {
				gPane.add(new Label("Enter year: "), 0, 0);
				TextField tYear = new TextField();
				gPane.add(tYear, 1, 0);

				variables.add(tYear);
			}
			else if (select.equals("Monthly Report")) {
				gPane.add(new Label("Enter month: "), 0, 0);
				TextField tMonth = new TextField();
				gPane.add(tMonth, 1, 0);

				gPane.add(new Label("Enter year: "), 0, 1);
				TextField tYear = new TextField();
				gPane.add(tYear, 1, 1);

				variables.add(tMonth);
				variables.add(tYear);
			}
			else {
				gPane.add(new Label("Enter year: "), 0, 0);
				TextField tYear = new TextField();
				gPane.add(tYear, 1, 0);

				gPane.add(new Label("Enter start month: "), 0, 1);
				TextField tSMonth = new TextField();
				gPane.add(tSMonth, 1, 1);

				gPane.add(new Label("Enter start day: "), 0, 2);
				TextField tSDay = new TextField();
				gPane.add(tSDay, 1, 2);

				gPane.add(new Label("Enter end month: "), 0, 3);
				TextField tEMonth = new TextField();
				gPane.add(tEMonth, 1, 3);

				gPane.add(new Label("Enter end day: "), 0, 4);
				TextField tEDay = new TextField();
				gPane.add(tEDay, 1, 4);

				variables.add(tYear);
				variables.add(tSMonth);
				variables.add(tSDay);
				variables.add(tEMonth);
				variables.add(tEDay);
			}

			//Creating Button for submission of inserted text
			//When clicked, file parses and info is displayed
			Button submit = new Button("Submit!");
			submit.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					newWindow.hide();

					try {
						parse(fileInput.getText());
						displayReport(select, variables); //display that report
					} catch (Exception e) {
						//Alerts the user that an Exception was thrown
						Alert warning = new Alert(AlertType.ERROR, "Something went wrong! "
								+ "Check that you've entered a valid file name and input valid information intended for "
								+ "computation.");
						warning.show();
					}
				}
			});
			gPane.add(submit, 0, 5);
			gPane.setVgap(4);
			gPane.setHgap(10);
			gPane.setPadding(new Insets(5, 5, 5, 5));

			Scene secondScene = new Scene(gPane, 330, 200);
			newWindow.setTitle(select);
			newWindow.setScene(secondScene);
			newWindow.show();
		});

		//Defining Add/Edit/Remove/Display Buttons to operate on the file
		Button add = new Button("Add");
		add.setMinWidth(78);
		add.setAlignment(Pos.BASELINE_LEFT);
		add.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Alert confirm = new Alert(AlertType.WARNING, "This is for display purposes "
						+ "only and cannot yet accurately perform computations.");
				confirm.show();
				//addEditSetup("Add", fileOutput);
			}
		});

		Button remove = new Button("Remove");
		remove.setMinWidth(78);
		remove.setAlignment(Pos.BASELINE_LEFT);
		remove.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Alert confirm = new Alert(AlertType.WARNING, "This is for display purposes "
						+ "only and cannot yet accurately perform computations.");
				confirm.show();
				//removeSetup("Remove", fileOutput);
			}
		});

		Button edit = new Button("Edit");
		edit.setMinWidth(78);
		edit.setAlignment(Pos.BASELINE_LEFT);
		edit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Alert confirm = new Alert(AlertType.WARNING, "This is for display purposes "
						+ "only and cannot yet accurately perform computations.");
				confirm.show();
				//addEditSetup("Edit", fileOutput);
			}
		});

		Button display = new Button("Display");
		display.setMinWidth(78);
		display.setAlignment(Pos.BASELINE_LEFT);
		display.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Alert confirm = new Alert(AlertType.WARNING, "This is for display purposes "
						+ "only and cannot yet accurately perform computations.");
				confirm.show();
			}
		});
		display.getOnMouseClicked();

		Image image = new Image("cow.png");
		ImageView iv1 = new ImageView();
		iv1.setImage(image);
		iv1.setFitWidth(100);
		iv1.setPreserveRatio(true);
		iv1.setSmooth(true);
		iv1.setCache(true);

		//Adding to GridPane
		GridPane mainGrid = new GridPane();
		mainGrid.setVgap(4);
		mainGrid.setHgap(10);
		mainGrid.setPadding(new Insets(5, 5, 5, 5));
		mainGrid.add(new Label("File input: "), 0, 0);
		mainGrid.add(fileInput, 1, 0);
		mainGrid.add(new Label("Farm Report Type: "), 2, 0);
		mainGrid.add(reportComboBox, 3, 0);
		mainGrid.add(table, 0, 1, 3, 1);

		//Adding to side GridPane
		GridPane sideGrid = new GridPane();
		sideGrid.setVgap(4);
		sideGrid.setHgap(10);
		sideGrid.setPadding(new Insets(0, 0, 0, 0));
		mainGrid.add(sideGrid, 3, 1);
		sideGrid.add(add, 0, 0);
		sideGrid.add(remove, 0, 1);
		sideGrid.add(edit, 0, 2);
		sideGrid.add(display, 0, 3);
		sideGrid.add(write, 0, 4);
		sideGrid.add(instructions, 0, 15);
		sideGrid.add(close, 0, 16);
		sideGrid.add(iv1, 1, 30);

		//Setting up stage and scene
		Scene scene = new Scene(new Group(), WINDOW_WIDTH, WINDOW_HEIGHT);
		Group root = (Group)scene.getRoot();
		root.getChildren().add(mainGrid);
		primaryStage.setTitle(APP_TITLE);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
