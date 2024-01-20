package com.shopping.electroshopping.controllers;

import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.repository.CartItemRepository;
import com.shopping.electroshopping.repository.ProductOfferRepository;
import com.shopping.electroshopping.repository.ProductRepository;
import com.shopping.electroshopping.service.userservice.UserService;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    ProductOfferRepository productOfferRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    UserService userService;


    //Login Page Controller for show Login Page Form
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    //Home Page Controller for Showing Index.html file
    @GetMapping("/")
    public String home(Model model) {
        List<Product> productList = productRepository.findAll();

        model.addAttribute("listProducts", productList);
        return "index";
    }


    // Controller for reset password, In here user should type email address
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "/forgot_password_form";
    }


    // This controller handle password reset, Control the password reset link send to User.
    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = userService.getSiteURL(request) + "/reset_password?token=" + token;
            userService.sendEmail(email, resetPasswordLink);
            model.addAttribute("message"
                    , "We have sent a reset password link to your email. please check.");
        } catch (UsernameNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "Error while sending email");
        }
        return "/login";
    }


    // To get Password and Conform Password entering Page.
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        }
        return "/resetPassword";
    }


    // Password will change Or show error message.
    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (user == null) {
            model.addAttribute("message", "invalid token");
            return "message";
        } else {
            userService.updatePassword(user, password);
            redirectAttributes.addFlashAttribute("passwordSuccess"
                    , "You have successfully changed your password.");
        }

        return "redirect:/login";
    }



}









