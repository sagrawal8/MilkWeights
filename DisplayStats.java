package application;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DisplayStats {

	private final static String[] MONTHS_OF_YEAR = {"January", "February", "March", "April", "May",  //Stores month of years as numbers
			"June", "July", "August", "September", "October", "November", "December"};

	DecimalFormat df = new DecimalFormat("#.00");
	Farm farm;

	public DisplayStats(Farm farm) {
		this.farm = farm;
	}

	/**
	 * returns total and percent total of each month for FarmReport as a 2D matrix with 1st row as total
	 * @param farmID
	 * @param year
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<String> farmReportResult(String farmID, String year) throws Exception{    
		ArrayList<Farm.Details> list = farm.farmReport(farmID, year);
		ArrayList<String> percentArray = new ArrayList<String>();
		int total = 0;

		//Traverse through list to get total sum
		for (int j = 0; j < list.size(); j++) {
			total += list.get(j).getMilkWeight();
		}
		//Compute each months percent of the total
		for (int i = 0; i < list.size(); i++) {       
			double percent = list.get(i).getMilkWeight() / (double)total * 100;
			percentArray.add(df.format(percent) + "%");
		} 

		return percentArray;
	}

	/**
	 * returns total and percent total of each farm for AnnualReport as a 2D matrix with 1st row as total
	 * @param year
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<String> annualReportResult(String year) throws Exception{    
		ArrayList<Farm.Details> list = farm.annualReport(year);
		ArrayList<String> percentArray = new ArrayList<String>();
		int total = 0;

		//Traverse through list to get total sum
		for (int j = 0; j < list.size(); j++) {
			total += list.get(j).getMilkWeight();
		}
		//Compute each farms percent of the total
		for (int i = 0; i < list.size(); i++) {       
			double percent = list.get(i).getMilkWeight() / (double)total * 100;
			percentArray.add(df.format(percent) + "%");
		} 

		return percentArray;
	}

	/**
	 * returns total and percent total of each farm for Monthly Report as a 2D matrix with 1st row as total
	 * @param month
	 * @param year
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<String> monthlyReportResult(int month, int year) throws Exception{
		ArrayList<Farm.Details> list = farm.monthlyReport(month, year);   
		ArrayList<String> percentArray = new ArrayList<String>();
		int total = 0;

		//Traverse through list to get total sum
		for (int j = 0; j < list.size(); j++) {
			total += list.get(j).getMilkWeight();
		}
		//Compute each farms percent of the total
		for (int i = 0; i < list.size(); i++) {       
			double percent = list.get(i).getMilkWeight() / (double)total * 100;
			percentArray.add(df.format(percent) + "%");
		} 

		return percentArray;
	}

	/**
	 * returns total and percent total of each farm for Date-Range Report as a 2D matrix with 1st row as total
	 * @param year
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<String> dateRangeResult(int year, int month, int day, int endMonth, int endDay) throws Exception{
		ArrayList<Farm.Details> list = farm.dateRange(year, month, day, endMonth, endDay);
		ArrayList<String> percentArray = new ArrayList<String>();
		int total = 0;

		//Traverse through list to get total sum
		for (int j = 0; j < list.size(); j++) {
			total += list.get(j).getMilkWeight();
		}
		//Compute each entries percent of the total
		for (int i = 0; i < list.size(); i++) {       
			double percent = list.get(i).getMilkWeight() / (double)total * 100;
			percentArray.add(df.format(percent) + "%");
		} 

		return percentArray;
	}
}
