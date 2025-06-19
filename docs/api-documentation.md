# API Documentation

## 🌐 API Overview

CheonYakPlanet provides a comprehensive RESTful API for Korean real estate subscription management, along with WebSocket support for real-time chat functionality.

**Base URL**: `http://localhost:8080/api`  
**Authentication**: JWT Bearer Token  
**Content-Type**: `application/json`  
**API Version**: 1.0

## 🔐 Authentication

### Authentication Flow
1. **Register**: `POST /api/member/signup`
2. **Login**: `POST /api/member/login`
3. **Use Token**: Include `Authorization: Bearer {access_token}` in headers
4. **Refresh**: `POST /api/member/refresh` when access token expires
5. **Logout**: `POST /api/member/logout`

### Token Structure
- **Access Token**: 60 minutes lifespan
- **Refresh Token**: 24 hours lifespan
- **Storage**: Database with blacklisting support

## 📚 API Endpoints

### 👤 User Management (`/api/member`)

#### Register User
```http
POST /api/member/signup
Content-Type: application/json

{
    "email": "user@cheonyakplanet.com",
    "password": "password123!",
    "username": "청약초보",
    "phoneNumber": "010-1234-5678"
}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "email": "user@cheonyakplanet.com",
        "username": "청약초보",
        "role": "USER",
        "status": "ACTIVE"
    }
}
```

#### Login
```http
POST /api/member/login
Content-Type: application/json

{
    "email": "user@cheonyakplanet.com",
    "password": "password123!"
}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 3600
    }
}
```

#### Get User Profile
```http
GET /api/member/mypage
Authorization: Bearer {access_token}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "email": "user@cheonyakplanet.com",
        "username": "청약초보",
        "phoneNumber": "010-1234-5678",
        "marriageStatus": "기혼",
        "numberOfChildren": 2,
        "monthlyIncome": 500,
        "totalAssets": 2000,
        "hasHouse": false,
        "interestLocations": ["서울특별시 강남구", "경기도 성남시"]
    }
}
```

#### Update User Profile
```http
PUT /api/member/update
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "username": "청약전문가",
    "marriageStatus": "기혼",
    "numberOfChildren": 2,
    "monthlyIncome": 600,
    "totalAssets": 2500,
    "hasHouse": false
}
```

#### Add Interest Location
```http
POST /api/member/interest-location
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "locations": ["서울특별시 강남구", "경기도 성남시", "서울특별시 서초구"]
}
```

#### Refresh Token
```http
POST /api/member/refresh
Content-Type: application/json

{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Logout
```http
POST /api/member/logout
Authorization: Bearer {access_token}
```

### 🏠 Subscription Information (`/api/info`)

#### Get Subscription Details
```http
GET /api/info/subscription/{subscriptionId}
Authorization: Bearer {access_token}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "id": 1,
        "houseName": "래미안 강남포레스트",
        "houseManageNo": "2024000001",
        "publicationNumber": "2024강남01",
        "supplyLocation": "서울특별시 강남구 대치동",
        "region": "서울특별시",
        "city": "강남구",
        "district": "대치동",
        "receptionStartDate": "2024-07-01",
        "receptionEndDate": "2024-07-03",
        "specialSupplyStartDate": "2024-06-25",
        "specialSupplyEndDate": "2024-06-27",
        "coordinates": {
            "latitude": 37.5665,
            "longitude": 127.0015
        },
        "priceInfo": [
            {
                "housingType": "84㎡",
                "supplyPrice": 1200000000,
                "supplyCount": 100
            }
        ],
        "specialSupplyTargets": {
            "multichild": 20,
            "newlywed": 30,
            "firstTime": 25
        }
    }
}
```

#### Search Subscriptions
```http
GET /api/info/subscriptions?region=서울특별시&city=강남구&page=0&size=10&sort=receptionStartDate,desc
Authorization: Bearer {access_token}
```

**Query Parameters:**
- `region`: 시/도 (optional)
- `city`: 시/군/구 (optional)
- `year`: 년도 (optional)
- `month`: 월 (optional)
- `page`: 페이지 번호 (default: 0)
- `size`: 페이지 크기 (default: 10)
- `sort`: 정렬 기준 (default: receptionStartDate,desc)

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "content": [
            {
                "id": 1,
                "houseName": "래미안 강남포레스트",
                "supplyLocation": "서울특별시 강남구 대치동",
                "receptionStartDate": "2024-07-01",
                "receptionEndDate": "2024-07-03",
                "isLiked": false
            }
        ],
        "pageable": {
            "pageNumber": 0,
            "pageSize": 10,
            "sort": {
                "sorted": true,
                "orderBy": "receptionStartDate",
                "direction": "DESC"
            }
        },
        "totalElements": 150,
        "totalPages": 15,
        "first": true,
        "last": false
    }
}
```

#### Get User Interest Subscriptions
```http
GET /api/info/interest-subscriptions?page=0&size=10
Authorization: Bearer {access_token}
```

#### Get Infrastructure Information
```http
GET /api/info/infrastructure?latitude=37.5665&longitude=127.0015
Authorization: Bearer {access_token}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "schools": [
            {
                "name": "대치초등학교",
                "category": "초등학교",
                "type": "공립",
                "distance": 0.3,
                "address": "서울특별시 강남구 대치동"
            }
        ],
        "stations": [
            {
                "name": "대치역",
                "line": "3호선",
                "type": "지하철",
                "distance": 0.5
            }
        ],
        "publicFacilities": [
            {
                "name": "강남구청",
                "category": "행정기관",
                "distance": 1.2
            }
        ]
    }
}
```

#### Like/Unlike Subscription
```http
POST /api/info/subscription/{subscriptionId}/like
Authorization: Bearer {access_token}
```

```http
DELETE /api/info/subscription/{subscriptionId}/like
Authorization: Bearer {access_token}
```

### 💬 Community (`/api/community`)

#### Get Posts
```http
GET /api/community/posts?category=SUBSCRIPTION_INQUIRY&page=0&size=10&sort=createdDate,desc
Authorization: Bearer {access_token}
```

**Query Parameters:**
- `category`: POST 카테고리 (optional)
- `search`: 검색어 (optional)
- `page`: 페이지 번호 (default: 0)
- `size`: 페이지 크기 (default: 10)
- `sort`: 정렬 기준 (default: createdDate,desc)

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "content": [
            {
                "id": 1,
                "title": "강남구 청약 질문",
                "content": "강남구 신규 분양 아파트 청약 자격 조건이 궁금합니다.",
                "username": "청약초보",
                "category": "SUBSCRIPTION_INQUIRY",
                "views": 150,
                "likes": 5,
                "commentCount": 3,
                "createdDate": "2024-06-19T10:30:00"
            }
        ],
        "totalElements": 50,
        "totalPages": 5
    }
}
```

#### Create Post
```http
POST /api/community/posts
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "title": "강남구 청약 질문",
    "content": "강남구 신규 분양 아파트 청약 자격 조건이 궁금합니다.",
    "category": "SUBSCRIPTION_INQUIRY"
}
```

#### Get Post Details
```http
GET /api/community/posts/{postId}
Authorization: Bearer {access_token}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "id": 1,
        "title": "강남구 청약 질문",
        "content": "강남구 신규 분양 아파트 청약 자격 조건이 궁금합니다.",
        "username": "청약초보",
        "category": "SUBSCRIPTION_INQUIRY",
        "views": 151,
        "likes": 5,
        "createdDate": "2024-06-19T10:30:00",
        "comments": [
            {
                "id": 1,
                "content": "1순위 자격은 청약통장 가입 기간과 지역에 따라 결정됩니다.",
                "username": "청약전문가",
                "createdDate": "2024-06-19T11:00:00",
                "replies": [
                    {
                        "id": 1,
                        "content": "감사합니다! 추가 질문이 있어요.",
                        "username": "청약초보",
                        "createdDate": "2024-06-19T11:15:00"
                    }
                ]
            }
        ]
    }
}
```

#### Like/Unlike Post
```http
POST /api/community/posts/{postId}/like
Authorization: Bearer {access_token}
```

```http
POST /api/community/posts/{postId}/dislike
Authorization: Bearer {access_token}
```

#### Add Comment
```http
POST /api/community/posts/{postId}/comments
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "content": "1순위 자격은 청약통장 가입 기간과 지역에 따라 결정됩니다."
}
```

#### Add Reply
```http
POST /api/community/comments/{commentId}/replies
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "content": "감사합니다! 추가 질문이 있어요."
}
```

### 🏦 Finance (`/api/finance`)

#### Get House Loans
```http
GET /api/finance/house-loans?page=0&size=10
Authorization: Bearer {access_token}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "content": [
            {
                "id": 1,
                "bankName": "국민은행",
                "productName": "주택담보대출 우대형",
                "joinMethod": "인터넷, 스마트폰, 영업점",
                "loanLimit": "최대 10억원",
                "interestRateMin": 3.5,
                "interestRateMax": 5.2,
                "interestRateAvg": 4.1
            }
        ],
        "totalElements": 25
    }
}
```

#### Get Mortgages
```http
GET /api/finance/mortgages?page=0&size=10
Authorization: Bearer {access_token}
```

### 📰 News (`/api/news`)

#### Manual News Crawl (Admin Only)
```http
POST /api/news/crawl
Authorization: Bearer {admin_access_token}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "message": "뉴스 크롤링이 완료되었습니다.",
        "articlesProcessed": 45,
        "categorizedNews": {
            "policy": 12,
            "subscription": 18,
            "market": 15
        }
    }
}
```

### 🏠 Home Dashboard (`/api/home`)

#### Get Dashboard Data
```http
GET /api/home/dashboard
Authorization: Bearer {access_token}
```

**Response (200 OK):**
```json
{
    "status": "success",
    "data": {
        "popularLocations": [
            {
                "region": "서울특별시",
                "city": "강남구",
                "subscriptionCount": 15,
                "popularity": 85
            }
        ],
        "recentSubscriptions": [
            {
                "id": 1,
                "houseName": "래미안 강남포레스트",
                "supplyLocation": "서울특별시 강남구 대치동",
                "receptionStartDate": "2024-07-01"
            }
        ],
        "trendingPosts": [
            {
                "id": 1,
                "title": "강남구 청약 질문",
                "views": 150,
                "likes": 5
            }
        ]
    }
}
```

#### Get Popular Locations
```http
GET /api/home/popular-locations
```

### 🔧 Admin Data Management (`/api/data`)

#### Refresh Subscription Data (Admin Only)
```http
POST /api/data/refresh-subscriptions
Authorization: Bearer {admin_access_token}
```

#### Update Real Estate Prices (Admin Only)
```http
POST /api/data/update-prices?yyyyMM=202406
Authorization: Bearer {admin_access_token}
```

## 🔌 WebSocket API

### Real-time Chat

#### Connection Endpoint
```
ws://localhost:8080/ws/chat
```

#### Authentication
Include JWT token in handshake:
```javascript
const token = "your_jwt_token";
const socket = new WebSocket(`ws://localhost:8080/ws/chat?token=${token}`);
```

#### Message Format
**Send Message:**
```json
{
    "type": "chat",
    "content": "강남구 청약 자격 조건이 궁금해요"
}
```

**Receive Message:**
```json
{
    "type": "response",
    "content": "강남구 청약을 위해서는 다음 조건들을 확인해보세요:\n1. 청약통장 가입 기간\n2. 지역 거주 요건\n3. 소득 및 자산 조건",
    "timestamp": "2024-06-19T14:30:00Z"
}
```

#### Usage Limits
- **Daily Limit**: 15 messages per user
- **Rate Limiting**: 1 message per 2 seconds
- **Session Timeout**: 30 minutes of inactivity

#### Connection Events
```javascript
socket.onopen = function(event) {
    console.log("Chat connected");
};

socket.onmessage = function(event) {
    const message = JSON.parse(event.data);
    console.log("AI Response:", message.content);
};

socket.onerror = function(error) {
    console.log("WebSocket error:", error);
};

socket.onclose = function(event) {
    console.log("Chat disconnected");
};
```

## 📊 Response Format

### Success Response
```json
{
    "status": "success",
    "data": {
        // Response data
    }
}
```

### Error Response
```json
{
    "status": "fail",
    "data": {
        "code": "AUTH001",
        "message": "인증이 필요한 서비스입니다",
        "details": "JWT token is missing or invalid"
    }
}
```

## 🚫 Error Codes

### Authentication Errors
- **AUTH001**: 인증이 필요한 서비스입니다
- **AUTH002**: 권한이 없습니다
- **AUTH003**: 유효하지 않은 JWT 서명
- **AUTH004**: 만료된 JWT 토큰
- **AUTH005**: JWT 토큰이 블랙리스트에 있습니다

### User Management Errors
- **SIGN001**: 이미 존재하는 회원입니다
- **SIGN002**: 존재하지 않는 회원입니다
- **SIGN003**: 비밀번호가 일치하지 않습니다
- **SIGN004**: 유효하지 않은 이메일 형식입니다

### Community Errors
- **POST001**: 게시글을 찾을 수 없습니다
- **POST002**: 게시글 작성 권한이 없습니다
- **COMMENT001**: 댓글을 찾을 수 없습니다

### Subscription Errors
- **SUB001**: 청약 정보를 찾을 수 없습니다
- **SUB002**: 청약 기간이 아닙니다

## 🔄 Rate Limiting

### API Rate Limits
- **Authentication**: 5 requests per minute per IP
- **General API**: 100 requests per minute per user
- **WebSocket**: 1 message per 2 seconds per user
- **Admin API**: 10 requests per minute per admin

### Headers
Response includes rate limit headers:
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1624123456
```

## 📚 OpenAPI Documentation

### Interactive Documentation
Visit `/swagger-ui` when the application is running for interactive API documentation.

### Swagger Configuration
- **OpenAPI Version**: 3.0
- **Authentication**: Bearer Token support
- **Try It Out**: Interactive API testing
- **Code Examples**: Multiple language examples

---

**API Documentation Version**: 1.0  
**Last Updated**: 2024-06-19  
**Base URL**: `http://localhost:8080/api`