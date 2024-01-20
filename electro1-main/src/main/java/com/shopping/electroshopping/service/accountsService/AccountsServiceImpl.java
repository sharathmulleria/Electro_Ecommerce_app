package com.shopping.electroshopping.service.accountsService;

import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.model.accounts.Accounts;
import com.shopping.electroshopping.repository.OrderItemRepository;
import com.shopping.electroshopping.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class AccountsServiceImpl implements AccountsService {

    @Autowired
    OrderItemRepository orderItemRepository;


    @Override
    public Accounts getAccountsDetails() {
        double totalSale = 0;
        double totalCost = 0;
        double totalProfit = 0;
        double todaySale = 0;

        Accounts accounts = new Accounts();
        List<OrderItem> orderItems = orderItemRepository.findAll();

        for (OrderItem order : orderItems) {
            totalSale = totalSale + order.getOrder().getTotal();
            totalCost = totalCost + order.getProduct().getCost();
            totalProfit = totalProfit + order.getPrice() - order.getProduct().getCost();
            if (LocalDate.now().equals(order.getOrder().getOrdered_date())){
                todaySale += order.getPrice();
            }

        }
        accounts.setTotalSale(totalSale);
        accounts.setTotalCost(totalCost);
        accounts.setTotalProfit(totalProfit);
        accounts.setTodaySale(todaySale);
        return accounts;
    }
}
