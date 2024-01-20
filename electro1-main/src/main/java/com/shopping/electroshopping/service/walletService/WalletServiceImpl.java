package com.shopping.electroshopping.service.walletService;

import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.wallet.Wallet;
import com.shopping.electroshopping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    UserRepository userRepository;

    @Override
    public double getCurrentWalletBalance(User user) {

        if (user != null) {
            Wallet wallet = user.getWallet();

            if (wallet != null) {
                return wallet.getBalance();
            }
        }
        return 0.0;
    }





}
