package com.api.invoicer.model.excel;

import lombok.Builder;

@Builder
public record Address(String fullAddress,
                      String streetName,
                      String city,
                      String postalCode) {
}
