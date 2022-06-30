package com.example.demo.constants;

public class SecurityConstants {

    public static final String SECRET = "SecretKeyToGenereateJWTs";
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 864_000_000; // 10 days
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 60*60*1000; // 1 hour

}
