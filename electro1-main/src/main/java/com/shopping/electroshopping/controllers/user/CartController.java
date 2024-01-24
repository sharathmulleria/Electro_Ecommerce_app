package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.cart.CartItem;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.repository.*;
import com.shopping.electroshopping.service.cartService.CartItemService;
import com.shopping.electroshopping.service.cartService.CartService;
import com.shopping.electroshopping.service.productService.ProductService;
import com.shopping.electroshopping.service.productService.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.model.IModel;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class CartController {

    @Autowired
    ProductServiceImpl productServiceImp;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    CartItemService cartItemService;

    @Autowired
    ProductService productService;

    @Autowired
    UserAddressRepository userAddressRepository;


    @GetMapping("/cart")
    public String showCart(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal != null) {
            String email = principal.getName();
            User user = userRepository.findByEmail(email);
            List<CartItem> cartItems = cartItemRepository.findByCartUser(user);
            double totalPrice = 0;

            for (CartItem cartItem : cartItems) {
                totalPrice += cartItem.getProduct().getDiscountedPrice();
            }

            double totalAmount;
            Optional<Cart> userCarts = Optional.ofNullable(user.getCart());

            if (userCarts.isPresent()) {
                Cart cart = userCarts.get();
                cart.setTotal(totalPrice);
                int totalProducts = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
                model.addAttribute("totalProducts", totalProducts);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("cartItems", cartItems);
                cartRepository.save(cart);
                return "/cart/cart";
            } else {
                redirectAttributes.addFlashAttribute("error", "Opps.! No item in your Cart..");
                return "/cart/cart";
            }
        }
        return "redirect:/login";
    }


    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("productId") Long productId,
                            Model model, Principal principal,
                            RedirectAttributes redirectAttributes) {
        String email = principal.getName();

        if (principal == null) {
            return "redirect:/login";
        } else {

            User user = userRepository.findByEmail(email);
            Cart existingCart = user.getCart(); // Use this simplified way to get the user's cart
            Optional<Product> product = productRepository.findById(productId);

            // Create a new cart if it doesn't exist
            if (existingCart == null) {
                existingCart = new Cart();
                existingCart.setUser(user);
                cartRepository.save(existingCart);
                user.setCart(existingCart);
                userRepository.save(user);
            }
            if (product.isPresent()) {
                Product existingProduct = product.get();
                // Check if the product is already in the cart
                CartItem existingCartItem = cartItemRepository
                        .findByCartAndProduct(existingCart, existingProduct);

                if (existingCartItem != null) {
                    // Product is already in the cart, increase the quantity
                    existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
                    cartItemRepository.save(existingCartItem);
                } else {
                    // Product is not in the cart, create a new cart item
                    cartItemService.saveToCart(existingCart, existingProduct);
                }

                redirectAttributes.addFlashAttribute("success",
                        "Cart item added");
                return "redirect:/user/productView?productId=" + productId;
            } else {
                redirectAttributes.addFlashAttribute("error_message",
                        "Product not found");
                return "redirect:/user/productView?productId=" + productId;
            }
        }
    }


    @GetMapping("/addToCartForWishlist")
    public String addToCartForWishlistItem(@RequestParam("productId") Long productId,
                                           Model model, Principal principal,
                                           RedirectAttributes redirectAttributes) {
        String email = principal.getName();

        if (principal == null) {
            return "redirect:/login";
        } else {

            User user = userRepository.findByEmail(email);
            Cart existingCart = user.getCart(); // Use this simplified way to get the user's cart

            if (existingCart == null) {

                // Create a new cart if it doesn't exist
                existingCart = new Cart();
                existingCart.setUser(user);
                cartRepository.save(existingCart);
                user.setCart(existingCart);
                userRepository.save(user);
            }

            Optional<Product> product = productRepository.findById(productId);

            if (product.isPresent()) {
                Product existingProduct = product.get();


                // Check if the product is already in the cart
                CartItem existingCartItem = cartItemRepository
                        .findByCartAndProduct(existingCart, existingProduct);

                if (existingCartItem != null) {

                    // Product is already in the cart, increase the quantity
                    existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
                    cartItemRepository.save(existingCartItem);
                } else {
                    // Product is not in the cart, create a new cart item
                    cartItemService.saveToCart(existingCart, existingProduct);
                }

                redirectAttributes.addFlashAttribute("success",
                        "Cart item added");

                return "redirect:/user/wishlist";

            } else {
                redirectAttributes.addFlashAttribute("error_message",
                        "Product not found");

                return "redirect:/user/wishlist";
            }
        }
    }


    @GetMapping("/removeFromCart/{id}")
    public String removeFromCart(@PathVariable("id") Long cartItemId, Model model,
                                 Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            String email = principal.getName();
            User user = userRepository.findByEmail(email);
            Cart existingCart = user.getCart();

            if (existingCart != null) {
                Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);

                if (cartItem.isPresent()) {
                    CartItem existingItem = cartItem.get();
                    cartItemRepository.delete(existingItem);
                }
            }

            return "redirect:/user/cart"; // Redirect to the cart page after removal
        }
    }


    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("productId") Long productId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        Cart userCart = null;

        if (user != null) {
            // Retrieve or create user's cart
            List<Cart> userCarts = user.getCarts();

            if (userCarts == null || userCarts.isEmpty()) {
                // Create a new cart if the user doesn't have one
                userCart = new Cart();
                userCart.setUser(user);
            } else {
                // Use the first cart if the user already has one (you might want to handle multiple carts differently)
                userCart = userCarts.get(0);
            }

            // Create a CartItem
            Product product = productService.getProductById(productId);
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1); // Set the quantity as needed

            // Save the Cart entity before associating it with CartItems
            cartRepository.save(userCart);

            // Associate CartItem with Cart
            cartItem.setCart(userCart);

            // Save CartItem to CartItems table

            List<CartItem> cartItems = cartItemRepository.findByCartUser(user);
            double totalPrice = cartItemRepository.sumCartItemsPriceByUser(user);
            userCart.setTotal(totalPrice);
            double newtotal = userCart.getTotal();
            // Pass the cart items to the Thymeleaf template
            model.addAttribute("totalPrice", newtotal);
            model.addAttribute("cartItems", cartItems);

            model.addAttribute("product", product);

            return "/cart/cart";
        } else {
            // Handle the case where the user is null (e.g., redirect to a login page or display an error message).
            return "redirect:/login"; // Example: Redirect to the login page
        }
    }


    @GetMapping("/decreaseQuantity/{id}")
    public String decreaseQty(@PathVariable("id") Long cartItemId, Model model,
                              Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            String email = principal.getName();
            User user = userRepository.findByEmail(email);
            Cart existingCart = user.getCart();

            if (existingCart != null) {

                Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);

                if (cartItem.isPresent()) {

                    CartItem existingItem = cartItem.get();

                    if (existingItem.getQuantity() > 1) {
                        existingItem.setQuantity(existingItem.getQuantity() - 1);

                        cartItemRepository.save(existingItem);
                    }
                }
            }
            return "redirect:/user/cart"; // Redirect to the cart page after removal
        }
    }


    @GetMapping("/addQuantity/{id}")
    public String increase(@PathVariable("id") Long cartItemId, Model model,
                           Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            String email = principal.getName();
            User user = userRepository.findByEmail(email);
            Cart existingCart = user.getCart();
            if (existingCart != null) {

                Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);

                if (cartItem.isPresent()) {
                    CartItem existingItem = cartItem.get();
                    if (existingItem.getQuantity() < 6) {
                        existingItem.setQuantity(existingItem.getQuantity() + 1);
                        cartItemRepository.save(existingItem);
                    } else {
                        redirectAttributes.addFlashAttribute("message", "Oops! Maximum quantity allowed is 6");
                    }
                }
            }
            return "redirect:/user/cart"; // Redirect to the cart page after removal
        }
    }


}


