package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.user.User;
import com.shopping.electroshopping.model.wishList.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishListItem, Long> {

    List<WishListItem> findByUser(User userId);

    @Query("SELECT w FROM WishListItem w WHERE w.user = :user")
    List<WishListItem> findByWishListItemUser(User user);






}
