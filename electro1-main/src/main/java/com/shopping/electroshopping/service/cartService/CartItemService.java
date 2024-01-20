package com.shopping.electroshopping.service.cartService;

import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CartItemService {

    void saveToCart(Cart existingCart, Product existingProduct);

    void decreaseStock(List<CartItem> cartItems);
}
