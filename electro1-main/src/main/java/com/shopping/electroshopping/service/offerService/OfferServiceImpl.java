package com.shopping.electroshopping.service.offerService;

import com.shopping.electroshopping.model.offer.ProductOffer;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.repository.ProductOfferRepository;
import com.shopping.electroshopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Optional;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    ProductOfferRepository productOfferRepository;

    @Autowired
    ProductRepository productRepository;


    @Override
    public boolean addProductOffer(ProductOffer productOffer) {
        Optional<ProductOffer> existingOffer = productOfferRepository
                .findByProductId(productOffer.getProduct().getId());
        Product product = productOffer.getProduct();
        if (existingOffer.isPresent()) {
            return false;
        } else {
            productOffer.getProduct().setIsOffer(true);
            productOffer.getProduct().setDiscountedPrice(productOffer.getProduct().getPrice()*productOffer.getDiscountPercentage()/100);
//            this.price * this.activeOffer.getDiscountPercentage() / 100
            product.setDiscountedPrice(productOffer.getProduct().getPrice() - productOffer.getProduct().getPrice()*productOffer.getDiscountPercentage()/100);
            productOfferRepository.save(productOffer);
            productRepository.save(product);
            return true;
        }
    }


    @Override
    public void deleteOffer(Long productOfferId) {
        ProductOffer offer = productOfferRepository.findByproductOfferId(productOfferId);

        if (offer != null) {
            Product product = offer.getProduct();
            productOfferRepository.deleteById(productOfferId);
            product.setIsOffer(false);
            product.setDiscountedPrice(product.getPrice());
            productRepository.save(product);
        }else {
            throw new IllegalArgumentException("Illegal Product Offer Id: "+productOfferId);
        }
    }


    @Override
    public boolean updateOffer(ProductOffer offer) {
        Optional<ProductOffer> productOffer = productOfferRepository.findByProductId(offer.getProductOfferId());

        if (productOffer.isPresent()) {
            ProductOffer existingOffer = productOffer.get();
            existingOffer.setProductOfferId(offer.getProductOfferId());
            existingOffer.setDiscountPercentage(offer.getDiscountPercentage());
            existingOffer.setCreateDate(offer.getCreateDate());
            existingOffer.setProduct(offer.getProduct());
            existingOffer.setExpirationDate(offer.getExpirationDate());
            productOfferRepository.save(existingOffer);
            return true;
        } else {
            return false;
        }
    }


}
