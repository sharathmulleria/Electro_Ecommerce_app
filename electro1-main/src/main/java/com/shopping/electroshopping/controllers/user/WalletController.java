package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.wallet.WalletHistory;
import com.shopping.electroshopping.repository.CartRepository;
import com.shopping.electroshopping.repository.UserRepository;
import com.shopping.electroshopping.repository.WalletHistoryRepository;
import com.shopping.electroshopping.service.walletService.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class WalletController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    WalletService walletService;

    @Autowired
    WalletHistoryRepository walletHistoryRepository;


    @GetMapping("/wallet")
    public String walletShow(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        double walletBalance = walletService.getCurrentWalletBalance(user);

        model.addAttribute("walletBalance", walletBalance);

        return "/user/wallet";
    }


    @GetMapping("/wallet-transaction")
    public String walletHistory(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<WalletHistory> walletHistory = walletHistoryRepository
                                            .findWalletHistoryByEmail(email);
        model.addAttribute("transactions", walletHistory);
        return "/user/wallet_transaction";
    }


}
