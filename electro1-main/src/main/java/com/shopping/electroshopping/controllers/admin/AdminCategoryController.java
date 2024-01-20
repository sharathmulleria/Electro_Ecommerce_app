package com.shopping.electroshopping.controllers.admin;

import com.shopping.electroshopping.dto.CategoryDto;
import com.shopping.electroshopping.dto.ProductDto;
import com.shopping.electroshopping.exeption.DuplicateException;
import com.shopping.electroshopping.model.category.Category;
import com.shopping.electroshopping.repository.CategoryRepository;
import com.shopping.electroshopping.service.categoryService.CategoryService;
import com.shopping.electroshopping.service.categoryService.CategoryServiceImpl;
import com.shopping.electroshopping.service.productService.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/categoryList")
    public String getProductListPage(Model model) {
        List<Category>categoryList = categoryRepository.findAllByDeletedIsFalse();
        model.addAttribute("listcategory", categoryList);
        return "/category/listCategory";
    }


    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable(value = "id") long id) {
        System.out.println("******************************************"+id);
        categoryService.deleteCategoryById(id);
        return "redirect:/admin/categoryList";
    }


    @GetMapping("/updateCategory/{id}")
    public String updatetheCategoryForm(@PathVariable(value = "id") long CategoryId, Model model) {
        Category category = categoryRepository.findById(CategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + CategoryId));
        model.addAttribute("category", category);
        model.addAttribute("categoryId", CategoryId);
        return "/category/updateCategory";
    }


    @PostMapping("/updateCategory/{id}")
    public String updateCategory(Model model, @PathVariable("id") Long categoryId
                                    , @ModelAttribute("category") CategoryDto categoryDto) {
        categoryService.editCategoryByID(categoryId, categoryDto);
        if (categoryDto == null) {
            return model.addAttribute("error"
                                        , "Category with Same Name Already Exists").toString();
        }
        return "redirect:/admin/categoryList";
    }


    @ModelAttribute("category")
    public CategoryDto categoryDto() {
        return new CategoryDto();
    }


    @GetMapping("/addCategory")
    public String showAddCategoryPage(Model model) {

        return "/category/addCategory";
    }


    @PostMapping("/addCategoryForm")
    public String addingCategoryIntoDatabase(@ModelAttribute("category")
                                             CategoryDto categoryDto, Model model) {

        try {
            categoryService.addcategory(categoryDto);
            return "redirect:/admin/categoryList";
        } catch (DuplicateException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/categoryList";
        }
    }


    @GetMapping("/categorySearch")
    public String searchCategory(@RequestParam("name") String name, Model model) {
        List<Category> list = categoryService.getCategoryByName(name);
        model.addAttribute("listcategory", list);
        return "/category/listcategory";
    }


}
