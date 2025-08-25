네, 당연히 가능합니다. 소셜 로그인으로 받아온 기본 정보(이름) 외에, 우리 서비스에만 필요한 별도의 이름이나 추가 정보를 받도록 설계하는 것은 매우 일반적인 구현 방식입니다.

이 과정은 보통 **최초 소셜 로그인 시, 추가 정보 입력 페이지로 이동**시켜서 처리합니다.

### ## 구현 메커니즘 ⚙️

1. **신규 사용자 식별** : `OAuth2UserCustomService`에서 구글로부터 받은 이메일로 우리 DB를 조회했을 때, 해당 사용자가 **신규 사용자**라는 것을 확인합니다.
2. **임시 권한 부여** : 이 신규 사용자에게는 아직 회원가입이 완료되지 않았다는 의미로, `ROLE_GUEST`와 같은 **임시 역할(Role)**을 부여합니다.
3. **추가 정보 페이지로 리다이렉트** : 로그인 성공 후, 사용자의 권한이 `ROLE_GUEST`인 경우, 시스템은 일반 서비스 페이지가 아닌 **'추가 정보 입력' 페이지**로 강제 리다이렉트시킵니다.
4. **정보 입력 및 저장** : 사용자는 이 페이지에서 우리 서비스에서 사용할 정확한 이름(닉네임 등)을 입력하고 저장합니다.
5. **정식 권한으로 변경** : 정보 저장이 완료되면, 사용자의 역할(Role)을 `ROLE_GUEST`에서 `ROLE_USER`와 같은 **정식 역할**로 업데이트합니다.
6. **정상 서비스 이용** : 이제 사용자는 정식 회원이 되어 모든 서비스를 정상적으로 이용할 수 있습니다.

---

### ## 코드 수정 방향 📝

이 로직을 구현하기 위해 기존 `OAuth2UserCustomService`를 다음과 같이 수정할 수 있습니다.

**1. `User` 엔티티에 Role 필드 추가**

**Java**

```
@Entity
public class User {
    // ...
    private String role; // 예: "ROLE_GUEST", "ROLE_USER"
}
```

**2. `OAuth2UserCustomService` 수정**
`saveOrUpdate` 로직을 수정하여, 신규 사용자는 `ROLE_GUEST`로 저장하도록 변경합니다.

**Java**

```
private User saveOrUpdate(OAuth2User oAuth2User) {
    // ...
    User user = userRepository.findByEmail(email)
            .map(entity -> entity.update(name)) // 기존 회원은 이름만 업데이트
            .orElse(User.builder()              // 신규 회원은
                    .email(email)
                    .nickname(name) // 구글 이름은 일단 닉네임으로 저장
                    .role("ROLE_GUEST") // 임시 역할 부여
                    .build()
            );
    return userRepository.save(user);
}
```

**3. 로그인 성공 후 리다이렉트 로직 추가**
`SecurityConfig`에서 로그인 성공 핸들러(`AuthenticationSuccessHandler`)를 커스텀으로 구현하여, 사용자의 권한이 `ROLE_GUEST`이면 추가 정보 입력 페이지로, `ROLE_USER`이면 메인 페이지로 보내도록 분기 처리를 해야 합니다.

이러한 "온보딩(Onboarding)" 절차를 통해 소셜 로그인의 편리함과 서비스에 필요한 상세 정보를 모두 확보할 수 있습니다.

[A video from the search results discusses custom login and registration with Spring Security.](https://www.youtube.com/watch?v=yQORDhvU_Kw) 이 영상은 OAuth 2.0을 사용하여 커스텀 로그인 및 회원가입 흐름을 구현하는 방법을 설명하여, 추가 정보를 받는 과정을 이해하는 데 도움이 될 수 있습니다.




### ## '쿼리 파라미터' 방식의 장단점 🤔

* **장점** :
* 구현이 간단하고, OAuth2 표준 흐름과 잘 맞습니다.
* 대부분의 싱글 페이지 애플리케이션(SPA)에서 JavaScript가 URL에서 토큰을 추출해 사용하기 편리합니다.
* **단점** :
* **보안상 취약** : URL에 노출된 토큰이 브라우저 히스토리, 서버 로그, 레퍼러(referrer) 헤더 등에 남을 수 있습니다. 공격자가 이를 탈취할 위험이 있습니다.
* **단일 토큰 전달** : 한 번에 하나의 토큰만 전달할 수 있어, 액세스 토큰과 리프레시 토큰을 모두 보내는 것이 어렵습니다.

---

### ## 더 안전하고 권장되는 방식 ✅

따라서, 실제 프로덕션 환경에서는 다음과 같은 방식을 더 많이 사용합니다.

1. **`HttpOnly` 쿠키 사용** : 리다이렉트 시 토큰을 URL에 노출하는 대신, `HttpOnly` 속성을 가진 쿠키에 저장하여 XSS 공격으로부터 토큰을 보호합니다.
2. **응답 본문 전달** : 리다이렉트 대신, 로그인 성공 후 토큰을 JSON 응답 본문에 담아 보내는 별도의 API 엔드포인트를 사용합니다. (이 경우 프론트엔드가 해당 API를 호출해야 합니다.)

요약하자면, 지금 방식은 학습이나 간단한 프로젝트에서는 충분하지만, 더 높은 수준의 보안이 요구되는 서비스에서는 **쿠키나 응답 본문을 통한 토큰 전달** 방식을 채택하는 것이 좋습니다.
