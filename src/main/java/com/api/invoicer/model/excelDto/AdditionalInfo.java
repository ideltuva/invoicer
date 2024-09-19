package com.api.invoicer.model.excelDto;

import lombok.Builder;

@Builder
public record AdditionalInfo(String bankName,
                             String accountNumber) {
}
