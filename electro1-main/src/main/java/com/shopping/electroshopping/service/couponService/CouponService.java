package com.shopping.electroshopping.service.couponService;

import com.shopping.electroshopping.model.coupon.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponService {

    public Coupon findByCode(String code);

    public void saveCoupon(Coupon coupon);

    public void deleteCoupon(Long id);

    public Optional<Coupon> findByCouponId(Long id);

    public List<Coupon> getAllCoupons();

    public Coupon editCoupon(Coupon updatedCoupon);


}
