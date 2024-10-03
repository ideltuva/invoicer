package com.api.invoicer.util;

import org.junit.Assert;
import org.junit.Test;

import static com.api.invoicer.util.AddressUtil.getPostalCode;

public class AddressUtilTest {

    private final String FULL_ADDRESS = "Studentų g. 11, LT-53356 Akademija, Kauno r.";
    private final String FULL_ADDRESS_NO_PREFIX = "Studentų g. 11, 53356 Akademija, Kauno r.";
    private final String POSTAL_CODE = "LT-53356";
    private final String POSTAL_CODE_NO_PREFIX = "53356";


    @Test
    public void shouldFindPostalAddress() {
        final String extractedPostalCode = getPostalCode(FULL_ADDRESS);
        Assert.assertEquals(POSTAL_CODE, extractedPostalCode);
    }

    @Test
    public void shouldFindPostalAddressWithNoPrefix() {
        final String extractedPostalCode = getPostalCode(FULL_ADDRESS_NO_PREFIX);
        Assert.assertEquals(POSTAL_CODE_NO_PREFIX, extractedPostalCode);
    }
}
