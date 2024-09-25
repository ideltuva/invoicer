package com.api.invoicer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class DateUtil {

    private final String DATE_SLASHES_FORMAT = "M/dd/yy";
    private final String DATE_DASHES_FORMAT = "yyyy-MM-dd";
    private final String DATE_REGEX_WITH_SLASHES = "\\d{1,2}/\\d{2}/\\d{2}";
    private final String DATE_REGEX_WITH_DASHES = "\\d{4}-\\d{2}-\\d{2}";

    public static LocalDate getLocaleDate(String date) {
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

}
