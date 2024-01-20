package com.shopping.electroshopping.service.basicService;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

public interface BasicService {

    String getFormattedDate(LocalDate date);

    public String getCurrentUsername();

}
