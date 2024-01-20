package com.shopping.electroshopping.model.wallet;

import com.shopping.electroshopping.model.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "wallet")
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double balance;


}
