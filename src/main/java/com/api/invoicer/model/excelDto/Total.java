package com.api.invoicer.model.excelDto;

import lombok.Builder;

@Builder
public record Total(Price price,
                    String standardTax,
                    String tax,
                    Price sum) {
}
