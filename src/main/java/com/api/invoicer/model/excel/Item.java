package com.api.invoicer.model.excel;

import lombok.Builder;

@Builder
public record Item(String service,
                   String description,
                   String quantity,
                   Price price,
                   Price sum) {
}
