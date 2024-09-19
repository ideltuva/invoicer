package com.api.invoicer.model.excel;

import lombok.Builder;

@Builder
public record AdditionalInfo(String bankName,
                             String accountNumber) {
}
