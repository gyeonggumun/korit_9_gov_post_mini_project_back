package com.korit.post_mini_project_back.filter;

import com.korit.post_mini_project_back.entity.User;
import com.korit.post_mini_project_back.jwt.JwtTokenProvider;
import com.korit.post_mini_project_back.security.PrincipalUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
//    private final UserMapper userMapper;

    //OncePerRequestFilter - 인증은 한번만 일어나면 됨 - 한 요청당 jwt 한번만 확인하겠다

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) { // if문에 걸리면 다음 filter로 그냥 리턴해줘서 다음 filter로 통과
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = bearerToken.replaceAll("Bearer ", ""); // 토큰 헤체작업

        if (!jwtTokenProvider.validateToken(accessToken)) {  // 유효하지 않으면 다음필터로 통과
            filterChain.doFilter(request, response);
            return;
        }

        int userId = jwtTokenProvider.getUserId(accessToken);
        User foundUser = null;
        if (foundUser == null) {
            filterChain.doFilter(request, response);
            return;
        }

        PrincipalUser principalUser = new PrincipalUser(foundUser);
        String password = "";
        Collection<? extends GrantedAuthority> authorities = principalUser.getAuthorities();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalUser, password, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}