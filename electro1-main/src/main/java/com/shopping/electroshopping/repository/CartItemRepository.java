package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartUser(User user);

    @Query("SELECT COALESCE(SUM(ci.price * ci.quantity), 0.0) FROM CartItem ci WHERE ci.cart.user=:user")
    double sumCartItemsPriceByUser(com.shopping.electroshopping.model.user.User user);

    CartItem findByCartAndProduct(Cart existingCart, Product existingProduct);

    List<CartItem> findByCart(Cart cart);


}
