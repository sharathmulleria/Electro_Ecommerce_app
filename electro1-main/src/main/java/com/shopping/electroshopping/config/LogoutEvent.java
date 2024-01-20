package com.shopping.electroshopping.config;

import org.springframework.context.ApplicationEvent;

public class LogoutEvent extends ApplicationEvent {




    private final String sessionId;





    public LogoutEvent(String sessionId) {
        super(sessionId);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
