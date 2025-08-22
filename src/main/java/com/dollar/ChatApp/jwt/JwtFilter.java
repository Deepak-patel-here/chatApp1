package com.dollar.ChatApp.jwt;



import com.dollar.ChatApp.repository.UserRepository;
import com.dollar.ChatApp.service.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService service;

    @Autowired
    ApplicationContext context;

    @Autowired
    private UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");
        String token=null;
        String userId=null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if(token==null){
            Cookie [] cookies= request.getCookies();
            if(cookies!=null){
                for(Cookie cookie:cookies){
                    if("JWT".equals(cookie.getName())){
                        token=cookie.getValue();
                        break;
                    }
                }
            }
        }

        if(token==null){
            filterChain.doFilter(request,response);
            return;
        }
        userId=service.extractUserId(token);

        if(userId!=null && SecurityContextHolder.getContext().getAuthentication()==null){
           var userDetails=userRepository.findById(userId)
                   .orElseThrow(()->new RuntimeException("user not found."));

           if(service.isTokenValid(token,userDetails)){
               UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails,null, Collections.emptyList());
               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authToken);
           }
        }
        filterChain.doFilter(request,response);
    }
}
