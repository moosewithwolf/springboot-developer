롬복(Lombok)에서 자주 쓰는 어노테이션 정리:

* **@Getter** / **@Setter** : 각 필드의 getter/setter 자동 생성
* **@ToString** : 클래스의 `toString()` 자동 생성
* **@EqualsAndHashCode** : `equals()` / `hashCode()` 자동 생성
* **@NoArgsConstructor** : 기본 생성자 생성
* **@AllArgsConstructor** : 모든 필드를 받는 생성자 생성
* **@RequiredArgsConstructor** : `final` 또는 `@NonNull` 필드만 받는 생성자 생성
* **@Data** : `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@ToString`, `@EqualsAndHashCode` 한 번에 적용
* **@Builder** : 빌더 패턴 클래스 자동 생성
* **@Value** : `@Getter`, `@AllArgsConstructor`, `@ToString`, `@EqualsAndHashCode` + 필드 `private final` 불변 객체

→ `@Data`는 편리하지만 무분별하게 쓰면 예기치 못한 `equals/hashCode`나 성능 문제 생길 수 있으므로, 필요한 어노테이션만 개별적으로 조합하는 게 실무에서 더 안전하다.
