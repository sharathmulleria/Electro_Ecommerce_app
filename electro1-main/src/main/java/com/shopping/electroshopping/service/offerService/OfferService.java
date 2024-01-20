package com.shopping.electroshopping.service.offerService;

import com.shopping.electroshopping.model.offer.ProductOffer;

import java.util.Optional;

public interface OfferService {

    public boolean addProductOffer(ProductOffer productOffer);

    void deleteOffer(Long productOfferId);

    boolean updateOffer(ProductOffer offer);



}
