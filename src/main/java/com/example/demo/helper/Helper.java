package com.example.demo.helper;

import java.util.List;

public class Helper {

    public static boolean isAdmin(List<String> roles) {
        return roles.contains("ROLE_ADMIN");
    }

}
