| 메서드 (Method)               | 반환 타입 (Return Type)                    | 설명 (Description)                                                                |
| :---------------------------- | :----------------------------------------- | :-------------------------------------------------------------------------------- |
| `getAuthorities()`          | `Collection<? extends GrantedAuthority>` | 사용자가 가지고 있는 권한의 목록을 반환합니다.                                    |
| `getUsername()`             | `String`                                 | 사용자를 식별할 수 있는 고유한 사용자 이름을 반환합니다.                          |
| `getPassword()`             | `String`                                 | 사용자의 암호화된 비밀번호를 반환합니다.                                          |
| `isAccountNonExpired()`     | `boolean`                                | 계정이 만료되지 않았는지 확인하며, 만료되지 않았을 때 `true`를 반환합니다.      |
| `isAccountNonLocked()`      | `boolean`                                | 계정이 잠금되지 않았는지 확인하며, 잠금되지 않았을 때 `true`를 반환합니다.      |
| `isCredentialsNonExpired()` | `boolean`                                | 비밀번호가 만료되지 않았는지 확인하며, 만료되지 않았을 때 `true`를 반환합니다.  |
| `isEnabled()`               | `boolean`                                | 계정이 활성화(사용 가능) 상태인지 확인하며, 사용 가능할 때 `true`를 반환합니다. |
