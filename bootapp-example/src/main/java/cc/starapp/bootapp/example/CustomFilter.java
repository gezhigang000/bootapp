package cc.starapp.bootapp.example;

import jakarta.servlet.*;

import java.io.IOException;

public class CustomFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("ooo custom filter.....");
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
