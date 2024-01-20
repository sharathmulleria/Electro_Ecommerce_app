package com.shopping.electroshopping.controllers.admin;

import com.shopping.electroshopping.Enums.Status;
import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.repository.OrderItemRepository;
import com.shopping.electroshopping.repository.OrderRepository;
import com.shopping.electroshopping.service.OrderService.OrderService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {


    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderService orderService;



    //List all Order List in Admin Order Tab
    @GetMapping("/orderList")
    public String updateOrder(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orderList", orders);
        return "/order/admin-order-manage";
    }


    //Show Order status page for Updating
    @GetMapping("/updateOrder/{id}")
    public String updateOrder(@PathVariable("id") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + orderId));
        model.addAttribute("order", order);
        model.addAttribute("orderId", orderId);
        return "/order/updateOrderStatus";
    }



    //Update user Order status PostMethod
    @PostMapping("/updateOrder/{id}")
    public String updateStatus(@PathVariable("id") Long orderId, @ModelAttribute("order") Order order
                                                                , RedirectAttributes redirectAttributes) {
        Optional<Order> orders = orderRepository.findById(orderId);
        if (orders.isPresent()) {
            Order existingOrder = orders.get();
            existingOrder.setStatus(Status.valueOf(String.valueOf(order.getStatus())));
            orderRepository.save(existingOrder);
            return "redirect:/admin/orderList";
        } else {
            redirectAttributes.addFlashAttribute("hi", "Order Status Updated");
            return "redirect:/admin/orderList";
        }
    }








}
