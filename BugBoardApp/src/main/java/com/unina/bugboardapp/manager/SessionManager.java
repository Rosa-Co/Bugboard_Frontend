package com.unina.bugboardapp.manager;

import com.unina.bugboardapp.model.User;

public class SessionManager {
    private static SessionManager instance;
    private String token;
    private User user;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if(instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }//?
    public void setToken(String token) {
        this.token = token;
    }//?
    public void logout() {
        this.token = null;
        this.user = null;
    }
    public boolean isLoggedIn() {
        return token != null;
    }
    public boolean isAdmin() {
        return user != null && user.getType() == User.UserType.ADMIN;
    }
    public void login(User user,String token) {
        this.user = user;
        this.token = token;
    }


}
