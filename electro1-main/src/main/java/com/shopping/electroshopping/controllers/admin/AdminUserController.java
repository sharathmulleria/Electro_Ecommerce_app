package com.shopping.electroshopping.controllers.admin;

import com.shopping.electroshopping.model.accounts.Accounts;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.repository.UserRepository;
import com.shopping.electroshopping.service.accountsService.AccountsService;
import com.shopping.electroshopping.service.userservice.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserRepository customerRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private SessionRegistry session;


    @GetMapping("/home")
    public String adminhome(Model model) {
        Accounts accounts = accountsService.getAccountsDetails();
        model.addAttribute("totalSale", accounts.getTotalSale());
        model.addAttribute("totalCost", accounts.getTotalCost());
        model.addAttribute("totalProfit", accounts.getTotalProfit());
        model.addAttribute("todaySale", accounts.getTodaySale());
        return "adminhome";
    }


    @GetMapping("/listUsers")
    public String listusers(Model model) {
        List<User> customers = customerRepository.findAll();
        model.addAttribute("listUsers", customers);
        return "user/listusers";
    }


    @GetMapping("/search")
    public String searchuser(@RequestParam("email") String email, Model model) {
        List<User> list = userService.getCustomerByName(email);
        model.addAttribute("listUsers", list);
        return "user/listusers";
    }


    @PostMapping("/blockUser/{id}")
    public String blockUser(@PathVariable Long id) {
        Optional<User> userOptional = customerRepository.findById(id);
        User user = userOptional.get();
        userService.blockUser(id);
        return "redirect:/admin/listUsers";
    }


    @PostMapping("/unblockUser/{id}")
    public String unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return "redirect:/admin/listUsers";
    }


}
