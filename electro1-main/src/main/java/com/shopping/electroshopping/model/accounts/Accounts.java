package com.shopping.electroshopping.model.accounts;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Double totalSale;

    private Double totalCost;

    private Double todaySale;

    private Double totalProfit;

    private int totalOrders;

    private  float totalAmount;


    public Accounts(int ordersTotal, float totalAmount) {
    }
}
