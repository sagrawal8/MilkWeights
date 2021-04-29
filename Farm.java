package application;

import javafx.util.Pair;

import java.util.*;

/**
 * Stores milk weight data for each farms and date that is being tracked
 */
public class Farm {

    /**
     * Details stores the milk weight on a farm for a specific date.
     */
    public class Details {
        private String farmID;             //Stores the farmID for this record.
        private int milkWeight;        //Stores the milkWeight associated with this farmID on a particular date.
        private int month;            //used for monthly report. Stores the month in which this record was entered.

        public Details() {
        }

        //constructor
        public Details(String farmID, String milkWeight, String month) {
            this.farmID = farmID;
            this.milkWeight = Integer.parseInt(milkWeight);
            this.month = Integer.parseInt(MONTHS_OF_YEAR[Integer.parseInt(month) - 1]);
        }

        //constructor
        public Details(String farmID, String milkWeight) {
            this.farmID = farmID;
            this.milkWeight = Integer.parseInt(milkWeight);
            this.month = -1;
        }

        //setter function for farmID
        public void setFarmID(String farmID) {
            this.farmID = farmID;
        }

        //setter function for milkWeight
        public void setMilkWeight(String milkWeight) {
            this.milkWeight = Integer.parseInt(milkWeight);
        }

        //setter function for milkWeight
        public void setMonth(String month) {
            this.month = Integer.parseInt(month);
        }

        //getter function for farmID
        public String getFarmID() {
            return farmID;
        }

        //getter function for milkWeight
        public int getMilkWeight() {
            return milkWeight;
        }

        //getter function for month
        public int getMonth() {
            return month;
        }
    }

    /**
     * A HashMap that associates a date with a list of details objects that keep track of the milk gathered for each
     * farm on that date.
     */
    private Map<Date, ArrayList<Details>> data;

    /**
     * ArrayList containing the IDs of all the farms in the data map. Used for annual and date range report
     */
    private ArrayList<String> farmIDs;

    // TODO: every time we access an element from this array, we do parseInt on it first to convert it to an integer.
    // Could we just store these as ints instead of strings so that we can skip that extra step?
    private final static String[] MONTHS_OF_YEAR = {"01", "02", "03", "04", "05",  //Stores month of years as numbers
            "06", "07", "08", "09", "10", "11", "12"};

    //constructor
    public Farm() {
        data = new HashMap<>(31);
        farmIDs = new ArrayList<>();
    }

    /**
     * Method to view FarmIDs.
     * Used for testing
     */
    public void printFarmIDs() {
        for (int i = 0; i < farmIDs.size(); i++)
            System.out.println(farmIDs.get(i));
    }

    /**
     * Get value associated with key
     *
     * @param date
     * @return ArrayList<details>
     */
    public ArrayList<Details> getValues(Date date) {
        return (data.get(date));
    }

    /**
     * adds a milk weight record to this farm's records.
     * Throws an exception if one of the parameters is invalid
     * Otherwise, returns list of all the details for that date
     *
     * @param date
     * @param farmID
     * @param milkWeight
     * @return ArrayList<details> - updated values
     * @throws Exception 
     */
    public ArrayList<Details> add(Date date, String farmID, String milkWeight) throws Exception {

        ArrayList<Details> list = new ArrayList<>();        
        try {
            // Verify that the given parameters are valid
            if (farmID.isEmpty())  //check if farmID is empty
            {
              throw new Exception("FarmID is empty");
            } else if (milkWeight.isEmpty())  //check if milkWeight is empty
            {
              throw new Exception("Milk Weight is empty");
            } else {                
                //check if milkWeight is negative.
                if (Integer.parseInt(milkWeight) >= 0) {
                    Details obj = new Details(farmID, milkWeight); //create new 'details' obj
                    list.add(obj);  //add obj to list
                    ArrayList<Details> returnedList = data.putIfAbsent(date, list); //add list if key is absent
                    boolean flag1 = false;
                    for (int i = 0; i < farmIDs.size(); i++)   //Add farmID of record to farmID array if not present
                    {
                        if (farmIDs.get(i).equals(farmID))
                            flag1 = true;
                    }
                    if (flag1 == false)                //Add if not present.
                        farmIDs.add(farmID);
                    try {
                        returnedList.isEmpty();  //checks if returnedList is null
                        //if returnedList is not null, add obj to returnedList
                        returnedList.add(obj);
                        boolean flag2 = false;
                        for (int i = 0; i < farmIDs.size(); i++)        //Add farmID of record to farmID array if not present
                        {
                            if (farmIDs.get(i).equals(farmID))
                                flag2 = true;
                        }
                        if (flag2 == false)
                            farmIDs.add(farmID);        //Add if not present

                        return (data.put(date, returnedList)); //add to HashMap
                    } catch (NullPointerException e) {
                        return null;   //if returnedList is null, obj is added.
                    }
                } else {
                    throw new Exception("milk weight is negative");
                }
            }
        } catch (NullPointerException e) {
            throw new Exception("One of the parameters are null");
        }

    }

    /**
     * edits a milk weight record in the HashMap.
     * if date is being edited, newFarmID and newMilkWeight are empty Strings.
     * if farmID is being edited, newMilkWeight is empty string.
     * if milkWeight is edited, there are no empty strings.
     * Throws an exception if a parameter is invalid
     * Otherwise, returns list of all the details for the new date
     *
     * @param date   - never empty
     * @param String farmID - empty if being edited
     * @param String milkWeight - empty if being edited
     * @param int    flag - 1 if date is edited, 2 if farmID is edited, 3 if milkWeight is edited
     * @param String newDate - empty if farmID/milkWeight is edited.
     * @param String newMilkWeight - empty if date/farmID is edited
     * @param String newFarmID - empty if date/milkWeight is edited    *
     * @return ArrayList<details> records for newDate
     * @throws Exception
     */
    public ArrayList<Details> edit(Date date, String farmID, String milkWeight, int flag, Date newDate, String newFarmID, String newMilkWeight) throws Exception {
        ArrayList<Details> list = new ArrayList<Details>();
        try {
            // Verify that the given parameters are valid
            if (farmID.isEmpty()) {
                throw new Exception("FarmID is empty");
            } else if (milkWeight.isEmpty()) {
                throw new Exception("Milk Weight is empty");
            } else {
                //check if milkWeight is negative.
                if (Integer.parseInt(milkWeight) >= 0) {
                    switch (flag) {
                        case 1:                             //case 1: edit date
                            if (data.containsKey(date)) {
                                Details obj = new Details();
                                list = data.get(date);
                                for (int i = 0; i < list.size(); i++) {
                                    obj = list.get(i);
                                    if (obj.milkWeight == Integer.parseInt(milkWeight)) {
                                        list.remove(i);       //find old record and remove it array list associated with old date.
                                        break;
                                    }
                                }
                                try {
                                    add(newDate, obj.farmID, Integer.toString(obj.milkWeight));  //Add record to new date.
                                    return data.get(newDate);
                                } catch (Exception e) {
                                    return null;
                                }
                            }
                            break;

                        case 2:                                   //case 2: edit farmID
                            if (data.containsKey(date))      //check if date is present in Map
                            {
                                boolean check = false;
                                Details obj = new Details();
                                list = data.get(date);
                                for (int i = 0; i < list.size(); i++) {
                                    obj = list.get(i);

                                    //check if farmID present on specified date. if yes, replace. else return null.
                                    if (obj.farmID.equals(farmID) && obj.milkWeight == Integer.parseInt(milkWeight)) {
                                        obj.farmID = newFarmID;
                                        check = true;
                                        break;
                                    }
                                }
                                if (check == false)
                                    return null;
                            }
                            return data.get(date);

                        case 3:                        //case 3: edit milkWeight
                            if (data.containsKey(date))  //check if date is present in Map
                            {
                                boolean check = false;
                                Details obj = new Details();
                                list = data.get(date);
                                for (int i = 0; i < list.size(); i++) {
                                    obj = list.get(i);

                                    //check if farmID and milkWeight match the specified. If yes, replace. else return null.
                                    if (obj.farmID.equals(farmID) && obj.milkWeight == Integer.parseInt(milkWeight)) {
                                        obj.milkWeight = Integer.parseInt(newMilkWeight);
                                        check = true;
                                    }
                                }
                                if (check == false)
                                    return null;
                            }
                            return data.get(date);
                    }                                      //end of switch

                    throw new Exception("What to be edited is not marked");
                } else {
                    throw new Exception("FarmID or milk Weight is negative");
                }
            }
        } catch (NullPointerException e) {
            throw new Exception("One or more of the parameters are null");
        } catch (NumberFormatException e) {
            throw new Exception("Integer passed as String or vice versa");
        }
    }

    /**
     * removes a milk weight record to this farm's records.
     * @throws Exception 
     */
    public ArrayList<Details> remove(Date date, String farmID, String milkWeight) throws Exception {

        ArrayList<Details> list = new ArrayList<Details>();
        try {
          // Verify that the given parameters are valid
              if (farmID.isEmpty())  //check if farmID is empty
              {
                throw new Exception("FarmID is empty");
            } else if (milkWeight.isEmpty())  //check if milkWeight is empty
              {
                throw new Exception("Milk Weight is empty");
        } else {
                //check if milkWeight is negative.
                if (Integer.parseInt(milkWeight) >= 0) {
                    list = data.get(date);
                    for (int i = 0; i < list.size(); i++)       //if farmID and milkWeight match specified
                    {                                          //remove record from arrayList and substitute for the old ArrayList.
                        if (list.get(i).farmID.equals(farmID) && list.get(i).milkWeight == Integer.parseInt(milkWeight)) {
                            list.remove(i);
                            break;
                        }                        
                    }
                    data.put(date, list);
                    return data.get(date);
                } else {
                  throw new Exception("parameters are negative");                    
                }
            }
        } catch (NullPointerException e) {
          throw new Exception("parameters are null");
            
        } catch (NumberFormatException e) {   //Parameters not passed properly
            throw new Exception("Parameters passed as String instead of int or vice versa");
        }
    }

    /**
     * Monthly Report
     * gets number of days in year and formats date in MMDDYYYY format. Then searches those dates
     * for records by assigning the values at each date to 'temp'. If a record if found, it is added to 'list'.
     *
     * @param month
     * @param year
     * @return
     * @throws Exception 
     */
    public ArrayList<Details> monthlyReport(int month, int year) throws Exception {
        ArrayList<Details> list = new ArrayList<Details>();
        ArrayList<Details> temp = new ArrayList<Details>();
        try {
            int numberOfDays = getNumberOfDaysInMonth(month, year);  //get number of days in year.
            for (int i = 1; i <= numberOfDays; i++)  //format list in MMDDYYYY format.
            {
                Date date = new Date(month, i, year);

                try {
                    if (list.size() == 0)           //check if list is empty.
                    {
                        if (data.get(date).size() != 0)  //If yes, add data from date where records are first found to list.
                        {
                            list = data.get(date);
                            continue;
                        }
                    }
                } catch (NullPointerException e)   //Calling .size() or .isEmpty() on an ArrayList with no values could result in NullPointer.
                {
                    continue;
                }
                temp = data.get(date);     //if 'list' isnt empty, assign temp with values associated with 'date' (key).
                try {
                    if (temp.size() == 0)  //check if values exist. If not, move on to next date.
                        continue;
                } catch (NullPointerException e) {
                    continue;
                }

                for (int j = 0; j < temp.size(); j++)   //if values are present.
                {
                    int k = farmIDIndex(temp.get(j).farmID, list);  //check if farmID is present in 'list'
                    if (k == -1)
                        list.add(temp.get(j));  //adds record to list if not present
                    else
                        list.get(k).milkWeight += temp.get(j).milkWeight;  //if present, the milkWeight is updated for that farmID.
                }
            }

            Details key;
            /*Function to sort farmIDs in ArrayList using insertion sort*/
            int n = list.size();
            for (int i = 1; i < n; i++) {
                key = list.get(i);
                int j = i - 1;

               /* Move elements of arr[0..i-1], that are
                  greater than key, to one position ahead
                  of their current position */
                while (j >= 0 && list.get(j).farmID.compareTo(key.farmID) > 0) {
                    list.set(j + 1, list.get(j));
                    j = j - 1;
                }
                list.set(j + 1, key);
            }
            return list;
        } catch (NullPointerException e) {
            throw new Exception("One or more of the parameters are null");
        }

    }

    /**
     * finds farmID index in a list. assumes farmID can't be null.
     * Used in monthly report.
     *
     * @param farmID
     * @param list
     * @return
     */
    private int farmIDIndex(String farmID, ArrayList<Details> list) {

        for (int j = 0; j < list.size(); j++) {
            if (farmID.equals(list.get(j).farmID)) {
                return j; //return index
            }
        }

        return -1;  //return -1 for no ID
    }


    /**
     * This method computes whether the specified year is a leap year or not.
     *
     * @param year is the year that is being checked for leap-year-ness
     * @return true when the specified year is a leap year, and false otherwise
     */
    public static boolean getIsLeapYear(int year) {
        boolean value = true;

        if (year % 4 != 0) {
            value = false;
        } else if (year % 100 != 0) {
            value = true;
        } else if (year % 400 != 0) {
            value = false;
        } else value = true;

        return value;

    }

    /**
     * Converts the name or abbreviation for any month into the index of that
     * month's abbreviation within MONTHS_OF_YEAR. Matches the specified month
     * based only on the first three characters, and is case in-sensitive.
     *
     * @param month which may or may not be abbreviated to 3 or more characters
     * @return the index within MONTHS_OF_YEAR that a match is found at
     * and returns -1, when no match is found
     */
    public static int getMonthIndex(String month) {
        int index = -1;
        for (int i = 0; i < MONTHS_OF_YEAR.length; i++) {
            if (Integer.parseInt(month) == Integer.parseInt(MONTHS_OF_YEAR[i])) {
                index = i;
                break;
            }
        }
        return (index);
    }

    /**
     * Calculates the number of days in the specified month, while taking into
     * consideration whether or not the specified year is a leap year.
     *
     * @param month which may or may not be abbreviated to 3 or more characters
     * @param year  of month that days are being counted for (Gregorian Calendar AD)
     *              String must contain the digits of a single non-negative int for year.
     * @return the number of days in the specified month (between 28-31)
     */
    public static int getNumberOfDaysInMonth(int month, int year) {
        int days[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int indexOfMonth = month - 1;
        if (indexOfMonth == 1 && getIsLeapYear(year)) {
            return 29;
        } else if (indexOfMonth == 1 && !getIsLeapYear(year)) {
            return 28;
        } else return days[indexOfMonth];
    }

    /**
     * Displays total and percent of total for each month of the year.
     *
     * @param farmID
     * @param year
     * @return ArrayList<details>
     * @throws Exception 
     */
    public ArrayList<Details> farmReport(String farmID, String year) throws Exception {
        ArrayList<Details> list = new ArrayList<Details>();
        ArrayList<Details> temp = new ArrayList<Details>();
        try {
            if (farmID.isEmpty() == true)  //check if date is empty
            {
              throw new Exception("FarmID is empty");
            } else if (year.isEmpty() == true)  //check if year is empty
            {
              throw new Exception("Year is empty");
            } else {
                int yearInt = Integer.parseInt(year);

                for (int month = 1; month <= 12; month++) {        //loop through all 12 months
                    int numberOfDays = getNumberOfDaysInMonth(month, yearInt);   //gets no. of days in month for calculating date.
                    for (int day = 1; day <= numberOfDays; day++) {
                        Date date = new Date(month, day, yearInt);
                        temp = data.get(date);      //temp is assigned values associated with date.
                        try {
                            if (temp.size() == 0)      //if values dont exist move on to next date.
                                continue;
                        } catch (NullPointerException e)  //calling .size() could result in NullPointer if values don't exist
                        {
                            continue;
                        }

                        for (int j = 0; j < temp.size(); j++) {
                            if (temp.get(j).farmID.equals(farmID)) //checks if farmID passed as argument matches that in record.
                            {
                                try {
                                    if (list.get(month - 1).month != month) //checks if month associated with previous item in list is not equal to the one being checked right now.
                                    {
                                        temp.get(j).month = month;  //change month field of record being added to its associated month.
                                        list.add(temp.get(j));
                                    } else {
                                        try {
                                            int prevMilkWeight = list.get(month - 1).milkWeight;   //if the month is the same, update milkWeight of previous record (simple addition)
                                            list.set(month - 1, new Details(farmID, Integer.toString(prevMilkWeight + temp.get(j).milkWeight), Integer.toString(month)));
                                        }
                                        /**
                                         * if current month is May, and last record was January. Since the
                                         * records are add to 'list' at index [month - 1], index 4 (May is 5) won't
                                         * exist and we would get an index out of bounds exception. In this case,
                                         * we just add it to the next available index in 'list'.
                                         */ catch (IndexOutOfBoundsException e) {
                                            temp.get(j).month = month;
                                            list.add(temp.get(j));
                                        }
                                    }
                                }
                                /**
                                 * any exception of nullPointer of indexOutOfBounds leads to record being added.
                                 * This is since temp isnt empty. ie. it contains records and the farmID matches that been passed as parameter.
                                 */ catch (NullPointerException e) {
                                    temp.get(j).month = month;
                                    list.add(temp.get(j));
                                } catch (IndexOutOfBoundsException e) {
                                    temp.get(j).month = month;
                                    list.add(temp.get(j));
                                }
                            }
                        }
                    }
                }

                return list;
            }
        } catch (NullPointerException e) {
            throw new Exception("One or more of the parameters are null");
        }
    }

    /**
     * Returns yearly report for all Farms sorted by FarmID.
     * Similar algorithm as farmReport. However we do it for all farms in the FarmID array.
     *
     * @param year
     * @return
     * @throws Exception 
     */

    public ArrayList<Details> annualReport(String year) throws Exception {
        ArrayList<Details> list = new ArrayList<Details>();
        ArrayList<Details> temp = new ArrayList<Details>();
        try {
            if (year.isEmpty() == true)  //check if year is empty
            {
              throw new Exception("Year is empty");
            } else {
                int yearInt = Integer.parseInt(year);

                for (int f = 0; f < farmIDs.size(); f++) {
                    String farmID = farmIDs.get(f);

                    for (int month = 1; month <= 12; month++) {
                        int numberOfDays = getNumberOfDaysInMonth(month, yearInt);
                        for (int day = 1; day <= numberOfDays; day++) {
                            Date date = new Date(month, day, yearInt);
                            temp = data.get(date);
                            try {
                                if (temp.size() == 0)
                                    continue;
                            } catch (NullPointerException e) {
                                continue;
                            }
                            for (int j = 0; j < temp.size(); j++) {
                                if (temp.get(j).farmID.equals(farmID)) {
                                    try {
                                        if (list.get(f) == null) {
                                            list.add(f, temp.get(j));
                                        } else {
                                            try {
                                                int prevMilkWeight = list.get(f).milkWeight;
                                                list.set(f, new Details(farmID, Integer.toString(prevMilkWeight + temp.get(j).milkWeight)));
                                            } catch (IndexOutOfBoundsException e) {
                                                list.add(temp.get(j));
                                            }
                                        }
                                    } catch (NullPointerException e) {
                                        list.add(f, temp.get(j));
                                    } catch (IndexOutOfBoundsException e) {
                                        list.add(f, temp.get(j));
                                    }
                                }
                            }
                        }
                    }
                }

                Details key;
                /*Function to sort array using insertion sort*/
                int n = list.size();
                for (int i = 1; i < n; i++) {
                    key = list.get(i);
                    int j = i - 1;
            
                      /* Move elements of arr[0..i-1], that are 
                         greater than key, to one position ahead 
                         of their current position */
                    while (j >= 0 && list.get(j).farmID.compareTo(key.farmID) > 0) {
                        list.set(j + 1, list.get(j));
                        j = j - 1;
                    }
                    list.set(j + 1, key);
                }

                return list;
            }
        } catch (NullPointerException e) {
            throw new Exception("One or more of the parameters are null");
        }
    }


    /**
     * Returns a date range for all Farms sorted by FarmID.
     * Same algorithm as Annual Report. However, we use a specific date range in this.
     *
     * @param year
     * @return
     * @throws Exception 
     */

    public ArrayList<Details> dateRange(int year, int startMonth, int startDay, int endMonth, int endDay) throws Exception {
        ArrayList<Details> list = new ArrayList<Details>();
        ArrayList<Details> temp = new ArrayList<Details>();

        try {
            for (int f = 0; f < farmIDs.size(); f++) {
                String farmID = farmIDs.get(f);
                for (int month = startMonth; month <= endMonth; month++) {
                    int numberOfDays = getNumberOfDaysInMonth(month, year);
                    if (month == endMonth)
                        numberOfDays = endDay;

                    for (int i = 1; i <= numberOfDays; i++) {
                        Date date = new Date(month, i, year);
                        temp = data.get(date);
                        try {
                            if (temp.size() == 0)
                                continue;
                        } catch (NullPointerException e) {
                            continue;
                        }
                        for (int j = 0; j < temp.size(); j++) {
                            if (temp.get(j).farmID .equals(farmID)) {
                                try {
                                    if (list.get(f) == null) {
                                        list.add(temp.get(j));
                                    } else {
                                        try {
                                            int prevMilkWeight = list.get(f).milkWeight;
                                            list.set(f, new Details(farmID, Integer.toString(prevMilkWeight + temp.get(j).milkWeight)));
                                        } catch (IndexOutOfBoundsException e) {
                                            list.add(temp.get(j));
                                        }
                                    }
                                } catch (NullPointerException | IndexOutOfBoundsException e) {
                                    list.add(temp.get(j));
                                }
                            }
                        }
                    }
                }
            }

            Details key;
            /*Function to sort array using insertion sort*/
            int n = list.size();
            for (int i = 1; i < n; i++) {
                key = list.get(i);
                int j = i - 1;

                  /* Move elements of arr[0..i-1], that are
                     greater than key, to one position ahead
                     of their current position */
                while (j >= 0 && list.get(j).farmID.compareTo(key.farmID) > 0) {
                    list.set(j + 1, list.get(j));
                    j = j - 1;
                }
                list.set(j + 1, key);
            }

            return list;
        } catch (NullPointerException e) {
            throw new Exception("One or more of the parameters are null");    
        }
    }

    public Collection<ArrayList<Details>> viewValues() {
        return data.values();
    }

    public List<Pair<Date, Details>> getAllDetails() {
        int totalSize = 0;
        for (ArrayList<Details> list : data.values())
            totalSize += list.size();

        List<Pair<Date, Details>> allDetails = new ArrayList<>(totalSize);
        for (Map.Entry<Date, ArrayList<Details>> entry : data.entrySet()) {
            Date date = entry.getKey();
            ArrayList<Details> detailsList = entry.getValue();
            for (Details details : detailsList)
                allDetails.add(new Pair<>(date, details));
        }

        return allDetails;
    }

    public ArrayList<String> getFarmIDs() {
        return farmIDs;
    }
    
    public Map<Date, ArrayList<Farm.Details>> returnMap(){
      return data;
    }
    
}