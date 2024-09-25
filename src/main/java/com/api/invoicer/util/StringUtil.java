package com.api.invoicer.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.api.invoicer.constant.FileConstant.EXCEL_SUFFIX_XLSM;
import static com.api.invoicer.constant.FileConstant.EXCEL_SUFFIX_XLSX;

@UtilityClass
public class StringUtil {
    private final String TELEPHONE_REGEX = "(?<=\\(Tel\\. )\\+?\\d+(?=\\))";

    public static String extractTelephone(String text) {
        final Pattern pattern = Pattern.compile(TELEPHONE_REGEX);
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()) return matcher.group();
        return "";
    }
    public static String removeTelephone(String text) {
        final String telephoneFragment = String.format(" %s %s%s", "(Tel.", extractTelephone(text), ")");
        return text.replace(telephoneFragment, "");
    }

    public static String trimFileSuffix(String fileName) {
        return endsWithXlsx(fileName) ? fileName.replace(EXCEL_SUFFIX_XLSX, "") :
                fileName.replace(EXCEL_SUFFIX_XLSM, "");
    }

    private static boolean endsWithXlsx(String fileName) {
        return fileName.endsWith(EXCEL_SUFFIX_XLSX);
    }
}
