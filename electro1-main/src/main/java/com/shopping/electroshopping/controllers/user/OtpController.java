package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.dto.UserSignUpDto;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.service.otpservice.OtpService;
import com.shopping.electroshopping.service.userservice.UserService;
import com.shopping.electroshopping.service.userservice.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

@Controller
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    UserService userService;


    @GetMapping("/getOtpPage")
    public String getOtpPage(@RequestParam(name = "error", defaultValue = " ") String error, Model model) {
        model.addAttribute("error", error);
        model.addAttribute("invalid", "invalid");
        model.addAttribute("resend", "resend");
        model.addAttribute("success", "success");
        return "/otpPage";
    }


    @PostMapping("/otpSubmit")
    public String processOtp(HttpServletRequest request, HttpSession session) {
        String enteredOtp = request.getParameter("enteredOtp");
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail != null) {

            Integer stroredOtp = Integer.parseInt(otpService.getStroedOtpByEmail(userEmail));

            if (enteredOtp != null && stroredOtp
                    .equals(Integer.parseInt(enteredOtp))) {

                UserSignUpDto userSignUpDto = (UserSignUpDto) session.getAttribute("UserSignUpDto");
                String link = (String) session.getAttribute("link");
                userService.registerNewUser(userSignUpDto, request, link);
                return "redirect:/login?otpSuccess";
            }

        }

        return "redirect:/getOtpPage?otpError";
    }


    @GetMapping("/reSendOtp")
    public String resendOtp(HttpServletRequest request, HttpSession session)
                                    throws MessagingException, UnsupportedEncodingException {

        UserSignUpDto signUpDto = (UserSignUpDto) session.getAttribute("User");

        if (signUpDto != null) {
            otpService.deleteExistingOtp(signUpDto.getEmail());
            otpService.sentOtp(signUpDto, session);
            return "redirect:/otpPage?success=resend";
        }

        return "redirect:/registration";
    }


}
