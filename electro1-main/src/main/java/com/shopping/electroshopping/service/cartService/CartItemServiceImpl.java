package com.shopping.electroshopping.service.cartService;

import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.repository.CartItemRepository;
import com.shopping.electroshopping.repository.CartRepository;
import com.shopping.electroshopping.repository.ProductRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Getter
    private List<CartItem> cartItems = new ArrayList<>();

    public void updateCartItemQuantity(Long cartItemId, int quantity) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    public void saveToCart(Cart existingCart, Product existingProduct) {

        CartItem newCartItem = new CartItem();
        newCartItem.setCart(existingCart);
        newCartItem.setQuantity(1);
        newCartItem.setProduct(existingProduct);
        newCartItem.setPrice((long) existingProduct.getPrice());
        cartItemRepository.save(newCartItem);

    }

    // Decrease Stock after checkout complete.
    @Override
    public void decreaseStock(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems){
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(UnsupportedOperationException::new);
            int quantityToDecrease = cartItem.getQuantity();
            int currentStock = product.getStock();

            if (currentStock >= quantityToDecrease){
                product.setStock(currentStock-quantityToDecrease);
                productRepository.save(product);
            }
        }
    }


}









