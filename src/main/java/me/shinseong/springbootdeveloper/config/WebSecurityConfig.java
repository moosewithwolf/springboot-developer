package me.shinseong.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.shinseong.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    // 1 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }
    // 2 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests() // 3 인증, 인가 설정
                .requestMatchers("/login", "/signup", "/user").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin() // 4 폼 기반 로그인 설정
                .loginPage("/login")
                .defaultSuccessUrl("/articles")
                .and()
                .logout() // 5 로그아웃 설정
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .and()
                .csrf().disable() // 6 csrf 비활성화
                .build();
    }
    // 7 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService) // 8 사용자 정보 서비스 설정
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }
    // 9 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
//
//package me.shinseong.springbootdeveloper.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//@RequiredArgsConstructor
//@Configuration
//public class WebSecurityConfig {
//
//    // UserDetailService는 이제 Spring Security가 자동으로 인식하여 사용합니다.
//
//    // 1. 스프링 시큐리티 기능 비활성화 (이 부분은 거의 동일)
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers("/static/**");
//    }
//
//    // 2. 특정 HTTP 요청에 대한 웹 기반 보안 구성 (가장 많이 바뀐 부분)
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth // authorizeRequests() -> authorizeHttpRequests()
//                        .requestMatchers("/login", "/signup", "/user").permitAll()
//                        .anyRequest().authenticated())
//                .formLogin(form -> form // .and() 없이 람다로 설정
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/articles"))
//                .logout(logout -> logout // .and() 없이 람다로 설정
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true))
//                .csrf(csrf -> csrf.disable()); // .and() 없이 람다로 설정
//
//        return http.build();
//    }
//
//    // 3. 인증 관리자 설정은 대부분 자동 구성으로 대체됨
//    // UserDetailsService와 PasswordEncoder가 Bean으로 등록되어 있으면 Spring이 알아서 처리합니다.
//    // 따라서 explicit하게 AuthenticationManager Bean을 설정할 필요가 없는 경우가 많습니다.
//
//    // 4. 패스워드 인코더로 사용할 빈 등록 (이 부분은 동일)
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}