package kgk.mobile.external.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateFormatter {

    public String unixSecondsToFormattedString(long unixSeconds, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ROOT);
        Date date = new Date(unixSeconds * 1000);
        return dateFormat.format(date);
    }
}
