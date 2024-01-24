package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.Enums.Payment;
import com.shopping.electroshopping.Enums.Status;
import com.shopping.electroshopping.dto.UserAddressDto;
import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.Order.OrderItem;
import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.user.UserAddress;
import com.shopping.electroshopping.model.wallet.Transactions;
import com.shopping.electroshopping.model.wallet.Wallet;
import com.shopping.electroshopping.model.wallet.WalletHistory;
import com.shopping.electroshopping.repository.*;
import com.shopping.electroshopping.service.OrderService.OrderItemService;
import com.shopping.electroshopping.service.OrderService.OrderService;
import com.shopping.electroshopping.service.basicService.BasicService;
import com.shopping.electroshopping.service.basicService.BasicServiceImpl;
import com.shopping.electroshopping.service.cartService.CartItemService;
import com.shopping.electroshopping.service.productService.ProductService;
import com.shopping.electroshopping.service.userservice.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserCheckOutController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    OrderRepository orderRepository;


    @Autowired
    BasicServiceImpl basicServices;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductService productService;

    @Autowired
    CartItemService cartItemService;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    WalletHistoryRepository walletHistoryRepository;


    @GetMapping("/checkout")
    public String checkOut(Model model, RedirectAttributes redirectAttributes,
                           Principal principal) {

        String email = principal.getName();
        User userId = userRepository.findByEmail(email);
        List<CartItem> cartItem = cartItemRepository.findByCartUser(userId);

        if (cartItem.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cart is empty");
            return "redirect:/user/cart";
        }

        User user = userRepository.findByEmail(email);
        Cart userCart = user.getCart();
        List<CartItem> cartItems = userCart.getCartItems();

        Optional<UserAddress> defaultAddress = userAddressRepository
                .findUserAddressByUserIdAndIsDefaultTrue(user.getId());
        defaultAddress.ifPresent(userAddress -> model.addAttribute("defaultAddress", userAddress));
        model.addAttribute("CartItems", cartItems);
        model.addAttribute("user", user);
        model.addAttribute("totalPrice", userCart.getTotal());

        return "Checkout/checkout";
    }


    @PostMapping("/addAddressFromCheckOut")
    public String addAddressFromCheckOut(@ModelAttribute UserAddress address, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        address.setUser(user);
        List<UserAddress> userAddresses = user.getAddresses();
        for (UserAddress address1 : userAddresses) {
            address1.setDefault(false);
        }
        address.setDefault(true);
        userAddressRepository.save(address);

        return "redirect:/user/checkout";
    }


    @PostMapping("/proceedToCheckOut")
    public String PlaceOrder(@RequestParam("paymentMethod") String paymentMethod, Principal principal
            , Model model, RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Cart userCart = user.getCart();
        Optional<UserAddress> defaultAddress = userAddressRepository
                .findUserAddressByUserIdAndIsDefaultTrue(user.getId());
        defaultAddress.ifPresent(userAddress -> model.addAttribute("defaultAddress", userAddress));

        Long userId = userRepository.findByEmail(email).getId();
        Long orderReference = orderRepository.getReferenceById(user.getId()).getId();
        Optional<User> customer = userService.findByUsername(basicServices.getCurrentUsername());
        User existingCustomer = customer.get();
        Cart cart = existingCustomer.getCart();
        List<CartItem> cartItems = userCart.getCartItems();

        model.addAttribute("CartItems", cartItems);
        model.addAttribute("totalPrice", userCart.getTotal());
        model.addAttribute("orderReference", orderReference);

        if (defaultAddress.isEmpty()){
            redirectAttributes.addFlashAttribute("address", "Please set address details before checkout");
            return "redirect:/user/checkout";

        }

        // Payment method is Paypal it will redirect to the Paypal controller page
        if (paymentMethod.equals("Paypal")) {
            return "redirect:/user/home";
            //Check stock, if the stock is available then only success
        }
        if (paymentMethod.equals("WALLET")) {
            return "redirect:/user/orderPlacedWithWallet";
        } else {
            List<CartItem> cartItemList = userCart.getCartItems();
            boolean allItemsInStock = true;
            List<String> outOfStockItems = new ArrayList<>();

            for (CartItem cartItem : cartItemList) {
                Product product = cartItem.getProduct();
                int quantityToOrder = cartItem.getQuantity();
                if (product.getStock() < quantityToOrder) {
                    allItemsInStock = false;
                    outOfStockItems.add(product.getProductName());
                }
            }
            if (allItemsInStock) {
                orderService.saveOrder(paymentMethod, principal);
                cartItemService.decreaseStock(cartItemList);
                cartItemRepository.deleteAll();
                return "Checkout/orderSuccess";
            } else {
                ;
                redirectAttributes.addFlashAttribute("outOfStockItems", outOfStockItems);
                // Redirect to cart with out-of-stock message
                return "redirect:/user/cart";
            }
        }
    }


    @GetMapping("/orderPlacedWithWallet")
    public String placeOrderWithWallet(RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String insufficientMoney = "Wallet has Insufficient Money";

        User user = userRepository.findByEmail(email);
        Optional<Wallet> wallet = Optional.ofNullable(user.getWallet());
        Cart userCart = cartRepository.findCartByUserId(user.getId());
        if (wallet.isPresent()) {
            Wallet userWallet = wallet.get();
            long id = userWallet.getId();
            double bal = userWallet.getBalance();
            double totalPrice = userCart.getTotal();

            LocalDateTime currentDateTime = LocalDateTime.now();
            Order userOrder = new Order();

            if (bal >= totalPrice) {
                userOrder.setUser(user);
                userOrder.setPayment(Payment.WALLET);
                userOrder.setStatus(Status.PENDING);
                userOrder.setOrdered_date(LocalDate.from(currentDateTime));
                userOrder.setTotal((int) totalPrice);
                orderRepository.save(userOrder);
                userWallet.setBalance(bal - totalPrice);
                walletRepository.save(userWallet);

                WalletHistory walletHistory = new WalletHistory();
                walletHistory.setWallet(userWallet);
                walletHistory.setAmount(totalPrice);
                walletHistory.setTransaction(Transactions.DEBIT);
                walletHistory.setTransactionDate(LocalDate.now());
                walletHistoryRepository.save(walletHistory);


                for (CartItem cartItem : userCart.getCartItems()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(userOrder);
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setProduct(cartItem.getProduct());

                    orderItemRepository.save(orderItem);
                }
                return "Checkout/orderSuccess";

            }
        }
        redirectAttributes.addFlashAttribute("insufficientMoney", insufficientMoney);
        return "redirect:/user/cart";
    }
}
