package com.shopping.electroshopping.service.OrderService;

import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.user.User;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

public interface OrderService {

    void saveOrder(String paymentMethod, Principal principal);


    void decresethQuantity(User user);

    void increaseQuantity(Order order);

    public List<Order> getOrdersByUserId(Long userId);

    public void editOrderStatus(Long orderId, Order order);

    public void cancelOrder(Long id);

    void deleteProductById(Long id);
}
