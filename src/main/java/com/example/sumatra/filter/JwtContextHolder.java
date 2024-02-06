package com.example.sumatra.filter;

import io.jsonwebtoken.Claims;

public class JwtContextHolder {

    private static final ThreadLocal<Claims> CONTEXT = new ThreadLocal<>();

    public static void setClaims(Claims claims) {
        CONTEXT.set(claims);
    }

    public static Claims getClaims() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
