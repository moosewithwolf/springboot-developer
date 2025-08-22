package me.shinseong.springbootdeveloper.repository;

import me.shinseong.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JPA는 '메서드 이름으로 쿼리를 생성하는 규칙(Query Derivation)'을 약속으로 정해두었다.
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 1 email로 사용자 정보를 가져옴
    // 함수명을 findBy...로 지어야함
}
