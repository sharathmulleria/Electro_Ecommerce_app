package com.shopping.electroshopping.service.otpservice;

import com.shopping.electroshopping.dto.UserSignUpDto;
import com.shopping.electroshopping.model.OtpModel.Otp;
import com.shopping.electroshopping.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpServiceImp implements OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;


    @Override
    public int genrateOtp(UserSignUpDto signUpDto) {
        Random random = new Random();
        int otp = 100_000 + random.nextInt(900_000);
        saveOtp(signUpDto, otp);
        return otp;
    }


    public void saveOtp(UserSignUpDto signUpDto, int otp) {
        otpRepository.save(buildOtp(signUpDto, otp));
    }


    private Otp buildOtp(UserSignUpDto signUpDto, int otp) {
        Otp otp1 = new Otp();
        otp1.setOtp(otp);
        otp1.setEmail(signUpDto.getEmail());
        otp1.setCreationTime(LocalDateTime.now());
        return otp1;
    }



    @Override
    public void sentOtpToEmail(String userEmail, int otp) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("pedalplanetbicycles@gmail.com", "electro_verification");
        helper.setTo(userEmail);
        helper.setSubject("Hey welcome to 'Electro' stay connected");
        String content = "<html><body style='font-family: Arial, sans-serif;'>"
                + "<h2 style='color: #007bff;'>Hey there!</h2>"
                + "<p>We're excited to have you join 'Electro'. To verify your email address, please use the following One-Time Password (OTP):</p>"
                + "<p style='font-size: 24px; color: #007bff;'>" + otp + "</p>"
                + "<p>If you didn't request this OTP or have any questions, feel free to contact our support team.</p>"
                + "<p>Happy Shopping!</p>"
                + "<p style='color: #888;'>Note: This OTP is valid for a single use and will expire shortly.</p>)"
                + "</body></html>";
        helper.setText(content, true);
        mailSender.send(message);
    }


    @Override
    public String getStroedOtpByEmail(String email) {
        Otp otp = otpRepository.findByEmail(email);
        return String.valueOf((otp != null) ? otp.getOtp() : null);
    }


    public void sentOtp(@ModelAttribute UserSignUpDto signUpDto, HttpSession session)
            throws MessagingException, UnsupportedEncodingException {

        int otp = genrateOtp(signUpDto);
        System.out.println(otp);
        sentOtpToEmail(signUpDto.getEmail(), otp);
        session.setAttribute("userEmail", signUpDto.getEmail());

    }


    @Override
    public void deleteExistingOtp(String email) {
        Otp otp = otpRepository.findByEmail(email);
        otpRepository.delete(otp);

    }


}
