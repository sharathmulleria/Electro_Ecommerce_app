package com.shopping.electroshopping.repository;

import com.shopping.electroshopping.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email LIKE :email%")
    List<User> findByName(String email);

    User findByPhoneNumber(String phoneNumber);

    Optional<User> findById(Long userId);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM User c WHERE c.email = ?1")
    public User findByUserEmail(String email);

    public User findByResetPasswordToken(String token);

    Long findUserIdByEmail(String email);

    @Query("UPDATE User u SET u.isBlocked = ?1 WHERE u.email = ?2")
    @Modifying
    public  void updateBlocked(boolean isBlocked, String email);

}
