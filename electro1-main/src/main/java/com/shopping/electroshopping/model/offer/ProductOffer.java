package com.shopping.electroshopping.model.offer;

import com.shopping.electroshopping.model.product.Product;
import lombok.*;

import javax.persistence.*;

@Entity
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "product_offer")
public class ProductOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productOfferId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String description;

    private double discountPercentage;

    private String expirationDate;

    private String createDate;



}
