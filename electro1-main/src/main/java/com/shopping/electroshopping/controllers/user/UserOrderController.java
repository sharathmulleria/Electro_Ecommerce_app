package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.repository.*;
import com.shopping.electroshopping.service.OrderService.OrderService;
import com.shopping.electroshopping.service.basicService.BasicServiceImpl;
import com.shopping.electroshopping.service.userservice.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Array;
import java.security.Principal;
import java.util.*;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/user")
public class UserOrderController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BasicServiceImpl basicService;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartRepository cartRepository;


//User Side Order History Controller for View All Order by User Made.
    @GetMapping("/orderHistory/{user_id}")
    public String orderHistory(@PathVariable("user_id") Long user_id,
                               Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        List<Order> orderList = orderRepository.findByUserEmail(email);

        Cart userCart = cartRepository.findByUser(user);
        double newTotal = userCart.getTotal();
        model.addAttribute("total", newTotal);
        model.addAttribute("orderList", orderList);
        return "order/orderHistory";
    }


//User Side Order Tracker for User can get Status of Order
    @GetMapping("/trackOrder")
    public String trackUserOrder(Model model) {

        model.addAttribute("");


        return "/order/trackOrder";
    }


    @GetMapping("cancelOrder/{id}")
    public String cancellOrder(@PathVariable("id") Long id){
        orderService.cancelOrder(id);
        return "redirect:/user/orderHistory/"+id;
    }






}
