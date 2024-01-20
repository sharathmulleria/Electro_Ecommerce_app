package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.user.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository  extends JpaRepository<UserAddress,Long> {

    Optional   <List<UserAddress>> findAllByUserId(Long id);

    Optional<UserAddress> findUserAddressByUserIdAndIsDefaultTrue(Long id);

    boolean existsByUserIdAndIsDefaultTrue(Long id);

}
