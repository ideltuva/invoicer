package com.api.invoicer.model.excel;

import lombok.Builder;

@Builder
public record SellerInfo(String clientName,
                         String clientId,
                         String taxPayerId,
                         Address address,
                         String registrationNumber) {
}
