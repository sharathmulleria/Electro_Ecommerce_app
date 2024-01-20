package com.shopping.electroshopping.service.otpservice;

import com.shopping.electroshopping.dto.UserSignUpDto;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

public interface OtpService {

    public int genrateOtp(UserSignUpDto signUpDto);

    public void sentOtpToEmail(String userEmail, int otp) throws
            MessagingException, UnsupportedEncodingException;

    public String getStroedOtpByEmail(String userEmail);

    public void sentOtp(UserSignUpDto signUpDto, HttpSession session)
            throws MessagingException, UnsupportedEncodingException;

    public void deleteExistingOtp(String email);


}
