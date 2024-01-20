package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.offer.ProductOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOfferRepository extends JpaRepository<ProductOffer, Long> {

    Optional<ProductOffer> findByProductId(Long id);

    ProductOffer findByproductOfferId(Long productOfferId);

}
