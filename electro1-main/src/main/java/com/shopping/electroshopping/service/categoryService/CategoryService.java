package com.shopping.electroshopping.service.categoryService;

import com.shopping.electroshopping.dto.CategoryDto;
import com.shopping.electroshopping.model.category.Category;
import com.shopping.electroshopping.model.user.User;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category addcategory(CategoryDto categoryDto);

    public void deleteCategoryById(Long id);

    public void editCategoryByID(Long id, CategoryDto categoryDto);

    Category findByName(String name);

    public Optional<User> findByUserName(String email);

    public List<Category> getCategoryByName(String name);

}
