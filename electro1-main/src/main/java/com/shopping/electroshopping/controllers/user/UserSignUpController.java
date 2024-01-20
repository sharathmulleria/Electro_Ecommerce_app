package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.dto.UserSignUpDto;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.service.otpservice.OtpService;
import com.shopping.electroshopping.service.userservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
@Controller
@RequestMapping("/registration")
public class UserSignUpController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private Validator validator;


    @ModelAttribute("user")
    public UserSignUpDto userSignUpDto() {
        return new UserSignUpDto();
    }


//Show User Sign Up Page for New User Registration
    @GetMapping()
    public String showsignUp() {
        return "signUp";
    }


//User Sign Up Controller for Register New User Details
    @PostMapping
    public String registeruser(@ModelAttribute("user") UserSignUpDto signUpDto,
                               HttpSession session, Model model, BindingResult bindingResult)
                                                    throws MessagingException, UnsupportedEncodingException {

        validator.validate(signUpDto,bindingResult);

        if (userService.isEmailAlreadyRegistered(signUpDto.getEmail())) {
            model.addAttribute("error", "This email address is already registered." +
                                                                    "Please use a different email address.");
            return "/signUp";
        }

        if (bindingResult.hasErrors()){
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "/signUp";
        }

        session.setAttribute("UserSignUpDto", signUpDto);
        userService.save(signUpDto);
        otpService.sentOtp(signUpDto, session);

        return "redirect:/getOtpPage";
    }


}