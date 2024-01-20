package com.shopping.electroshopping.service.couponService;

import com.shopping.electroshopping.model.coupon.Coupon;
import com.shopping.electroshopping.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;


    @Override
    public Coupon findByCode(String code) {
        return couponRepository.findByCode(code);
    }


    @Override
    public void saveCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }


    @Override
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    @Override
    public Optional<Coupon> findByCouponId(Long id) {
        return couponRepository.findById(id);
    }


    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }


//Update Coupon for AdminCouponController class
    @Override
    public Coupon editCoupon(Coupon updatedCoupon) {
        Optional<Coupon> couponOptional = couponRepository.findById(updatedCoupon.getCouponId());
        if (couponOptional.isPresent()){
            Coupon coupon = couponOptional.get();
            coupon.setCode(updatedCoupon.getCode());
            coupon.setDescription(updatedCoupon.getDescription());
            coupon.setExpirationDate(updatedCoupon.getExpirationDate());
            coupon.setDiscountPercentage(updatedCoupon.getDiscountPercentage());
            return couponRepository.save(coupon);
        }else {
            throw new RuntimeException("Coupon not found with id "+updatedCoupon.getCouponId());
        }
    }
}
