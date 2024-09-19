package com.api.invoicer.model.excelDto;

import lombok.Builder;

@Builder
public record SellerInfo(String clientName,
                         String clientId,
                         String taxPayerId,
                         Address address,
                         String registrationNumber) {
}
