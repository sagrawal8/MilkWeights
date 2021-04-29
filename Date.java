package application;

import java.util.Objects;

/**
 * Represents a simple date with a month, day, and year.
 */
public class Date {
    private int day;
    private int month;
    private int year;

    public Date(int month, int day, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    /**
     * Returns this date in the YYYYMD format -- e.g. "2019-5-4" for May 4, 2019.
     * @return this date in the YYYYMD format
     */
    public String toYYYYMD() {
        return year + "-" + month + "-" + day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return day == date.day &&
                month == date.month &&
                year == date.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

    /**
     * Returns the date from the format "YYYY-M-D", e.g. "2019-5-4" for May 4, 2019. This is the format used in the CSV
     * files.
     * @param dateStr the date string
     * @return a date object from the string
     */
    public static Date fromYYYYMD(String dateStr) {
        String[] split = dateStr.split("-");
        if (split.length != 3)
            throw new RuntimeException("Date is in invalid format");

        int year;
        int month;
        int day;
        try {
            year = Integer.parseInt(split[0]);
            month = Integer.parseInt(split[1]);
            day = Integer.parseInt(split[2]);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Date is in invalid format", ex);
        }

        return new Date(month, day, year);
    }
}
