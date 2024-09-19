package com.api.invoicer.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class AddressUtil {
    private static final Pattern POSTCODE_PATTERN = Pattern.compile("LT-\\d{5}|\\b\\d{5}\\b");

    public static String getPostalCode(final String address) {
        Matcher matcher = POSTCODE_PATTERN.matcher(address);
        if(matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }
}
