package com.shopping.electroshopping.service.wishlistService;

import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.repository.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishlistServiceImpl implements WishlistService {


    public boolean isStockAvailable(Product product){


        return true;
    }




}
