package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.wishList.WishListItem;
import com.shopping.electroshopping.repository.ProductRepository;
import com.shopping.electroshopping.repository.UserRepository;
import com.shopping.electroshopping.repository.WishListRepository;
import com.shopping.electroshopping.service.productService.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class wishlistController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WishListRepository wishlistRepository;

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    //Show wishlist page
    @GetMapping("/wishlist")
    public String addToWishlist( Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        List<WishListItem> wishListItems = wishlistRepository.findByWishListItemUser(user);
        model.addAttribute("wishListItem", wishListItems);
        return "/user/wishlist";
    }

    @GetMapping("/wishlistDelete/{id}")
    public String removeWishlistItem(@PathVariable("id") Long id){
        this.wishlistRepository.deleteById(id);


        return "redirect:/user/wishlist";
    }


    @GetMapping("/addToWishList")
    public String addToWishList(@RequestParam("productId") Long productId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        Product product = productService.getProductById(productId);

        WishListItem wishListItem = new WishListItem();
        wishListItem.setProduct(product);
        wishListItem.setUser(user);
        wishListItem.setProduct(product);
        List<WishListItem> item = wishlistRepository.findByUser(user);
        for (WishListItem items : item){
            if (items.getProduct().equals(wishListItem.getProduct())){
                return "redirect:/";
            }
        }
        wishlistRepository.save(wishListItem);
        List<Product> productList = productRepository.findAll();
        model.addAttribute("listProduct", productList);

        return "redirect:/";
    }





}
