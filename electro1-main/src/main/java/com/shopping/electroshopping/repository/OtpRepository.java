package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.OtpModel.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {

    public Otp findByEmail(String email);


}
