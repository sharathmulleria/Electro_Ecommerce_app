package com.shopping.electroshopping.model.category;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long Id;

    @Column(name = "name")
    private String name;

    private boolean isAvailable;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "deleted")
    private boolean deleted=false;

}
