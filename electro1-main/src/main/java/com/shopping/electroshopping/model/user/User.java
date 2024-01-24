package com.shopping.electroshopping.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopping.electroshopping.dto.UserSignUpDto;
import com.shopping.electroshopping.model.Order.Order;
import com.shopping.electroshopping.model.cart.Cart;
import com.shopping.electroshopping.model.role.Role;
import com.shopping.electroshopping.model.wallet.Wallet;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Validated
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name")
    @NotNull
    private String userName;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "password")
    @NotEmpty(message = "password is required")
    @Size(min=6, message = "Password must be at least 6 characters")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    private boolean isBlocked;

    private boolean verified;

    private LocalDate joinDate;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cart> carts;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "default_address")
    private UserAddress defaultAddress;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns =
                            @JoinColumn(name = "user_id", referencedColumnName = "id"),
                                         inverseJoinColumns = @JoinColumn(name = "role_id",
                                                                referencedColumnName = "id"))
    private Collection<Role> roles;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAddress> addresses;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @ToString.Exclude
    private Cart cart;

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Wallet wallet;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;



    public User(String userName, String email,
                String password, String phoneNumber, Collection<Role> roles) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }


    public User(String email, String otp, Collection<Role> roles) {
        this.email = email;
        this.roles = roles;
    }


    public User(String email, String otp,
                LocalDate joinDate, Collection<Role> roles) {
        this.email = email;
        this.joinDate = joinDate;
        this.roles = roles;
    }

    public User(UserSignUpDto userSignUpDto){
        this.email = userSignUpDto.getEmail();
        this.password = userSignUpDto.getPassword();
    }


}
