package com.shopping.electroshopping.service.productService;

import com.shopping.electroshopping.dto.ProductDto;
import com.shopping.electroshopping.model.category.Category;
import com.shopping.electroshopping.model.product.Product;
import com.shopping.electroshopping.repository.CategoryRepository;
import com.shopping.electroshopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    // Add products in the admin side.
    @Override
    public void addProduct(ProductDto productDto) {
        Product product = new Product();
        product.setProductName(productDto.getProductName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageName(productDto.getImageName());
        product.setCost(productDto.getCost());
        product.setDiscountedPrice(product.getPrice());

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        product.setCategory(category);
        product.setDiscountedPrice(product.getDiscountedPrice());

        productRepository.save(product);
    }


  //     When adding product, if product already exits it will disable the boolean and save
 //     other details and Save it as a new Product
    public void productAlreadyExistsSaveNew(List<Product> product) {
//        product.setDeleted(false);
        Product product1 = product.get(0);
        product1.setDeleted(false);
        productRepository.save(product1);
    }


    @Override
    public void deleteProductById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("product id not valid"));
        product.setDeleted(true);
        productRepository.save(product);
    }


    @Override
    @Transactional
    public void editproduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            product.setProductName(productDto.getProductName());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setStock(productDto.getStock());
            product.setImageName(productDto.getImageName());
            product.setCost(productDto.getCost());
            product.setDiscountedPrice(product.getPrice());

            productRepository.save(product);
        }
    }


    public List<Product> getProductByName(String productName) {
        return productRepository.findByproductName(productName);
    }


    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        // Return the product if it exists
        return productOptional.orElse(null);
    }


}

