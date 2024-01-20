package com.shopping.electroshopping.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SalesReportDTO {

    private Long orderId;

    private String productName;

    private double totalSales;

    private LocalDate orderDate;

    private double orderTotal;





}
