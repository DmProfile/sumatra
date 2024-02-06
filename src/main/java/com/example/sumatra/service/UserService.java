package com.example.sumatra.service;

import com.example.sumatra.service.request.UserRegistrationRequest;

public interface UserService {
    boolean registerUser(UserRegistrationRequest event);
}
