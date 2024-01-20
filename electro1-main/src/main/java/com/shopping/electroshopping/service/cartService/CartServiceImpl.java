package com.shopping.electroshopping.service.cartService;

import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.repository.CartItemRepository;
import com.shopping.electroshopping.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public void deleteCartItemsById(Long cartItemid) {
        this.cartItemRepository.deleteById(cartItemid);
    }

    @Override
    public List<CartItem> getCartItemByCart(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }


}
