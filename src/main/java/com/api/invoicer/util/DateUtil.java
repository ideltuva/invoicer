package com.api.invoicer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class DateUtil {

    private final String LITHUANIAN_DATE_FORMAT = "y 'm'. MMMM d 'd'.";
    private final String DATE_SLASHES_FORMAT = "M/dd/yy";
    private final String DATE_DASHES_FORMAT = "yyyy-MM-dd";
    private final String DATE_REGEX_WITH_SLASHES = "\\d{1,2}/\\d{2}/\\d{2}";
    private final String DATE_REGEX_WITH_DASHES = "\\d{4}-\\d{2}-\\d{2}";

    public static LocalDate getLocaleDate(String date) {
        if (isValidLithuanianDateFormat(date)) return getLocalDateForLithuanianDate(date);
        if (dateWithSlashes(date)) return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_SLASHES_FORMAT));
        if (dateWithDashes(date)) return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_DASHES_FORMAT));
        log.error("Please check the date format, it does not follow any pattern: {}", date);
        return null;
    }

    public static boolean dateWithSlashes(String date) {
        final Pattern pattern = Pattern.compile(DATE_REGEX_WITH_SLASHES);
        Matcher matcher = pattern.matcher(date);
        return matcher.find();
    }

    public static boolean dateWithDashes(String date) {
        final Pattern pattern = Pattern.compile(DATE_REGEX_WITH_DASHES);
        Matcher matcher = pattern.matcher(date);
        return matcher.find();
    }


    public static boolean isValidLithuanianDateFormat(String value) {
        LocalDateTime ldt;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LITHUANIAN_DATE_FORMAT, getLithuanianLocale());
        try {
            ldt = LocalDateTime.parse(value, formatter);
            String result = ldt.format(formatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(value, formatter);
                String result = ld.format(formatter);
                return result.equals(value);
            } catch (DateTimeParseException exp) {
                try {
                    LocalTime lt = LocalTime.parse(value, formatter);
                    String result = lt.format(formatter);
                    return result.equals(value);
                } catch (DateTimeParseException ignored) {
                }
            }
        }
        return false;
    }

    private static Locale getLithuanianLocale() {
        return new Locale.Builder()
                .setLanguage("lt")
                .setRegion("LT")
                .build();
    }

    private static LocalDate getLocalDateForLithuanianDate(String created) {
        DateFormat dateFormat = new SimpleDateFormat(LITHUANIAN_DATE_FORMAT, getLithuanianLocale());
        try {
            Date date = dateFormat.parse(created);
            SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_DASHES_FORMAT);
            return LocalDate.parse(dateFormatter.format(date), DateTimeFormatter.ofPattern(DATE_DASHES_FORMAT));
        }
        catch (Exception ignored) { }
        return null;
    }
}
