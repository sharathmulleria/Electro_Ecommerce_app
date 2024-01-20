package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

//    @Query("SELECT w FROM Wallet w JOIN w.user u WHERE u.email = :email")
//    Wallet findWalletByUserEmail(@Param("email") String email);

    public Wallet findWalletByUserEmail(String email);

}
