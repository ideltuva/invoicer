package com.api.invoicer.model.excelDto;

import lombok.Builder;

@Builder
public record BuyerInfo(String clientName,
                        Address address,
                        String taxPayerId,
                        String clientId) {
}
