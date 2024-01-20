package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT u FROM Category u WHERE u.name LIKE :name%")
    List<Category> findByCategoryName(String name);

    Category findByName(String name);

    List<Category>findAllByDeletedIsFalse();

}
