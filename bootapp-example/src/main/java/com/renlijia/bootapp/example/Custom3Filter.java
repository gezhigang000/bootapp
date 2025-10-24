package com.renlijia.bootapp.example;

import jakarta.servlet.*;

import java.io.IOException;

public class Custom3Filter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("ooo custom3 filter.....");
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
