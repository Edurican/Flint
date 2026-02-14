# Flint (플린트)

**Flint**는 사용자 간의 소통과 정보 공유를 위한 소셜 미디어 플랫폼 백엔드 프로젝트입니다.
Spring Boot 기반으로 구축되었으며, 안정적인 REST API 제공과 대용량 데이터 처리를 고려한 아키텍처를 지향합니다.

## 🛠 기술 스택 (Tech Stack)

### Core
- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security**: 인증 및 인가 처리
- **Spring Batch**: 대용량 데이터 배치 처리

### Data & Storage
- **Spring Data JPA**: ORM 기술 표준
- **QueryDSL 5.0**: 타입 세이프한 동적 쿼리 작성
- **MariaDB**: 관계형 데이터베이스 (RDBMS)
- **MinIO**: 이미지 및 파일 객체 스토리지

### Auth
- **JWT (JSON Web Token)**: 무상태(Stateless) 인증 방식

### Testing & Tools
- **Junit 5**: 단위 테스트
- **Swagger (SpringDoc)**: API 문서화
- **Gradle**: 빌드 도구
- **Lombok**: 보일러플레이트 코드 제거

---

## 📂 프로젝트 구조 (Project Structure)

프로젝트는 크게 비즈니스 로직을 담당하는 `core` 모듈과 데이터 접근을 담당하는 `storage` 모듈로 구성되어 있습니다.

```
src/main/java/com/edurican/flint
├── core            # 핵심 비즈니스 로직
│   ├── api         # Controller, DTO 등 API 계층
│   ├── domain      # 도메인 서비스 로직
│   ├── enums       # 공통 Enum 클래스
│   ├── job         # Spring Batch Job 설정
│   └── support     # 유틸리티 및 설정(Config)
└── storage         # 엔티티 및 Repository 계층
    ├── db          # JPA Entity 및 Repository
    └── ...
```

---

## ✨ 주요 기능 (Key Features)

- **사용자 (User)**
  - **인증/인가**: JWT 기반의 회원가입 및 로그인 (Stateless)
  - **프로필**: 
    - 닉네임, 한줄 소개 수정
    - 프로필 이미지 업로드/수정/삭제 (MinIO 연동)
  - **검증**: 이메일 및 유저네임 중복 검사

- **게시글 (Post)**
  - **피드 시스템**: 
    - 커서 기반 페이지네이션 (Cursor-based Pagination) 적용으로 대용량 데이터 조회 성능 최적화
    - 추천 피드 및 팔로잉 피드 지원
  - **기능**: 게시글 작성/수정/삭제, 조회수 집계
  - **토픽(Topic)**: 관심 주제별 게시글 분류 및 탐색
  - **인기글(Hot Post)**: 사용자 반응(좋아요 등)을 기반으로 한 인기 게시글 선정 알고리즘

- **댓글 (Comment)**
  - **대댓글 구조**: 계층형 댓글(Nested Comment) 지원 (Depth 제한 로직 포함)
  - **기능**: 댓글 및 대댓글 작성/수정/삭제, 좋아요

- **소셜 (Interaction)**
  - **팔로우/언팔로우**: 
    - 사용자 간 팔로우 관계 관리
    - **동시성 제어**: Lock을 활용하여 팔로우/언팔로우 시 발생할 수 있는 데이터 정합성 문제 해결 (Deadlock 방지 로직 적용)
  - **좋아요**: 게시글 및 댓글에 대한 좋아요/취소 기능

- **미디어 (Media)**
  - **이미지 처리**: 
    - MinIO 객체 스토리지를 활용한 이미지 파일 저장 및 서빙
    - 프로필 이미지 등 미디어 리소스 관리

---

## 🚀 시작하기 (Getting Started)

### 전제 조건 (Prerequisites)
- Java 17 이상
- MariaDB 설치 및 실행
- MinIO (또는 S3 호환 스토리지) 실행

### 설치 및 실행 (Installation via Gradle)

1. **저장소 클론**
   ```bash
   git clone https://github.com/Start-Toy-Project/Flint.git
   cd Flint
   ```

2. **환경 변수 설정**
   `application.yml` 또는 환경 변수를 통해 DB 및 MinIO 접속 정보를 설정해야 합니다.

3. **빌드 및 실행**
   ```bash
   ./gradlew bootRun
   ```

### API 문서 확인
서버 실행 후 다음 주소에서 API 문서를 확인할 수 있습니다.
- Swagger UI: `http://localhost:8080/swagger-ui/index.html` (기본 설정 시)

---

## 🤝 기여 (Contributing)
이 프로젝트는 학습 및 사이드 프로젝트 목적으로 진행되고 있습니다. 이슈 등록 및 PR은 언제나 환영합니다.
