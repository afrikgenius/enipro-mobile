package com.enipro.model;


import java.security.InvalidParameterException;

public class DateTimeStringProcessor {

    /**
     * Processes the local date instance passed and returns a string representing
     * the date time string of an item from the current time.
     * @param localDate
     * @return
     */
    public static String process(org.joda.time.LocalDateTime localDate) throws InvalidParameterException{
        // Date part of string
        int year = localDate.getYear();
        StringBuilder date = new StringBuilder(localDate.getDayOfMonth() + " " + getMonthString(localDate.getMonthOfYear()));

        // Time part of string
        String time = localDate.getHourOfDay() + ":" + localDate.getMinuteOfHour();
        if(year != org.joda.time.LocalDateTime.now().getYear())
            date.append(" " + year);

        date.append(" " + time);
        return date.toString();
    }

    /**
     * Returns the string of a month given the number.
     * @param month
     * @return
     */
    private static String getMonthString(int month) throws InvalidParameterException {
        // Perform check on month.
        if(month < 1 || month > 12)
            throw new InvalidParameterException("Number must be between 1 and 12");

        String monthString;
        switch (month) {
            case 1:  monthString = "January";       break;
            case 2:  monthString = "February";      break;
            case 3:  monthString = "March";         break;
            case 4:  monthString = "April";         break;
            case 5:  monthString = "May";           break;
            case 6:  monthString = "June";          break;
            case 7:  monthString = "July";          break;
            case 8:  monthString = "August";        break;
            case 9:  monthString = "September";     break;
            case 10: monthString = "October";       break;
            case 11: monthString = "November";      break;
            case 12: monthString = "December";      break;
            default: monthString = "Invalid month"; break;
        }
        return monthString;
    }
}