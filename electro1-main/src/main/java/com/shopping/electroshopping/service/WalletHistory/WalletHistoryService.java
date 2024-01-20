package com.shopping.electroshopping.service.WalletHistory;

import com.shopping.electroshopping.model.user.User;

public interface WalletHistoryService {

    void saveHistory(double totalAmount, User user);
}
