package com.shopping.electroshopping.service.userservice;

import com.shopping.electroshopping.dto.UserAddressDto;
import com.shopping.electroshopping.dto.UserSignUpDto;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.user.UserAddress;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    User save(UserSignUpDto signUpDto);

    public void blockUser(Long id);

    public void unblockUser(Long id);

    public UserAddress addUserAddress(UserAddressDto userAddressDto);

    public void registerNewUser(UserSignUpDto user, HttpServletRequest request, String link);

    boolean isEmailAlreadyRegistered(String email);

    void updateUserAddress(UserAddressDto userAddressDto, Long id);

    public void updatePassword(Long id, User user);

    public void updateResetPasswordToken(String token, String email);

    //Update user with new password
    void updatePassword(User user, String newPassword);

    String getSiteURL(HttpServletRequest request);

    public void sendEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException;

    public User getByResetPasswordToken(String token);
}
