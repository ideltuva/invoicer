package com.api.invoicer.model.excel;

import lombok.Builder;

import java.util.Objects;

@Builder
public record Price(String value,
                    String currency) {
    public Price {
        if(Objects.isNull(currency)) currency = "EUR";
    }
}
