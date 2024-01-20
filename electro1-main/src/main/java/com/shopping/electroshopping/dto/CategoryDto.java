package com.shopping.electroshopping.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    @Setter
    @Getter
    private String name;

    private String description;

    private boolean deleted;

}
