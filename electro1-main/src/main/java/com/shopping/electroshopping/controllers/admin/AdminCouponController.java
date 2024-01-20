package com.shopping.electroshopping.controllers.admin;

import com.shopping.electroshopping.model.coupon.Coupon;
import com.shopping.electroshopping.service.couponService.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminCouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/listCoupon")
    public String showCoupons(Model model){
        List<Coupon> couponList = couponService.getAllCoupons();
        model.addAttribute("coupons", couponList);
        return "/coupon/user-coupon-code";
    }

    @GetMapping("/coupon")
    public String addCoupon(@ModelAttribute("coupon") Coupon coupon, Model model){
        List<Coupon> couponList = couponService.getAllCoupons();
        return "coupon/coupon-add";
    }

    @PostMapping("/add-coupon")
    public String addCoupon(@ModelAttribute("coupon") Coupon coupon){
        couponService.saveCoupon(coupon);
        return "redirect:/admin/coupon";
    }

    @GetMapping("/updateCouponForm/{id}")
    public String updateCoupon(@PathVariable("id") Long id, Model model){
        Optional<Coupon> coupon = Optional.ofNullable(couponService.findByCouponId(id).orElse(null));
        if (coupon.isPresent()){
            Coupon existingCoupon = coupon.get();
            model.addAttribute("coupon", existingCoupon);
        }
        return "coupon/updateCoupon";
    }

    @PostMapping("/updateCoupon")
    public String updateCoupon(@ModelAttribute("coupon") Coupon coupon){
        couponService.editCoupon(coupon);
        return "redirect:/admin/listCoupon";
    }

}
