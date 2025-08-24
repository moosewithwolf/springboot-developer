이 `TokenProvider` 클래스는 JWT(JSON Web Token)의 **생성, 검증, 정보 추출을 전담**하는 매우 중요한 서비스입니다. 전체적인 흐름과 각 라인의 역할을 자세히 설명해 드릴게요.

---

### ## 전체적인 흐름 및 목적 (여권 발급처 비유 🛂)

이 `TokenProvider` 클래스를 **'여권 발급 및 검증처'**라고 생각하시면 쉽습니다.

* **`generateToken()`** : 사용자가 로그인에 성공하면, 이 사용자를 위한 **새로운 여권(토큰)을 발급**합니다.
* **`validateToken()`** : 사용자가 제시한 여권(토큰)이 **위조되지 않았고 유효기간이 남았는지 검증**합니다.
* **`getAuthentication()`** : 유효한 여권(토큰)에서 **사용자의 신원 정보를 읽어와서** 스프링 시큐리티가 이해할 수 있는 **'인증된 사용자' 객체**로 만들어줍니다.
* **`getUserId()`** : 여권(토큰)에서 **사용자의 고유 ID만 빠르게** 뽑아냅니다.

이 모든 과정에 필요한 규칙들(발급처 이름, 비밀 서명 방식 등)은 `JwtProperties`라는 별도의 설정 파일에서 가져옵니다.

---

### ## 라인별 상세 설명

#### **클래스 선언 및 의존성 주입**

**Java**

```
@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;
```

* **`@Service`** : 이 클래스가 스프링의 **서비스 레이어 컴포넌트**임을 나타냅니다.
* **`@RequiredArgsConstructor`** : Lombok 어노테이션으로, `final`이 붙은 필드(`jwtProperties`)를 인자로 받는 생성자를 자동으로 만들어줍니다.
* **`private final JwtProperties jwtProperties`** : JWT의 설정값(issuer, secret_key)들을 담고 있는 `JwtProperties` 객체를 의존성 주입받습니다.

#### **`generateToken()`: 토큰 생성**

**Java**

```
public String generateToken(User user, Duration expiredAt) {
    Date now = new Date();
    return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
}
```

* **목적** : 사용자와 유효기간을 받아 최종 토큰 문자열을 반환하는 공개 메소드입니다.
* **`new Date(now.getTime() + expiredAt.toMillis())`** : 현재 시간에 전달받은 유효기간(`expiredAt`)을 더해서 **토큰의 만료 시간**을 계산합니다.
* **`makeToken(...)`** : 계산된 만료 시간과 사용자 정보를 바탕으로 실제 토큰을 만드는 приват 메소드를 호출합니다.

#### **`makeToken()`: 실제 토큰 제작 과정**

**Java**

```
private String makeToken(Date expiry, User user) {
    Date now = new Date();
    return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
            .setIssuer(jwtProperties.getIssuer()) // 내용 iss : ajufresh@gmail.com
            .setIssuedAt(now)                     // 내용 iat : 현재 시간
            .setExpiration(expiry)                // 내용 exp : 만료 시간
            .setSubject(user.getEmail())          // 내용 sub : 유저의 이메일
            .claim("id", user.getId())            // 클레임 id : 유저 ID
            .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명
            .compact();                           // 토큰 생성
}
```

* **`Jwts.builder()`** : JWT 라이브러리(`jjwt`)를 사용해 토큰 생성을 시작합니다.
* **`.setHeaderParam(...)`** : 헤더의 `typ` 필드를 `JWT`로 설정합니다.
* **`.setIssuer(...)`, `.setIssuedAt(...)`, `.setExpiration(...)`, `.setSubject(...)`** : 페이로드(내용)에 들어갈 **표준 클레임**들을 설정합니다. 각각 발급자, 발급 시간, 만료 시간, 토큰 제목(여기서는 사용자 이메일)을 의미합니다.
* **`.claim("id", user.getId())`** : **비공개 클레임**을 추가합니다. 여기서는 `id`라는 이름으로 사용자의 고유 ID를 토큰에 담았습니다.
* **`.signWith(...)`** : **가장 중요한 서명 과정**입니다. HS256 알고리즘과 우리가 설정한 `secret_key`를 사용해 헤더와 페이로드를 암호화하여 서명을 생성합니다.
* **`.compact()`** : 위에서 설정한 헤더, 페이로드, 서명을 합쳐 `xxxxx.yyyyy.zzzzz` 형태의 최종 JWT 문자열로 만듭니다.

#### **`validateToken()`: 토큰 유효성 검증**

**Java**

```
public boolean validateToken(String token) {
    try {
        Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey()) // 비밀키로 복호화
                .parseClaimsJws(token);
        return true;
    } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
        return false;
    }
}
```

* **목적** : 전달받은 토큰이 위조되지 않았고, 만료되지 않았는지 검증합니다.
* **`Jwts.parser().setSigningKey(...)`** : 토큰을 해석(parse)하기 위해, 서명을 만들 때 사용했던 동일한 `secret_key`를 설정합니다.
* **`.parseClaimsJws(token)`** : 이 메소드가 실질적인 검증을 수행합니다. 만약 토큰의 서명이 다르거나, 만료 시간이 지났거나, 형식이 잘못되었다면 **예외(Exception)를 발생**시킵니다.
* **`try-catch`** : 예외가 발생하면(`catch`) 토큰이 유효하지 않다는 의미이므로 `false`를 반환하고, 예외 없이 성공적으로 파싱되면 `true`를 반환합니다.

#### **`getAuthentication()`: 인증 정보 생성**

**Java**

```
public Authentication getAuthentication(String token) {
    Claims claims = getClaims(token);
    Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

    return new UsernamePasswordAuthenticationToken(
            new org.springframework.security.core.userdetails.User(
                    claims.getSubject(), "", authorities), token, authorities);
}
```

* **목적** : 유효한 토큰으로부터 사용자 정보를 뽑아내어, 스프링 시큐리티가 이해할 수 있는 `Authentication` 객체로 변환합니다.
* **`Claims claims = getClaims(token)`** : 아래에 설명된 `getClaims` 메소드를 호출해 토큰의 페이로드(내용) 부분을 가져옵니다.
* **`new UsernamePasswordAuthenticationToken(...)`** : 사용자 정보(principal), 자격 증명(credentials), 권한(authorities)을 담아 스프링 시큐리티용 인증 객체를 생성합니다. 이 객체가 생성되면, 스프링 시큐리티는 "이 사용자는 인증되었다"고 판단합니다.

#### **`getUserId()` 및 `getClaims()`**

**Java**

```
public Long getUserId(String token) {
    Claims claims = getClaims(token);
    return claims.get("id", Long.class);
}

private Claims getClaims(String token) {
    return Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey())
            .parseClaimsJws(token)
            .getBody();
}
```

* **`getClaims()`** : `validateToken`과 유사하지만, 검증 성공 시 토큰의 `body` (즉, 페이로드/클레임)를 반환하는 내부 헬퍼 메소드입니다.
* **`getUserId()`** : 위 `getClaims`를 통해 페이로드를 가져온 뒤, 우리가 토큰을 만들 때 넣었던 `id` 클레임 값을 꺼내서 반환하는 유틸리티 메소드입니다.
