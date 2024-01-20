package com.shopping.electroshopping.model.user;

import javax.persistence.*;

import lombok.*;

import java.util.Arrays;

@Getter
@Setter
@Data
@ToString
@Entity
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAddressID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String streetDetails;

    private String cityName;

    private String state;

    private String postalCode;

    private String addressPhoneNumber;

    private boolean isDefault;

}
