# 🏠 뉴스 크롤링 기능 설정 가이드

## 개요
부동산 및 청약 관련 뉴스를 자동으로 수집하여 커뮤니티에 포스트로 생성하는 기능입니다.

## 환경 변수 설정

다음 환경 변수를 설정해야 합니다:

```bash
# 네이버 뉴스 API
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret
```

## 네이버 API 키 발급

1. [네이버 개발자 센터](https://developers.naver.com/main/) 접속
2. 로그인 후 "Application 등록" 클릭
3. 애플리케이션 정보 입력:
   - 애플리케이션 이름: CheonYakPlanet News
   - 사용 API: 검색 API 선택
   - 서비스 환경: 웹 서비스 URL 등록

## 기능 상세

### 자동 스케줄링
- **실행 시간**: 매일 오전 9시 (KST)
- **크론 표현식**: `0 0 9 * * ?`
- **설정 위치**: `application.properties` > `news.scheduling.cron`

### 크롤링 키워드
다음 키워드로 뉴스를 검색합니다:
- 부동산 정책
- 주택 청약
- 청약 당첨
- 분양
- 아파트 청약
- 주택공급
- LH청약
- SH청약
- 공공분양
- 민간분양

### 포스트 분류
- **청약/분양 관련**: `SUBSCRIPTION_INFO` 카테고리
- **정책/규제 관련**: `INFO_SHARE` 카테고리
- **기타**: `INFO_SHARE` 카테고리 (기본값)

### 중복 방지
- 24시간 이내 동일한 제목의 포스트가 있으면 생성하지 않음
- 제목의 첫 20자를 기준으로 중복 검사

## 수동 실행

관리자는 다음 API를 통해 수동으로 뉴스 크롤링을 실행할 수 있습니다:

```http
POST /api/news/crawl
Authorization: Bearer {access_token}
```

## 시스템 사용자

뉴스 포스트는 `jjsus0307@gmail.com` 계정으로 생성됩니다.
해당 계정이 데이터베이스에 존재해야 합니다.

## 설정 옵션

`application.properties`에서 다음 설정을 변경할 수 있습니다:

```properties
# 뉴스 크롤링 설정
news.api.max-results=10                    # 키워드당 최대 뉴스 수
news.api.delay-between-requests=1000       # API 요청 간 지연시간(ms)
news.scheduling.enabled=true               # 자동 스케줄링 활성화
news.scheduling.cron=0 0 9 * * ?          # 크론 표현식
news.scheduling.max-daily-posts=20        # 일일 최대 포스트 수
```

## 로그 확인

다음 로그를 통해 실행 상황을 확인할 수 있습니다:

```
INFO  - 부동산 뉴스 크롤링 시작
INFO  - 뉴스 포스트 생성: [뉴스 제목]
INFO  - 부동산 뉴스 크롤링 완료
ERROR - 뉴스 크롤링 중 오류 발생 - 키워드: {keyword}
```

## 보안 고려사항

- API 키는 환경 변수로 관리하여 코드에 노출되지 않도록 함
- 시스템 사용자 계정의 보안 유지
- API 요청 제한 및 에러 처리로 안정성 확보