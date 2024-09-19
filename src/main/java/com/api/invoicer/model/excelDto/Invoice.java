package com.api.invoicer.model.excel;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record Invoice(String serialNumber,
                      String orderNumber,
                      SellerInfo sellerInfo,
                      LocalDate created,
                      BuyerInfo buyerInfo,
                      List<Item> items,
                      Total total,
                      AdditionalInfo additionalInfo,
                      String remarks,

                      LocalDate dueDate,
                      String responsible) {
}
