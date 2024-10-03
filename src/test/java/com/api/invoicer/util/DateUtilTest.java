package com.api.invoicer.util;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static com.api.invoicer.util.DateUtil.getLocaleDate;
import static com.api.invoicer.util.DateUtil.isValidLithuanianDateFormat;

public class DateUtilTest {

    private final String LT_DATE = "2024 m. rugsėjo 23 d.";
    private final String DASHED_DATE = "2024-09-23";
    private final String FORWARD_SLASHED_DATE = "9/23/24";

    @Test
    public void shouldValidateLtDate() {
        Assert.assertTrue(isValidLithuanianDateFormat(LT_DATE));
    }

    @Test
    public void shouldNotValidateLtDate() {
        Assert.assertFalse(isValidLithuanianDateFormat(DASHED_DATE));
    }

    @Test
    public void shouldFormatLtDate() {
        final LocalDate date = getLocaleDate(LT_DATE);
        assert date != null;
        Assert.assertEquals(DASHED_DATE, date.toString());
    }

    @Test
    public void shouldFormatDate() {
        final LocalDate date = getLocaleDate(FORWARD_SLASHED_DATE);
        assert date != null;
        Assert.assertEquals(DASHED_DATE, date.toString());
    }
}
