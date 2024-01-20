package com.shopping.electroshopping.repository;


import com.shopping.electroshopping.model.accounts.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {

}
