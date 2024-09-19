package com.api.invoicer.model.excelDto;

import lombok.Builder;

@Builder
public record Item(String service,
                   String description,
                   String quantity,
                   Price price,
                   Price sum) {
}
