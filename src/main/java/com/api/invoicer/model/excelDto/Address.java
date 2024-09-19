package com.api.invoicer.model.excelDto;

import lombok.Builder;

@Builder
public record Address(String fullAddress,
                      String streetName,
                      String city,
                      String postalCode) {
}
