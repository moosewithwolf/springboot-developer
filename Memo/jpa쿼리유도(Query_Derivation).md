## 스프링 데이터 JPA: 메서드 이름으로 쿼리 생성 (Query Derivation)

스프링 데이터 JPA는 정해진 규칙에 따라 Repository 인터페이스에 메서드 이름을 작성하면, 그 이름을 분석하여 자동으로 JPQL(Java Persistence Query Language) 쿼리를 생성하고 실행합니다. 이를 **쿼리 유도**라고 부릅니다.

### 주요 조회 키워드 (find...By...)

| 키워드 (Keyword)           | 메서드 이름 예시 (Example)                                              | 생성되는 JPQL/SQL 조건 (Generated Clause)              |
| :------------------------- | :---------------------------------------------------------------------- | :----------------------------------------------------- |
| **(기본)**           | `findByName(String name)`                                             | `WHERE name = ?1`                                    |
| `And`                    | `findByNameAndAge(String name, int age)`                              | `WHERE name = ?1 AND age = ?2`                       |
| `Or`                     | `findByNameOrAge(String name, int age)`                               | `WHERE name = ?1 OR age = ?2`                        |
| `LessThan`               | `findByAgeLessThan(int age)`                                          | `WHERE age < ?1`                                     |
| `GreaterThan`            | `findByAgeGreaterThan(int age)`                                       | `WHERE age > ?1`                                     |
| `LessThanEqual`          | `findByAgeLessThanEqual(int age)`                                     | `WHERE age <= ?1`                                    |
| `GreaterThanEqual`       | `findByAgeGreaterThanEqual(int age)`                                  | `WHERE age >= ?1`                                    |
| `IsNull` / `IsNotNull` | `findByNameIsNull()` / `findByNameIsNotNull()`                      | `WHERE name IS NULL` / `WHERE name IS NOT NULL`    |
| `Like` / `Containing`  | `findByNameLike(String name)` / `findByNameContaining(String name)` | `WHERE name LIKE '%?1%'`                             |
| `StartingWith`           | `findByNameStartingWith(String prefix)`                               | `WHERE name LIKE '?1%'`                              |
| `EndingWith`             | `findByNameEndingWith(String suffix)`                                 | `WHERE name LIKE '%?1'`                              |
| `IgnoreCase`             | `findByNameIgnoreCase(String name)`                                   | `WHERE UPPER(name) = UPPER(?1)`                      |
| `In`                     | `findByAgeIn(Collection<Integer> ages)`                               | `WHERE age IN (?1)`                                  |
| `True` / `False`       | `findByIsActiveTrue()` / `findByIsActiveFalse()`                    | `WHERE isActive = true` / `WHERE isActive = false` |
| `OrderBy...Asc/Desc`     | `findAllByOrderByAgeDesc()`                                           | `ORDER BY age DESC`                                  |
| `Top<N>` / `First<N>`  | `findTop3ByOrderByScoreDesc()`                                        | `... ORDER BY score DESC` (결과를 3개로 제한)        |

---

**참고**: `?1`, `?2` 등은 메서드의 첫 번째, 두 번째 파라미터를 의미합니다.
