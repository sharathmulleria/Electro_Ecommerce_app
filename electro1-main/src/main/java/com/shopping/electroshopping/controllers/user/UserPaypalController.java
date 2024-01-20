package com.shopping.electroshopping.controllers.user;

import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;
import com.shopping.electroshopping.Enums.Status;
import com.shopping.electroshopping.dto.OrderDtoPaypal;
import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.repository.*;
import com.shopping.electroshopping.service.OrderService.OrderService;
import com.shopping.electroshopping.service.PaymentService.PaypalService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.paypal.api.payments.Payment;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class UserPaypalController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    PaypalService paypalService;

    @Autowired
    OrderService orderService;

    @Autowired


    public static final String SUCCESS_URL = "pay/success";
    public static final String CANCEL_URL = "pay/cancel";


    @GetMapping("/user/home")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        Cart userCart = cartRepository.findByUser(user);
        List<CartItem> cartItems = cartItemRepository.findByCartUser(user);


        if (userCart != null) {
            double total = userCart.getTotal();
            model.addAttribute("amount", total);
            model.addAttribute("cartItems", cartItems);
        }
        return "/PaymentSuccess/payment";
    }


    @PostMapping("/pay")
    public String payment(@ModelAttribute("order") OrderDtoPaypal orderDtoPaypal) throws PayPalRESTException {
        orderDtoPaypal.setIntend("sale");

        Payment payment = paypalService.createPayment(orderDtoPaypal.getPrice(), orderDtoPaypal.getCurrency()
                , orderDtoPaypal.getMethod(), orderDtoPaypal.getIntend()
                , orderDtoPaypal.getDescription(), "http://localhost:8080/" + CANCEL_URL,
                "http://localhost:8080/" + SUCCESS_URL);

        for (Links link : payment.getLinks()) {
            if (link.getRel().equals("approval_url")) {
                return "redirect:" + link.getHref();
            }
        }

        return "redirect:/";
    }


    @GetMapping(value = CANCEL_URL)
    public String cancelPay(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("not", "payment not successful");
        return "redirect:/payment/failure";
    }


    @GetMapping("/pay/success")
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, Principal principal) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                String paymentMethod = "Paypal";
                orderService.saveOrder(paymentMethod, principal);
                cartItemRepository.deleteAll();

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                User user = userRepository.findByEmail(email);
                Order order = orderRepository.getReferenceById(user.getId());
                orderService.decresethQuantity(user);
                return "/payment/paymentSuccess";
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/";
    }

}
