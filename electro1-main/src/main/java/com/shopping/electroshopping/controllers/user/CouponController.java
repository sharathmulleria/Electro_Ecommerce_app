package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.coupon.Coupon;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.user.UserAddress;
import com.shopping.electroshopping.repository.CartItemRepository;
import com.shopping.electroshopping.repository.CartRepository;
import com.shopping.electroshopping.repository.CouponRepository;
import com.shopping.electroshopping.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class CouponController {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;


    @PostMapping("/user/applyCoupon")
    public String applyCoupon(@ModelAttribute("cart")Cart cart, @RequestParam("couponCode") String couponCode, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        Coupon coupon = couponRepository.findByCode(couponCode);
        Cart newCart = cartRepository.findCartByUserId(user.getId());

        if (couponCode==null){
            model.addAttribute("couponError", "Please Enter a Valid Coupon Code");
            double totalPrice = cartItemRepository.sumCartItemsPriceByUser(user);

            double totalAmount = newCart.getTotal();
            List<UserAddress> addressList = user.getAddresses();
            List<CartItem> cartItems = cartItemRepository.findByCartUser(user);
            Cart userCart = cartRepository.findByUser(user);
            userCart.setTotal(totalPrice);
            cartRepository.save(userCart);
            model.addAttribute("addresses", addressList);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("cartItems", cartItems);
            return "redirect:/user/checkout";
        }

        String expirationDateStr = coupon.getExpirationDate();
        Date expirationDate = null;
        if (expirationDateStr != null && !expirationDateStr.isEmpty()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                expirationDate = dateFormat.parse(expirationDateStr);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }

        Date currentDate = new Date();
        if (expirationDate != null && currentDate.after(expirationDate)){
            model.addAttribute("couponError", "Coupon has expired");
            return "redirect:/user/checkout";
        }

        double discountPercentage = coupon.getDiscountPercentage();
//        double totalPrice = cart.getTotal();
        double totalAmount = newCart.getTotal();
        double newTotal = totalAmount * ( 1 - discountPercentage / 100);
        List<UserAddress> addressList = user.getAddresses();
        List<CartItem> cartItems = cartItemRepository.findByCartUser(user);
        Cart userCart = cartRepository.findByUser(user);
        userCart.setTotal(newTotal);
        cartRepository.save(userCart);
        model.addAttribute("addresses", addressList);
        model.addAttribute("totalPrice", newTotal);
        model.addAttribute("cartItems", cartItems);
        return "redirect:/user/checkout";
    }

    @GetMapping("/user/couponAdd")
    public String userCouponAdd(){
        return "/coupon/userCoupon";
    }

}
