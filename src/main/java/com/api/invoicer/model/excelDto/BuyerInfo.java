package com.api.invoicer.model.excel;

import lombok.Builder;

@Builder
public record BuyerInfo(String clientName,
                        Address address,
                        String taxPayerId,
                        String clientId) {
}
