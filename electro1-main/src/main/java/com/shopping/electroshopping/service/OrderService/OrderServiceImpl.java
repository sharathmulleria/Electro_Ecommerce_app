package com.shopping.electroshopping.service.OrderService;

import com.nimbusds.jose.proc.SecurityContext;
import com.shopping.electroshopping.Enums.Payment;
import com.shopping.electroshopping.Enums.Status;
import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.user.UserAddress;
import com.shopping.electroshopping.model.wallet.Transactions;
import com.shopping.electroshopping.model.wallet.Wallet;
import com.shopping.electroshopping.model.wallet.WalletHistory;
import com.shopping.electroshopping.repository.*;
import com.shopping.electroshopping.service.WalletHistory.WalletHistoryService;
import com.shopping.electroshopping.service.walletService.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    UserAddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    WalletHistoryRepository walletHistoryRepository;

    @Autowired
    WalletService walletService;

    @Autowired
    WalletHistoryService walletHistoryService;


    @Override
    public void saveOrder(String paymentMethod, Principal principal) {


        Order newOrder = new Order();
        newOrder.setOrdered_date(LocalDate.now());
        User user = userRepository.findByEmail(principal.getName());
        Optional<UserAddress> orderedAddress = userAddressRepository.findUserAddressByUserIdAndIsDefaultTrue(user.getId());
        List<CartItem> cartItems = cartItemRepository.findByCartUser(user);

        double totalAmount = user.getCart().getTotal();
        if (orderedAddress.isPresent()) {
            UserAddress address = orderedAddress.get();
            newOrder.setAddress(address);
        }

        newOrder.setShipping_date(LocalDate.now().plusDays(4));

        if (paymentMethod.equals("COD")) {
            newOrder.setPayment(Payment.COD);
        }

        if (paymentMethod.equals("Paypal")) {
            newOrder.setPayment(Payment.Paypal);
        }
        if (paymentMethod.equals("WALLET")) {
            newOrder.setPayment(Payment.WALLET);
            Wallet userWallet = user.getWallet();
            userWallet.setBalance(userWallet.getBalance() - totalAmount);
            walletRepository.save(userWallet);
            walletHistoryService.saveHistory(totalAmount, user);
        }

        newOrder.setExpecting_date(LocalDate.now().plusDays(7));
        newOrder.setUser(user);
        newOrder.setStatus(Status.PENDING);
        newOrder.setTotal((float) user.getCart().getTotal());

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setOrder(newOrder);
            orderItems.add(orderItem);
            orderItem.setProduct(cartItem.getProduct());
            orderItemRepository.save(orderItem);
        }

        orderRepository.save(newOrder);
    }


    @Override
    public void decresethQuantity(User user) {
        Optional<CartItem> cartItems = cartItemRepository.findById(user.getId());
        if (cartItems.isPresent()) {
            CartItem cartItem = cartItems.get();
            List<CartItem> items = (List<CartItem>) cartItem;

            for (CartItem cart : items) {
                Product product = cartItem.getProduct();
                int quantyPurchased = cartItem.getQuantity();
                int currentStock = product.getStock();

                if (currentStock >= quantyPurchased) {
                    product.setStock(currentStock - quantyPurchased);
                    productRepository.save(product);
                }
            }
        }
    }


    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findOrdersByUserId(userId);
    }


    @Override
    public void editOrderStatus(Long orderId, Order order) {
        order = orderRepository.findById(orderId).orElse(null);
        order.setStatus(order.getStatus());
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void cancelOrder(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        Order order = orderRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Order id not found to check wallet"));
        double refundAmount = order.getTotal();
        Wallet userWallet = user.getWallet();

        if (order.getPayment().toString().equals("Paypal")) {
            WalletHistory walletHistory = new WalletHistory();
            walletHistory.setWallet(userWallet);
            walletHistory.setAmount(refundAmount);
            walletHistory.setTransaction(Transactions.CREDIT);
            walletHistory.setTransactionDate(LocalDate.now());
            walletHistoryRepository.save(walletHistory);

            if (user.getWallet()==null){
                userWallet= new Wallet();
                userWallet.setUser(user);
                userWallet.setBalance(refundAmount);
                user.setWallet(userWallet);
            }else {
                userWallet.setBalance(refundAmount+userWallet.getBalance());
                walletRepository.save(userWallet);
            }

            increaseQuantity(order);
        }

// Check if the status is cancelled or not then update the wallet refund
        if (order.getStatus() != Status.CANCELLED) {
            order.setStatus(Status.CANCELLED);
            orderRepository.save(order);
            increaseQuantity(order);
        }
    }


    @Override
    public void increaseQuantity(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(order.getId());
        for (OrderItem x : orderItems) {
            Product product = x.getProduct();
            int existingStock = product.getStock();
            product.setStock(existingStock + x.getQuantity());
            productRepository.save(product);
        }
    }

    @Override
    public void deleteProductById(Long id) {
        this.orderRepository.deleteById(id);
    }


    public boolean quantityCheck(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(order.getId());
        for (OrderItem x : orderItems) {
            Product product = x.getProduct();
            int existingStock = product.getStock();
            int quantity = x.getQuantity();
            if (product.getStock() >= quantity) {
                return true;
            }
        }
        return false;
    }

    public Order saveOrderItem(Order order, List<OrderItem> orderItems) {
        // Calculate order total
        float total = 0;
        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findById(orderItem.getProduct().getId()).get();
            orderItem.setProduct(product);
            orderItem.setPrice((float) (product.getPrice() * orderItem.getQuantity()));
            total += orderItem.getPrice();
            order.getOrderItems().add(orderItem);
        }
        order.setTotal(total);
        return orderRepository.save(order);
    }




}
