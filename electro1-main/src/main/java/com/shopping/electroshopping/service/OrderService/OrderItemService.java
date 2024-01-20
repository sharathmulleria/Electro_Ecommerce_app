package com.shopping.electroshopping.service.OrderService;

import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.model.user.User;

import java.util.List;

public interface OrderItemService {

    void saveOrderItems(List<CartItem> cartItems, Order order);

}
