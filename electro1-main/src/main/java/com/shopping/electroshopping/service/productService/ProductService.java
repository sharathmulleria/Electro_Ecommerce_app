package com.shopping.electroshopping.service.productService;

import com.shopping.electroshopping.dto.ProductDto;
import com.shopping.electroshopping.model.product.Product;

import java.util.List;

public interface ProductService {


    void addProduct(ProductDto productDto);


    public void deleteProductById(long id);


    void editproduct(Long id, ProductDto productDto);


    Product getProductById(Long productId);


    public void productAlreadyExistsSaveNew(List<Product> product);

}
