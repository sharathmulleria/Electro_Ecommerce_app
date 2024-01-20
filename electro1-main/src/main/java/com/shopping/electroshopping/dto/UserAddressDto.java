package com.shopping.electroshopping.dto;

import lombok.*;

import java.util.Hashtable;

@Data
@Getter
@Setter
public class UserAddressDto {

    private  Long user_id;

    @Getter
    private String streetDetails;

    @Getter
    private String cityName;

    private String state;

    private String postalCode;

    private String addressPhoneNumber;

    private boolean isDefault;

}
