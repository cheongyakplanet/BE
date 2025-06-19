# Feature Documentation

## 🎯 Product Overview

CheonYakPlanet (청약플래닛) is a comprehensive Korean real estate subscription platform that helps users navigate the complex Korean public housing subscription system (청약).

## 🏠 Core Features

### 1. User Management & Authentication

#### 1.1 User Registration & Login
- **Native Registration**: Email-based account creation with password encryption
- **Kakao OAuth Integration**: Social login with Kakao account
- **JWT Authentication**: Stateless authentication with access/refresh token pairs
- **Password Recovery**: Email-based password reset with verification codes

**Technical Details:**
- Access tokens: 60 minutes lifespan
- Refresh tokens: 24 hours lifespan
- Database token storage with blacklisting for secure logout
- BCrypt password encryption

#### 1.2 User Profile Management
- **Financial Profile**: Income, assets, debt, property ownership tracking
- **Demographics**: Marriage status, number of children for subscription eligibility
- **Interest Locations**: Up to 5 Korean administrative regions for personalized content
- **Account Status**: Active/Withdrawn status with soft delete support

**Business Rules:**
- Financial data used for subscription eligibility matching
- Interest locations filter subscription opportunities
- Profile completeness affects recommendation accuracy

### 2. Korean Housing Subscription System (청약)

#### 2.1 Subscription Discovery
- **Government Data Integration**: Real-time sync with LH Corporation API
- **Comprehensive Coverage**: All Korean public housing announcements
- **Geographic Organization**: Organized by 시/도, 시/군/구, 읍/면/동 hierarchy
- **Timeline Tracking**: Subscription periods, winner announcements, contract dates

**Data Sources:**
- LH Corporation (한국토지주택공사) API
- Ministry of Land (국토교통부) API
- Administrative district codes (시군구 코드)

#### 2.2 Subscription Eligibility Matching
- **Ranking System**: 1순위/2순위 priority calculation
- **Area Eligibility**: 해당지역/기타지역 qualification
- **Special Supply Categories**: 
  - 다자녀 가구 (Multi-child families)
  - 신혼부부 (Newlyweds)
  - 생애최초 (First-time homebuyers)
  - 노부모 부양 (Elderly parent support)
  - 청년 (Youth)
  - 신생아 (Newborn families)

**Matching Algorithm:**
- User financial profile vs. subscription requirements
- Geographic preference matching
- Special supply category qualification
- Priority ranking calculation

#### 2.3 Subscription Details
- **Price Information**: Supply prices, payment schedules, deposit requirements
- **Supply Targets**: Unit types, floor plans, household counts
- **Timeline Information**: All important dates in subscription process
- **Developer Information**: Construction companies, contact details

### 3. Location Intelligence

#### 3.1 Infrastructure Analysis
- **Educational Facilities**: Schools (elementary, middle, high school) within 1km
- **Transportation**: Subway stations, bus stops, accessibility analysis
- **Public Facilities**: Government offices, hospitals, parks, cultural centers
- **Distance Calculations**: Precise Haversine formula for geographic distance

**Data Sources:**
- Korean school information database
- Public transportation network data
- Government facility location data
- Kakao Maps API for geocoding

#### 3.2 Geographic Services
- **Address Parsing**: Korean administrative hierarchy parsing
- **Coordinate Mapping**: Lat/Long conversion for all locations
- **Proximity Search**: 1km radius searches with bounding box optimization
- **Regional Analytics**: Popular locations and infrastructure density

### 4. Financial Product Integration

#### 4.1 House Loan Products
- **Bank Integration**: Major Korean bank loan products
- **Rate Comparison**: Min/max/average lending rates
- **Eligibility Matching**: User income vs. loan requirements
- **Product Categories**: Mortgage loans vs. rental house loans

**Data Sources:**
- Financial Supervisory Service (FSS) API
- Major Korean banks (국민은행, 신한은행, etc.)
- Real-time interest rate updates

#### 4.2 Financial Analysis
- **Affordability Calculator**: Income-based loan capacity
- **Rate Trends**: Historical interest rate analysis
- **Recommendation Engine**: Best loan products for user profile

### 5. Market Intelligence & News

#### 5.1 Automated News Aggregation
- **Multi-source Crawling**: Naver News API with 20+ real estate keywords
- **Content Filtering**: Advanced anti-spam and quality filtering
- **Trusted Sources**: 30+ verified news domains
- **Daily Summaries**: Automated markdown-formatted daily reports

**Keywords Monitored:**
- 부동산, 청약, 분양, LH, 아파트
- 주택정책, 임대차, 재건축, 재개발
- 국토교통부, 가계대출, 주담대

#### 5.2 Content Categorization
- **정책 뉴스**: Government policy and regulation updates
- **청약/분양**: Subscription and pre-sale announcements
- **시장 동향**: Market trends and price analysis
- **자동 게시**: Daily community posts with categorized content

#### 5.3 Quality Assurance
- **Multi-stage Filtering**: 7-step content quality pipeline
- **Ad Detection**: Strong/weak advertising keyword filtering
- **Relevance Scoring**: Real estate relevance vs. advertising score
- **Deduplication**: Title-based duplicate prevention

### 6. Community Platform

#### 6.1 Discussion System
- **Hierarchical Structure**: Posts → Comments → Replies (3-level)
- **Categories**: Review, Subscription Info, Free Talk, Q&A
- **Reaction System**: Like/Dislike with duplicate prevention
- **View Tracking**: Post popularity and engagement metrics

#### 6.2 Content Management
- **Rich Text Support**: Markdown-style content formatting
- **Moderation**: Content flagging and blind post system
- **Search**: Full-text search across posts and comments
- **Pagination**: Performance-optimized list views

#### 6.3 User Engagement
- **Gamification**: Like counts, view counts, popular posts
- **User Profiles**: Username-based identity system
- **Activity Tracking**: User contribution history

### 7. AI-Powered Chat Assistant

#### 7.1 Real-time Consultation
- **Gemini 2.0 Integration**: Google's latest conversational AI
- **WebSocket Connection**: Real-time bidirectional communication
- **Usage Limits**: 15 messages per user per day
- **Session Management**: Conversation context and memory

#### 7.2 Domain Expertise
- **Korean Real Estate Knowledge**: Trained on subscription system rules
- **Personalized Advice**: Based on user financial profile and preferences
- **Multi-language**: Korean and English support
- **Context Awareness**: Integration with user subscription interests

#### 7.3 Technical Implementation
- **JWT Authentication**: Secure WebSocket handshake
- **Memory Management**: Efficient conversation history handling
- **Rate Limiting**: Anti-abuse mechanisms
- **Fallback Handling**: Graceful degradation for API failures

### 8. Data Processing & Automation

#### 8.1 Scheduled Data Updates
- **Daily Updates**: Government subscription data (1:00 AM KST)
- **Coordinate Sync**: Address geocoding updates (1:15 AM KST)
- **News Processing**: Daily aggregation and summarization (12:30 AM KST)
- **Monthly Updates**: Real estate transaction data (last day, 2:00 AM KST)

#### 8.2 External API Integration
- **Government APIs**: Resilient integration with retry mechanisms
- **Rate Limiting**: Respectful API usage with configurable delays
- **Incremental Processing**: Duplicate prevention and delta updates
- **Error Handling**: Comprehensive logging and fallback mechanisms

#### 8.3 Performance Optimization
- **Async Processing**: Parallel data processing with CompletableFuture
- **Batch Operations**: Bulk database operations for efficiency
- **Caching**: Strategic caching for frequently accessed data
- **Database Optimization**: Proper indexing and query optimization

## 🎮 User Journey

### 1. New User Onboarding
1. **Registration**: Email or Kakao social login
2. **Profile Setup**: Financial information and demographics
3. **Interest Selection**: Choose up to 5 regions of interest
4. **First Recommendations**: Personalized subscription suggestions

### 2. Daily Usage Pattern
1. **Dashboard View**: Latest subscriptions in interest areas
2. **News Consumption**: Daily real estate news summaries
3. **Community Engagement**: Browse posts, ask questions
4. **AI Consultation**: Get personalized subscription advice

### 3. Subscription Discovery
1. **Browse Listings**: Filter by region, date, price
2. **Detailed Analysis**: View subscription details and requirements
3. **Eligibility Check**: Match personal profile against requirements
4. **Infrastructure Review**: Check nearby schools, stations, facilities
5. **Save Favorites**: Like interesting subscriptions for later

### 4. Financial Planning
1. **Loan Exploration**: Browse available loan products
2. **Rate Comparison**: Compare interest rates across banks
3. **Affordability Analysis**: Calculate loan capacity based on income
4. **Application Guidance**: Get directed to bank websites

## 🔒 Security Features

### Authentication Security
- **JWT with Database Storage**: Enhanced security with token blacklisting
- **Password Encryption**: BCrypt with configurable complexity
- **Session Management**: Secure logout with token invalidation
- **Role-based Access**: USER/ADMIN roles with method-level security

### Data Protection
- **Soft Delete Pattern**: Data preservation with audit trails
- **Financial Data Encryption**: Secure handling of sensitive information
- **API Security**: Rate limiting and authentication for all endpoints
- **CORS Configuration**: Flexible cross-origin resource sharing

### WebSocket Security
- **JWT Handshake**: Secure real-time connection establishment
- **Usage Limits**: Anti-abuse mechanisms for chat features
- **Connection Management**: Automatic cleanup and session management

## 📱 API Design

### RESTful Architecture
- **Resource-based URLs**: Clear, intuitive endpoint structure
- **HTTP Method Usage**: Proper GET, POST, PUT, DELETE operations
- **Consistent Responses**: Standardized ApiResponse wrapper
- **Error Handling**: Comprehensive error codes and messages

### Documentation
- **OpenAPI 3.0**: Complete API specification with examples
- **Swagger UI**: Interactive API documentation
- **Postman Collections**: Ready-to-use API testing collections

## 🚀 Performance Features

### Scalability
- **Async Processing**: Non-blocking I/O for external API calls
- **Thread Pool Management**: Optimized concurrency for data processing
- **Database Optimization**: Strategic indexing and query optimization
- **Caching Strategy**: Multi-level caching for performance

### Monitoring
- **Health Checks**: Comprehensive application health monitoring
- **Metrics Collection**: Performance metrics with Prometheus integration
- **Error Tracking**: Detailed logging and error reporting
- **Audit Trails**: Complete activity tracking for debugging

---

**Last Updated**: 2024-06-19  
**Feature Version**: 1.0  
**Platform**: Web API + WebSocket