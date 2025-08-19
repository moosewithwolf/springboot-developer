분류 기준을 잡는 방법

Spring에서 클래스가 보통 쓰이는 역할은 크게 나뉜다:

Entity: DB 테이블과 매핑되는 클래스 (Article).

Repository: DB 접근 (CRUD 수행).

Service: 비즈니스 로직 처리.

Controller: API 요청/응답 담당.

DTO: 데이터 이동용 객체 (요청/응답을 깔끔히 분리하기 위함).

즉, AddArticleRequest는 DTO 계열이고, 주 목적은 요청 데이터 수집 → Entity 변환이다.

👉 팁: 코드를 볼 때 패키지명(dto, domain, repository, service, controller 등)을 먼저 확인하면 그 클래스의 성격이 보통 드러난다.
