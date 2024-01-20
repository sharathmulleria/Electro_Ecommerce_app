package com.shopping.electroshopping.service.userservice;

import com.shopping.electroshopping.config.LogoutEvent;
import com.shopping.electroshopping.dto.UserAddressDto;
import com.shopping.electroshopping.dto.UserSignUpDto;
import com.shopping.electroshopping.model.role.Role;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.user.UserAddress;
import com.shopping.electroshopping.model.wallet.Wallet;
import com.shopping.electroshopping.repository.UserAddressRepository;
import com.shopping.electroshopping.repository.UserRepository;
import com.shopping.electroshopping.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    public UserServiceImpl(UserRepository customerRepository) {
        this.userRepository = customerRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(username));
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("invalid username or password "+username);
        }

        User user = userOptional.get();



        if (user.isBlocked()) {
            throw new DisabledException("User is blocked");
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }


    //To save the user details
    @Override
    public User save(UserSignUpDto signUpDto) {
        User customer = new User(signUpDto.getUserName(),
                            signUpDto.getEmail(),
                            passwordEncoder.encode(signUpDto.getPassword()),
                            signUpDto.getPhoneNumber(),
                            Arrays.asList(new Role("ROLE_USER")));
        customer.setJoinDate(LocalDate.now());
        return userRepository.save(customer);
    }


    @Override
    public void blockUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        if (user.isBlocked()){
            user.setBlocked(false);
        }else {
            user.setBlocked(true);
        }
        invalidateUserSession(user.getEmail());

        userRepository.save(user);
    }


    private void invalidateUserSession(String username){
        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals){
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                if (userDetails.getUsername().equals(username)){
                    List<SessionInformation> sessionInformationList = sessionRegistry.getAllSessions(principal,false);
                    for (SessionInformation sessionInformation : sessionInformationList){
                        eventPublisher.publishEvent(new LogoutEvent(sessionInformation.getSessionId()));
                    }
                    }
            }
        }
    }


    public void unblockUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBlocked(false);
            userRepository.save(user);
        }
    }


    public UserAddress addUserAddress(UserAddressDto userAddressDto) {
        Long userAddressId = userAddressDto.getUser_id();
        UserAddress userAddress = new UserAddress();
        userAddress.setStreetDetails(userAddressDto.getStreetDetails());
        userAddress.setCityName(userAddressDto.getCityName());
        userAddress.setAddressPhoneNumber(userAddressDto.getAddressPhoneNumber());
        userAddress.setState(userAddressDto.getState());
        userAddress.setPostalCode(userAddressDto.getPostalCode());
        Long userId = userAddressDto.getUser_id();

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        userAddress.setUser(user);
        return userAddressRepository.save(userAddress);
    }


    @Override
    public void registerNewUser(UserSignUpDto userSignUpDto, HttpServletRequest request, String link) {
        User newUser = new User();
        LocalDate localDate = LocalDate.now();
        Role role = new Role();
        newUser.setEmail(userSignUpDto.getEmail());
        newUser.setUserName(userSignUpDto.getUserName());
        newUser.setJoinDate(LocalDate.now());
        newUser.setPhoneNumber(userSignUpDto.getPhoneNumber());
        newUser.setJoinDate(localDate);
        newUser.setVerified(true);
        role.setName("ROLE_USER");
        newUser.setPassword(passwordEncoder.encode(userSignUpDto.getPassword()));
    }


    @Override
    public boolean isEmailAlreadyRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updateUserAddress(UserAddressDto userAddressDto, Long id) {
        Long userAddressId = userAddressDto.getUser_id();
        Optional<User> existingUserOptional = userRepository.findById(id);
        Optional<UserAddress> existinAddressOptional = userAddressRepository.findById(userAddressId);

        if (existingUserOptional.isPresent()) {
            UserAddress existinAddress = existinAddressOptional.get();
            existinAddress.setStreetDetails(userAddressDto.getStreetDetails());
            existinAddress.setCityName(userAddressDto.getCityName());
            existinAddress.setAddressPhoneNumber(userAddressDto.getAddressPhoneNumber());
            existinAddress.setState(userAddressDto.getState());
            existinAddress.setPostalCode(userAddressDto.getPostalCode());
            existinAddress.setUserAddressID(userAddressDto.getUser_id());
            userAddressRepository.save(existinAddress);
        }
    }

    @Override
    public void updatePassword(Long id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User newUser = existingUser.get();
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(newUser);
        }
    }


    public Optional<User> findByUsername(String currentUsername) {
        return Optional.ofNullable(userRepository.findByEmail(currentUsername));
    }


    public List<User> getCustomerByName(String email) {
        return userRepository.findByName(email);
    }


    @Transactional
    public void updateProfile(Long userId, UserSignUpDto updateProfile) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUserName(updateProfile.getUserName());
            user.setEmail(updateProfile.getEmail());
            user.setPhoneNumber(updateProfile.getPhoneNumber());
            user.setPassword(passwordEncoder.encode(updateProfile.getPassword()));
            userRepository.save(user);
        }
    }


    public void editUserAddress(Long userAddressID, UserAddress userAddress) {
        UserAddress userAddressDto = userAddressRepository.findById(userAddressID).orElse(null);
        userAddressRepository.save(userAddressDto);
    }


    @Override
    // Update User Token for reset the password
    public void updateResetPasswordToken(String token, String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("could not find any user with the email");
        }
    }


    @Override
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }


    @Override
    //Update user with new password
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }


    @Override
    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }


    // Configure mail for reset password. Subject and text of the mail configure here.
    @Override
    public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("sharathalways041@gmail.com", "Shopme Support");
        helper.setTo(recipientEmail);
        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password</p>"
                + "<p>Click the link below to change your password</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "</br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request</p>";

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }


}


