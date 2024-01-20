package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.Order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT o FROM orders o WHERE o.user.id = :userId")
    List<Order> findOrdersByUserId(@Param("userId") Long user_id);

    List<Order> findByUserEmail(String email);

//    @Query("SELECT o FROM Order o WHERE o.OrderDate BETWEEN :startDate AND :endDate")
//    List<Order> findByOrderDatesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM orders o WHERE o.ordered_date BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDatesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
