package com.shopping.electroshopping.model.product;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.model.category.Category;

import com.shopping.electroshopping.model.offer.ProductOffer;
import lombok.*;
import org.hibernate.annotations.Formula;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @ToString.Exclude
    private Category category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // use mappedBy here
    @ToString.Exclude
    private ProductOffer activeOffer;

    @Column(nullable = false)
    private String imageName;

    private Boolean isOffer;

    private int stock;

    private int cost;

    @Column(name="deleted", nullable = false)
    private boolean deleted = false;

    private Double discountedPrice;

//    private boolean isStock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderItem> orderItems;

    public Double getDiscountedPrice() {
        if (this.activeOffer != null && this.activeOffer.getDiscountPercentage() > 0) {
            return this.price - (this.price * this.activeOffer.getDiscountPercentage() / 100);
        } else {
            return this.price; // return original price if no active offer or 0% discount
        }
    }

}

