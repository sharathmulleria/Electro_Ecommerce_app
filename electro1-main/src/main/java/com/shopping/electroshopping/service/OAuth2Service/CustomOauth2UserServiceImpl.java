//package com.shopping.electroshopping.service.OAuth2Service;
//
//import com.shopping.electroshopping.model.user.User;
//import com.shopping.electroshopping.repository.UserRepository;
//import com.shopping.electroshopping.service.userservice.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Map;
//
//@Service
//public class CustomOauth2UserServiceImpl implements CustomeOauth2UserService {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        Map<String, Object> attribute = authentication.getPrincipal().getAttributes();
//        String email = (String) attribute.get("email");
//        String name = (String) attribute.get("name");
//
//        String userNameAttributeName = userRequest.getClientRegistration()
//                .getProviderDetails()
//                .getUserInfoEndpoint()
//                .getUserNameAttributeName();
//
//        OAuth2User oAuth2User = new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
//                attribute,userNameAttributeName
//        );
//
//
//
//
//
//        return null;
//    }
//
//
//
//
//
//
//
//
//}
