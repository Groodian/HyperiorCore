package de.groodian.hyperiorcore.util;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.Date;

public class Time {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public static String timeLeftString(Temporal startInclusive, Temporal endExclusive) {
        return durationString(Duration.between(startInclusive, endExclusive));
    }

    public static String durationString(Duration duration) {
        String timeLeft = "";

        if (duration.toDays() > 1) {
            timeLeft = duration.toDays() + " Tage";
        } else if (duration.toDays() == 1) {
            timeLeft = duration.toDays() + " Tag";
        } else if (duration.toHours() > 1) {
            timeLeft = duration.toHours() + " Stunden";
        } else if (duration.toHours() == 1) {
            timeLeft = duration.toHours() + " Stunde";
        } else if (duration.toMinutes() > 1) {
            timeLeft = duration.toMinutes() + " Minuten";
        } else if (duration.toMinutes() == 1) {
            timeLeft = duration.toMinutes() + " Minute";
        } else if (duration.getSeconds() > 1 || duration.getSeconds() == 0) {
            timeLeft = duration.getSeconds() + " Sekunden";
        } else if (duration.getSeconds() == 1) {
            timeLeft = duration.getSeconds() + " Sekunde";
        }

        return timeLeft;
    }

    public static String durationStringNoDay(Duration duration) {
        String timeLeft = "";

        if (duration.toHours() > 1) {
            timeLeft = duration.toHours() + " Stunden";
        } else if (duration.toHours() == 1) {
            timeLeft = duration.toHours() + " Stunde";
        } else if (duration.toMinutes() > 1) {
            timeLeft = duration.toMinutes() + " Minuten";
        } else if (duration.toMinutes() == 1) {
            timeLeft = duration.toMinutes() + " Minute";
        } else if (duration.getSeconds() > 1 || duration.getSeconds() == 0) {
            timeLeft = duration.getSeconds() + " Sekunden";
        } else if (duration.getSeconds() == 1) {
            timeLeft = duration.getSeconds() + " Sekunde";
        }

        return timeLeft;
    }

    public static String formatDate(OffsetDateTime offsetDateTime) {
        return dateFormatter.format(Date.from(offsetDateTime.toInstant()));
    }

}
