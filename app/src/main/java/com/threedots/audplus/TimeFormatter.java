package com.threedots.audplus;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {
    public static String getTime(long time) {
        Date now = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - time);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - time);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - time);

        if (seconds < 60) {
            return "Just Now";
        } else if (minutes == 1) {
            return "Just a minute ago";
        } else if (minutes < 60) {
            return minutes + " minutes ago.";
        } else if (hours == 1) {
            return "An hour ago.";
        } else if (hours < 24) {
            return hours + " hours ago.";
        } else if (days == 1) {
            return "A day ago.";
        } else {
            return days + " day ago.";
        }


    }
}
