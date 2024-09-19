package com.api.invoicer.model.excel;

import lombok.Builder;

@Builder
public record Total(Price price,
                    String standardTax,
                    String tax,
                    Price sum) {
}
