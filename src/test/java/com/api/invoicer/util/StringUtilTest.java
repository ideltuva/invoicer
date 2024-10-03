package com.api.invoicer.util;

import org.junit.Assert;
import org.junit.Test;

import static com.api.invoicer.util.StringUtil.extractTelephone;
import static com.api.invoicer.util.StringUtil.removeTelephone;

public class StringUtilTest {
    private final String REMARK_INFO = "Vyr. Buhalteris TadasTadas (Tel. 860000008)";
    private final String REMARK_INFO_TELEPHONE_REMOVED = "Vyr. Buhalteris TadasTadas";
    private final String REMARK_INFO_WITH_PLUS = "Vyr. Buhalteris TadasTadas (Tel. +37060000008)";
    private final String TELEPHONE = "860000008";
    private final String TELEPHONE_WITH_PLUS = "+37060000008";

    @Test
    public void shouldExtractTelephone() {
        final String telephone = extractTelephone(REMARK_INFO);
        Assert.assertEquals(TELEPHONE, telephone);
    }

    @Test
    public void shouldExtractTelephoneWithPlus() {
        final String telephone = extractTelephone(REMARK_INFO_WITH_PLUS);
        Assert.assertEquals(TELEPHONE_WITH_PLUS, telephone);
    }

    @Test
    public void shouldRemoveTelephone() {
        final String removedTelephone = removeTelephone(REMARK_INFO);
        Assert.assertEquals(REMARK_INFO_TELEPHONE_REMOVED, removedTelephone);
    }
}
