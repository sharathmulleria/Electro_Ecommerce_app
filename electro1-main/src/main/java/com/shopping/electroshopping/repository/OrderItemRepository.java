package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrder_Id(Long id);


    @Query("SELECT oi.product.id FROM OrderItem oi WHERE oi.order.id = orderId")
    public List<Long> findProductIdsByOrderId(@Param("orderId") Long orderId);


    List<OrderItem> findByOrderUser(User user);

}
