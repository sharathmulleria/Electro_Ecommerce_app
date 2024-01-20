package com.shopping.electroshopping.service.WalletHistory;

import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.wallet.Transactions;
import com.shopping.electroshopping.model.wallet.WalletHistory;
import com.shopping.electroshopping.repository.WalletHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WalletHistoryServiceImpl implements WalletHistoryService{

    @Autowired
    WalletHistoryRepository walletHistoryRepository;

    @Override
    public void saveHistory(double totalAmount, User user) {
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setTransaction(Transactions.DEBIT);
        walletHistory.setTransactionDate(LocalDate.now());
        walletHistory.setAmount(totalAmount);
        walletHistory.setWallet(user.getWallet());
        walletHistoryRepository.save(walletHistory);
    }
}
