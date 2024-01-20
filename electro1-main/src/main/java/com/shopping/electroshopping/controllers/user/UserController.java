package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.dto.UserAddressDto;
import com.shopping.electroshopping.dto.UserDto;
import com.shopping.electroshopping.dto.UserSignUpDto;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.user.UserAddress;
import com.shopping.electroshopping.repository.UserAddressRepository;
import com.shopping.electroshopping.repository.UserRepository;
import com.shopping.electroshopping.service.userservice.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    UserAddressRepository userAddressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;


    //Add User address Details in User Profile Page
    @GetMapping("addUserAddress")
    public String addUserAddress(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().
                getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        // Check if user is not null
        Long userId = (user != null) ? user.getId() : null;

        model.addAttribute("user_id", userId);

        return "/user/addUserAddress";

    }


    //View User profile in Users Account
    @GetMapping("/profile")
    public String profileView(Model model) {
        Authentication authentication = SecurityContextHolder.
                getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        UserDto userDto = new UserDto();
        userDto.setDefaultAddress(user.getDefaultAddress());

        Long userId = user.getId();
        String userName = user.getUserName();
        String emailId = user.getEmail();
        String phone = user.getPhoneNumber();
        String selectedAddresses = String.valueOf(user.getDefaultAddress());
        List<UserAddress> addresses = user.getAddresses();

        model.addAttribute("user_id", userId);
        model.addAttribute("username", userName);
        model.addAttribute("email", emailId);
        model.addAttribute("phone", phone);
        model.addAttribute("addresses", addresses);
        model.addAttribute("selectedAddresses", selectedAddresses);
        model.addAttribute("userDto", userDto);

        return "/user/userProfile";
    }


    //User Can set Default Address in User Profile Page
    @PostMapping("/set-default-address")
    public String makeUserAddressDefault(@RequestParam("addressId") Long addressId, RedirectAttributes redirectAttributes
            , Principal principal) {
        String name = principal.getName();
        Optional<UserAddress> existingAddress = userAddressRepository.findById(addressId);
        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(name));
        Optional<List<UserAddress>> addressList = userAddressRepository.findAllByUserId(existingUser.get().getId());

        for (UserAddress address : addressList.get()) {
            address.setDefault(false);
        }

        if (existingAddress.isPresent()) {
            UserAddress address = existingAddress.get();
            address.setDefault(true);
            userAddressRepository.save(address);
        }

        redirectAttributes.addFlashAttribute("success", "Your default address is changed");
        return "redirect:/user/profile";
    }


    @GetMapping("/updatePassword/{user_id}")
    public String updatePassword(@PathVariable("user_id") Long userId, Model model) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "user/updatePassword";
    }

    @PostMapping("/conformPassword")
    public String changeUserPassword(@ModelAttribute("user") User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email);
        userService.updatePassword(currentUser.getId(), user);
        return "redirect:/user/profile";
    }


    @ModelAttribute("userAddressDto")
    public UserAddressDto userAddressDto() {
        return new UserAddressDto();
    }


    @PostMapping("addUserAddress")
    public String addUserAddressFormSubmit(@ModelAttribute("userAddressDto")
                                           UserAddressDto userAddressDto) {
        userService.addUserAddress(userAddressDto);
        return "redirect:/user/profile";
    }


    @GetMapping("/updateProfile/{user_id}")
    public String editUserProfile(@PathVariable("user_id") Long user_id, Model model) {

        User user = userRepository.findById(user_id).
                orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + user_id));

        model.addAttribute("user", user);
        model.addAttribute("userId", user_id);

        return "user/updateProfile";
    }


    @ModelAttribute("user")
    public UserSignUpDto userSignUpDto() {
        return new UserSignUpDto();
    }


    @PostMapping("/updateProfile/{id}")
    public String updateUser(@PathVariable("id") Long userId,
                             @ModelAttribute("user") UserSignUpDto updateProfile) {
        userService.updateProfile(userId, updateProfile);
        return "redirect:/user/profile";
    }


    @GetMapping("/edit-address")
    public String editAddress(@RequestParam("userAddressID") Long userAddressID, Model model) {
        // Fetch the UserAddress object from the database
        UserAddress userAddress = userAddressRepository.findById(userAddressID).orElseThrow(() ->
                new RuntimeException("User address not found"));
        model.addAttribute("userAddress", userAddress);

        return "user/editUserAddress";
    }


    @GetMapping("/delete-address")
    public String deleteAddress(@RequestParam("userAddressID") Long userAddressID) {
        UserAddress userAddress = userAddressRepository.findById(userAddressID).orElseThrow(() ->
                new RuntimeException("User address not found"));
        userAddressRepository.deleteById(userAddressID);

        return "redirect:/user/profile";

    }


    @PostMapping("/post-address")
    public String saveEditedAddress(@RequestParam("userAddressID") Long userAddressID
            , @ModelAttribute("userAddress") UserAddressDto userAddressDto, Model model
            , Principal principal) {

        System.out.println(userAddressDto.toString() +"address***************************");
        String email = principal.getName();
        Long id = userRepository.findByEmail(email).getId();

        userService.updateUserAddress(userAddressDto, id);
        return "redirect:/user/profile";
    }







}





