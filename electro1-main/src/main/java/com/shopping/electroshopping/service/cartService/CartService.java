package com.shopping.electroshopping.service.cartService;

import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;

import java.util.List;

public interface CartService {
    public void deleteCartItemsById(Long cartItemid);

    List<CartItem> getCartItemByCart(Cart cart);


}
