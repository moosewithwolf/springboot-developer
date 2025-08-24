```java
// [사용자 정의]: 개발자가 직접 생성한 패키지 경로
package me.shinseong.springbootdeveloper.config.jwt;

// [외부 라이브러리]: JWT(JSON Web Token)를 생성하고 검증하기 위한 'jjwt' 라이브러리
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
// [외부 라이브러리]: 반복적인 코드를 줄여주는 'Lombok' 라이브러리
import lombok.RequiredArgsConstructor;
// [사용자 정의]: 개발자가 직접 만든 User 엔티티 클래스
import me.shinseong.springbootdeveloper.domain.User;
// [스프링 프레임워크]: 스프링 시큐리티의 인증 정보를 담는 객체들
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// [스프링 프레임워크]: 스프링의 서비스 컴포넌트를 나타내는 어노테이션
import org.springframework.stereotype.Service;

// [Java 기본 문법]: 자바 8부터 도입된 시간/기간을 다루는 클래스
import java.time.Duration;
// [Java 기본 문법]: 자바의 컬렉션(List, Set 등)과 관련된 유틸리티 클래스
import java.util.Collections;
import java.util.Date;
import java.util.Set;

// [외부 라이브러리]: Lombok 어노테이션. final 필드를 인자로 받는 생성자를 자동으로 만들어줌 (의존성 주입용)
@RequiredArgsConstructor
// [스프링 프레임워크]: 이 클래스가 스프링의 서비스(비즈니스 로직 담당) 컴포넌트임을 나타냄
@Service
// [Java 기본 문법]: public 접근 제어자와 class 키워드를 사용한 클래스 선언
// [사용자 정의]: 개발자가 직접 만든 클래스 이름 'TokenProvider'
public class TokenProvider {

    // [Java 기본 문법]: private 접근 제어자와 final 키워드로 불변 필드 선언
    // [사용자 정의]: 개발자가 만든 설정 클래스 'JwtProperties'와 변수명 'jwtProperties'
    private final JwtProperties jwtProperties;

    // [Java 기본 문법]: public 메소드 선언. 반환 타입은 String.
    // [사용자 정의]: 개발자가 만든 메소드 이름 'generateToken'과 파라미터 변수명 'user', 'expiredAt'
    public String generateToken(User user, Duration expiredAt) {
        // [Java 기본 문법]: Date 클래스 객체 생성
        Date now = new Date();
        // [Java 기본 문법]: return 키워드를 사용해 메소드 결과 반환
        // [사용자 정의]: 아래에 정의된 makeToken 메소드 호출
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // [Java 기본 문법]: private 메소드 선언
    // [사용자 정의]: 개발자가 만든 메소드 이름 'makeToken'
    private String makeToken(Date expiry, User user) {
        // [Java 기본 문법]: Date 클래스 객체 생성
        Date now = new Date();
        // [외부 라이브러리]: 'jjwt' 라이브러리의 JWT 빌더 시작
        return Jwts.builder()
                // [외부 라이브러리]: JWT 헤더의 typ(타입)을 'JWT'로 설정
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                // [외부 라이브러리]: JWT 페이로드의 iss(발급자)를 설정 파일 값으로 설정
                .setIssuer(jwtProperties.getIssuer())
                // [외부 라이브러리]: JWT 페이로드의 iat(발급 시간)을 현재 시간으로 설정
                .setIssuedAt(now)
                // [외부 라이브러리]: JWT 페이로드의 exp(만료 시간)을 파라미터 값으로 설정
                .setExpiration(expiry)
                // [외부 라이브러리]: JWT 페이로드의 sub(토큰 제목)을 유저 이메일로 설정
                .setSubject(user.getEmail())
                // [외부 라이브러리]: JWT 페이로드에 비공개 클레임 'id'를 추가
                .claim("id", user.getId())
                // [외부 라이브러리]: HS256 알고리즘과 비밀 키를 사용해 서명 생성
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                // [외부 라이브러리]: JWT를 문자열 형태로 최종 생성
                .compact();
    }

    // [Java 기본 문법]: public 메소드 선언. 반환 타입은 boolean.
    // [사용자 정의]: 개발자가 만든 메소드 이름 'validateToken'
    public boolean validateToken(String token) {
        // [Java 기본 문법]: 예외 처리를 위한 try-catch 구문
        try {
            // [외부 라이브러리]: 'jjwt' 라이브러리의 JWT 파서 시작
            Jwts.parser()
                    // [외부 라이브러리]: 서명 검증을 위해 비밀 키 설정
                    .setSigningKey(jwtProperties.getSecretKey())
                    // [외부 라이브러리]: 토큰 파싱 및 검증. 실패 시 예외 발생.
                    .parseClaimsJws(token);
            // [Java 기본 문법]: 예외가 없으면 true 반환
            return true;
        } catch (Exception e) { // [Java 기본 문법]: 모든 예외를 잡음
            // [Java 기본 문법]: 예외 발생 시 false 반환
            return false;
        }
    }

    // [Java 기본 문법]: public 메소드 선언
    // [스프링 프레임워크]: 스프링 시큐리티의 인증 정보 객체 'Authentication'을 반환
    // [사용자 정의]: 개발자가 만든 메소드 이름 'getAuthentication'
    public Authentication getAuthentication(String token) {
        // [외부 라이브러리]: JWT의 페이로드(클레임)를 담는 객체
        Claims claims = getClaims(token);
        // [Java 기본 문법]: Set 컬렉션 생성
        // [스프링 프레임워크]: 스프링 시큐리티의 권한 객체 'SimpleGrantedAuthority'
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        // [Java 기본 문법]: new 키워드로 객체 생성 및 반환
        // [스프링 프레임워크]: 스프링 시큐리티의 인증 객체 'UsernamePasswordAuthenticationToken' 생성
        return new UsernamePasswordAuthenticationToken(
                // [스프링 프레임워크]: 스프링 시큐리티의 UserDetails 구현체 User 객체 생성
                new org.springframework.security.core.userdetails.User(
                        claims.getSubject(), "", authorities), token, authorities);
    }

    // [Java 기본 문법]: public 메소드 선언. 반환 타입은 Long.
    // [사용자 정의]: 개발자가 만든 메소드 이름 'getUserId'
    public Long getUserId(String token) {
        // [외부 라이브러리]: JWT의 페이로드(클레임)를 담는 객체
        Claims claims = getClaims(token);
        // [외부 라이브러리]: 클레임에서 'id' 값을 Long 타입으로 가져옴
        return claims.get("id", Long.class);
    }

    // [Java 기본 문법]: private 메소드 선언
    // [사용자 정의]: 개발자가 만든 메소드 이름 'getClaims'
    private Claims getClaims(String token) {
        // [외부 라이브러리]: 'jjwt' 라이브러리를 사용해 토큰을 파싱하고 페이로드(body)를 반환
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
```
