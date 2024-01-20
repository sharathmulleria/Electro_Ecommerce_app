package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.wallet.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {

@Query("SELECT wh FROM WalletHistory wh "+
        "JOIN wh.wallet w "+
        "JOIN w.user u "+
        "WHERE u.email = :userEmail")
    List<WalletHistory> findWalletHistoryByEmail(@Param("userEmail") String userEmail);


}
