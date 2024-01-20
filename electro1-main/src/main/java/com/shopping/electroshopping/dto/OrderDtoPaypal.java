package com.shopping.electroshopping.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class OrderDtoPaypal {

    private Double price;
    private String currency;
    private String method;
    private String intend;
    private String description;

}
