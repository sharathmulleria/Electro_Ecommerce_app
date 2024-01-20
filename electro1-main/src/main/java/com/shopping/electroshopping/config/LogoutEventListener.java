package com.shopping.electroshopping.config;

import org.springframework.context.ApplicationListener;

public class LogoutEventListener implements ApplicationListener<LogoutEvent> {

    @Override
    public void onApplicationEvent(LogoutEvent logoutEvent) {
        String sessionId = logoutEvent.getSessionId();
    }
}
