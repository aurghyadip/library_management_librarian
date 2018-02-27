package com.aurghyadip.librarymanagementlibrarian;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/* Support Class for RentActivity and DepositActivity */
public class RentDeposit {

    private Date today;

    public RentDeposit() {
        today = new Date();
    }

    // Get current date string from simple date format
    public String getCurrentDateString() {
        SimpleDateFormat formatDate = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.UK);
        return formatDate.format(today.getTime());
    }

    // Calculate the difference between two days
    private int dateDiffCalculator(String rentDate) {
        long difference = 0;
        SimpleDateFormat formatDate = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.UK);
        try {
            Date rent = formatDate.parse(rentDate);
            Date deposit = formatDate.parse(getCurrentDateString());
            long duration = deposit.getTime() - rent.getTime();
            difference = TimeUnit.MILLISECONDS.toDays(duration);
            if(difference < 0) {
                difference = 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) difference;
    }

    // Calculate the fine based on a slab structure
    public int getFine(String date1) {
        int fine;
        int diff = dateDiffCalculator(date1);

        if(diff < 5) {
            fine = 0;
        } else if(diff > 5 && diff < 10) {
            fine = diff * 2;
        } else if(diff > 10 && diff < 20) {
            fine = diff * 5;
        }
        else {
            fine = 100;
        }
        return fine;
    }
}
