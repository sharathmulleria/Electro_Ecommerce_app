package com.shopping.electroshopping.model.Order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopping.electroshopping.Enums.Payment;
import com.shopping.electroshopping.Enums.Status;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.user.UserAddress;
import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ToString.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "address_id")
    private UserAddress address;

    Payment payment;

    Status status;

    LocalDate ordered_date;

    LocalDate expecting_date;

    LocalDate shipping_date;

    private String cancellationReason;

    private float total;



}