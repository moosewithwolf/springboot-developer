package me.shinseong.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users") //  db 테이블 이름과 이 클래스를 매핑
@Entity // 이 클래스의 용도는 엔티티임을 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED) //Lombok ann. 디폴드생성자 생성
@Getter // getter 메서드들 생성
public class User implements UserDetails { // 스프링 시큐리티 인터페이스 규격 사용

    @Id // 이 필드는 기본키임을 명시
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 생성시 자동1씩 증가
    @Column(name = "id", updatable = false) // 테이블 컬럼 id에 매핑할것임을 명시
    private Long id; // 위 내용을 생성하는 id 변수에 주입

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Builder // lombok, 객체 생성시
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User update(String nickname){
        this.nickname = nickname;
        return this;
    }
}

