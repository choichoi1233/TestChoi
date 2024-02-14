package com.example.choitest1.config;


import com.example.choitest1.model.Constants;
import com.example.choitest1.model.CustomException;
import com.example.choitest1.model.JwtService;
import com.example.choitest1.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API 요청시 메소드별로 개별 데이터가 필요한경우
 * HEADER에 코드값을 읽고 누구의 토큰인지 체크함.
 */
@Configuration
@Log4j2
@RequiredArgsConstructor
public class AuthWebConfig implements WebMvcConfigurer {

    private final JwtService userJwtService = new JwtService();

    /**
     * 각 api의 path를 읽고 인증 로직 구분
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdmInterceptor()).addPathPatterns("/api/adm/**");  // adm => 관리자의 경우
        registry.addInterceptor(new StudentInterceptor()).addPathPatterns("/api/student/**");  // student => 학생의 경우
        registry.addInterceptor(new ProInterceptor()).addPathPatterns("/api/pro/**");  // pro => 교수의 경우
    }

    class AdmInterceptor implements HandlerInterceptor {
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            // JWT 토큰을 가져옵니다.
            String token = request.getHeader("Authorization");
            log.info("token = {}  || url = {}", token, request.getRequestURI());

            if (!StringUtils.hasText(token))
                throw new CustomException("인가되지 않은 요청", HttpStatus.UNAUTHORIZED.toString());

            token = token.replaceAll("[\\\\\"]", "");
            Jws<Claims> claims = null;
            User user = null;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                claims = userJwtService.verifyToken(token);
                user = objectMapper.readValue(claims.getBody().getSubject(), User.class);
                if (!user.getRole().equals(Constants.USER_RULE_ADMIN))
                    throw new CustomException("인가되지 않은 요청", HttpStatus.UNAUTHORIZED.toString());
            } catch (ExpiredJwtException exjwt) {
                throw new CustomException("엑세스 토큰 만료", HttpStatus.UNAUTHORIZED.toString());
            } catch (JwtException e) {
                throw new CustomException("엑세스 토큰 복호화 실패", HttpStatus.UNAUTHORIZED.toString());
            }
            request.setAttribute("user", user);
            return true;
        }
    }

    class StudentInterceptor implements HandlerInterceptor {
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            // JWT 토큰을 가져옵니다.
            String token = request.getHeader("Authorization");
            log.info("token = {}  || url = {}", token, request.getRequestURI());
            if (!StringUtils.hasText(token))
                throw new CustomException("인가되지 않은 요청", HttpStatus.UNAUTHORIZED.toString());
            token = token.replaceAll("[\\\\\"]", "");
            Jws<Claims> claims = null;
            User user = null;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                claims = userJwtService.verifyToken(token);
                user = objectMapper.readValue(claims.getBody().getSubject(), User.class);
                if (!user.getRole().equals(Constants.USER_RULE_STUDENT))
                    throw new CustomException("인가되지 않은 요청", HttpStatus.UNAUTHORIZED.toString());
            } catch (ExpiredJwtException exjwt) {
                throw new CustomException("엑세스 토큰 만료", HttpStatus.UNAUTHORIZED.toString());
            } catch (JwtException e) {
                throw new CustomException("엑세스 토큰 복호화 실패", HttpStatus.UNAUTHORIZED.toString());
            }
            request.setAttribute("user", user);
            return true;
        }
    }

    class ProInterceptor implements HandlerInterceptor {
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            // JWT 토큰을 가져옵니다.
            String token = request.getHeader("Authorization");
            log.info("token = {}  || url = {}", token, request.getRequestURI());
            if (!StringUtils.hasText(token))
                throw new CustomException("인가되지 않은 요청", HttpStatus.UNAUTHORIZED.toString());
            token = token.replaceAll("[\\\\\"]", "");
            Jws<Claims> claims = null;
            User user = null;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                claims = userJwtService.verifyToken(token);
                user = objectMapper.readValue(claims.getBody().getSubject(), User.class);
                if (!user.getRole().equals(Constants.USER_RULE_PRO))
                    throw new CustomException("인가되지 않은 요청", HttpStatus.UNAUTHORIZED.toString());
            } catch (ExpiredJwtException exjwt) {
                throw new CustomException("엑세스 토큰 만료", HttpStatus.UNAUTHORIZED.toString());
            } catch (JwtException e) {
                throw new CustomException("엑세스 토큰 복호화 실패", HttpStatus.UNAUTHORIZED.toString());
            }
            request.setAttribute("user", user);
            return true;
        }
    }
}
