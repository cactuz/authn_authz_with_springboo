package com.example.demo.security;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException, ServletException {
        System.out.println("CustomAuthenticationEntryPoint");

        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(403);
        res.getWriter().write(new JSONObject()//my util class for creating json strings
                .put("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()))
                .put("status", 403)
                .put("message", "Access denied").toString()
                );

    }

}
