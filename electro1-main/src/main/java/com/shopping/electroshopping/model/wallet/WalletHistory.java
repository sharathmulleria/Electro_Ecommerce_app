package com.shopping.electroshopping.model.wallet;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class WalletHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Transactions Transaction;

    private LocalDate TransactionDate;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "Wallet_id")
    private Wallet wallet;



}
