package com.shopping.electroshopping.repository;


import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUser(User user);

    @Query("SELECT c FROM Cart c JOIN c.user u WHERE u.id = :userId")
    Cart findCartByUserId(@Param("userId") Long userId);

}
