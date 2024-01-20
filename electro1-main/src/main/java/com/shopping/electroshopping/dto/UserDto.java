package com.shopping.electroshopping.dto;

import com.shopping.electroshopping.model.user.UserAddress;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class UserDto {

    private UserAddress defaultAddress;

}
