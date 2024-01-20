package com.shopping.electroshopping.service.categoryService;

import com.shopping.electroshopping.dto.CategoryDto;
import com.shopping.electroshopping.exeption.DuplicateException;
import com.shopping.electroshopping.model.category.Category;
import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.repository.CategoryRepository;
import com.shopping.electroshopping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;


    @Override
    public Category addcategory(CategoryDto categoryDto) {
        Category existCategory = categoryRepository.findByName(categoryDto.getName());
        if (existCategory == null) {
            Category category = new Category();
            category.setName(categoryDto.getName());
            category.setDescription(categoryDto.getDescription());
            return categoryRepository.save(category);
        }
        if (existCategory.getName().equals(categoryDto.getName())){
            existCategory.setDeleted(false);
            categoryRepository.save(existCategory);
        }
        throw new DuplicateException("Category with same name already exist");
    }


    @Override
    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Category Id not found"));
        category.setDeleted(true);
        categoryRepository.save(category);
    }


    @Override
    @Transactional
    public void editCategoryByID(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            category.setName(categoryDto.getName());
            category.setDescription(categoryDto.getDescription());
            categoryRepository.save(category);
        }
    }


    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }


    @Override
    public Optional<User> findByUserName(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }


    public List<Category> getCategoryByName(String name) {
        return categoryRepository.findByCategoryName(name);
    }


}
