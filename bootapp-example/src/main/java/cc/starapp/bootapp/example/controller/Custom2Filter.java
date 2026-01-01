package cc.starapp.bootapp.example.controller;

import jakarta.servlet.*;

import java.io.IOException;

public class Custom2Filter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("custom2 filter adddddd.....");
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
