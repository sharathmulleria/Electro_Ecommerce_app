package com.shopping.electroshopping.controllers.admin;

import com.shopping.electroshopping.dto.ProductDto;
import com.shopping.electroshopping.model.offer.ProductOffer;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.repository.ProductOfferRepository;
import com.shopping.electroshopping.repository.ProductRepository;
import com.shopping.electroshopping.service.offerService.OfferService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminOfferController {


    @Autowired
    ProductOfferRepository productOfferRepository;

    @Autowired
    OfferService offerService;

    @Autowired
    ProductRepository productRepository;


    @GetMapping("/listOffer")
    public String showOffers(Model model) {
        Optional<List<ProductOffer>> activeOffers = Optional.of(productOfferRepository.findAll());
        if (activeOffers.isPresent()){
            model.addAttribute("productOffer", activeOffers.get());
        }


        return "/offer/offer_list";
    }

    @GetMapping("/addProductOffer")
    public String addProductOffer(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("productOffer", new ProductOffer());
        return "/offer/add_offer";
    }

    @PostMapping("/offerAdd")
    public String offerAdd(@ModelAttribute("productOffer") ProductOffer productOffer, Model model, RedirectAttributes redirectAttributes) {
        if (offerService.addProductOffer(productOffer)) {
            redirectAttributes.addFlashAttribute("success", "Offer added successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Product have already a offer");
        }

        return "redirect:/admin/listOffer";
    }

    @GetMapping("/deleteOffer/{id}")
    public String deleteOffer(@PathVariable("id") Long productOfferId, RedirectAttributes redirectAttributes) {
        offerService.deleteOffer(productOfferId);
        return "redirect:/admin/listOffer";
    }

    @GetMapping("/updateOffer/{id}")
    public String updateOfferShow(@PathVariable("id") Long productOfferId, Model model){
        ProductOffer offer = productOfferRepository.findById(productOfferId)
                .orElseThrow(() -> new IllegalArgumentException("invalid offer Id: " + productOfferId));
        model.addAttribute("productOffer", offer);
        model.addAttribute("allProducts", productRepository.findAll());
        return "/offer/update_offer";
    }

    @PostMapping("/offerUpdate/{id}")
    public String updateOffer(@ModelAttribute("productOffer") ProductOffer productOffer, @PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        productOffer.setProductOfferId(id);
        offerService.updateOffer(productOffer);
            redirectAttributes.addFlashAttribute("update", "Update successfull");


        return "redirect:/admin/listOffer";
    }




}
