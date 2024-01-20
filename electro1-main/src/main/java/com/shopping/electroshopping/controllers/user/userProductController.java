package com.shopping.electroshopping.controllers.user;

import com.shopping.electroshopping.model.category.Category;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.repository.CategoryRepository;
import com.shopping.electroshopping.repository.ProductRepository;
import com.shopping.electroshopping.service.productService.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/user")
@Controller
public class userProductController {
    @Autowired
    ProductServiceImpl productService;
@Autowired
    ProductRepository productRepository;
@Autowired
    CategoryRepository categoryRepository;
    @GetMapping("/productView")
    public String viewProductDetail(@RequestParam("productId") Long productId, Model model) {
        // Retrieve the product details based on productId
        Product product = productService.getProductById(productId);
        List<Product> productList = productRepository.findAll();

        // Add the product to the model for use in the view
        model.addAttribute("product", product);
        model.addAttribute("listProducts", productList);

        // Return the product detail view template
        return "/product/productDetailView";
    }



    @GetMapping("/dellCollection")
    public String dellCollection(Model model)
    {
        List<Product> productList=productRepository.findAll();
        List<Category> categories=categoryRepository.findAll();
        model.addAttribute("listProducts",productList);
        model.addAttribute("categoryList",categories);
        return "/product/dellCollection";
    }
    @GetMapping("/samsungCollection")
    public String samsungCollection(Model model)
    {
        List<Product> productList=productRepository.findAll();
        List<Category> categories=categoryRepository.findAll();
        model.addAttribute("listProducts",productList);
        model.addAttribute("categoryList",categories);
        return "/product/samsungCollection";
    }
    @GetMapping("/hpCollection")
    public String hpCollection(Model model)
    {
        List<Product> productList=productRepository.findAll();
        List<Category> categories=categoryRepository.findAll();
        model.addAttribute("listProducts",productList);
        model.addAttribute("categoryList",categories);
        return "/product/hpCollection";
    }



}
