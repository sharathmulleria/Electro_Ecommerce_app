package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT u FROM Product u WHERE u.productName LIKE :productName%")
    List<Product> findByproductName(String productName);

    List<Product> findAllByDeletedIsFalse();

}
