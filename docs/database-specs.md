# Database Specifications

## 📊 Database Overview

CheonYakPlanet uses MySQL database with catalog separation (`catalog = "planet"`) for organized data management. All entities implement comprehensive audit trails and soft delete patterns.

## 🏗️ Database Architecture

### Base Patterns
- **Audit Trail**: All tables inherit `created_at`, `created_by`, `updated_at`, `updated_by`
- **Soft Delete**: `deleted_at`, `deleted_by` fields for data preservation
- **Catalog Separation**: All tables use `catalog = "planet"`
- **Primary Keys**: Auto-incrementing `BIGINT` for most entities, natural keys for reference data

## 📋 Table Specifications

### 👤 User Management

#### `user_info`
```sql
CREATE TABLE planet.user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    status ENUM('ACTIVE', 'WITHDRAWN') DEFAULT 'ACTIVE',
    
    -- Financial Profile for Subscription Eligibility
    property DOUBLE COMMENT '부동산 자산 (만원)',
    income INTEGER COMMENT '월소득 (만원)',
    is_married BOOLEAN COMMENT '결혼 여부',
    num_child INTEGER COMMENT '자녀 수',
    num_house INTEGER COMMENT '주택 보유 수',
    
    -- Interest Locations (up to 5)
    interest_local_1 VARCHAR(100) COMMENT '관심지역 1',
    interest_local_2 VARCHAR(100) COMMENT '관심지역 2',
    interest_local_3 VARCHAR(100) COMMENT '관심지역 3',
    interest_local_4 VARCHAR(100) COMMENT '관심지역 4',
    interest_local_5 VARCHAR(100) COMMENT '관심지역 5',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255)
);
```

**Indexes:**
- `idx_email` (email)
- `idx_status_deleted` (status, deleted_at)
- `idx_interest_locations` (interest_local_1, interest_local_2, interest_local_3)

#### `user_token`
```sql
CREATE TABLE planet.user_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    access_token TEXT NOT NULL,
    refresh_token TEXT NOT NULL,
    access_token_expiry DATETIME NOT NULL,
    refresh_token_expiry DATETIME NOT NULL,
    blacklisted BOOLEAN DEFAULT FALSE,
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Indexes:**
- `idx_email_blacklisted` (email, blacklisted)
- `idx_access_token` (access_token(255))
- `idx_token_expiry` (access_token_expiry, refresh_token_expiry)

### 🏠 Subscription Management

#### `subscription_info` (Main subscription data)
```sql
CREATE TABLE planet.subscription_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Basic Information
    house_nm VARCHAR(255) COMMENT '주택명',
    house_manage_no VARCHAR(50) UNIQUE COMMENT '주택관리번호',
    pblanc_no VARCHAR(50) COMMENT '공고번호',
    
    -- Location Information
    hssply_adres TEXT COMMENT '공급위치',
    region VARCHAR(50) COMMENT '시/도',
    city VARCHAR(50) COMMENT '시/군/구',
    district VARCHAR(50) COMMENT '읍/면/동',
    
    -- Reception Periods
    rcept_bgnde DATE COMMENT '청약접수시작일',
    rcept_endde DATE COMMENT '청약접수종료일',
    spsply_rcept_bgnde DATE COMMENT '특별공급접수시작일',
    spsply_rcept_endde DATE COMMENT '특별공급접수종료일',
    
    -- Ranking System Dates
    gnrl_rnk1_crsparea_rcptde DATE COMMENT '1순위해당지역접수일',
    gnrl_rnk1_etc_area_rcptde DATE COMMENT '1순위기타지역접수일',
    gnrl_rnk2_crsparea_rcptde DATE COMMENT '2순위해당지역접수일',
    gnrl_rnk2_etc_area_rcptde DATE COMMENT '2순위기타지역접수일',
    
    -- Winner Announcement and Contract
    przwner_presnatn_de DATE COMMENT '당첨자발표일',
    cntrct_cncls_bgnde DATE COMMENT '계약체결시작일',
    cntrct_cncls_endde DATE COMMENT '계약체결종료일',
    
    -- Business Information
    bsns_mby_nm VARCHAR(255) COMMENT '사업주체명',
    cnstrct_entrps_nm VARCHAR(255) COMMENT '건설업체명',
    mdhs_telno VARCHAR(20) COMMENT '문의처전화번호',
    hmpg_adres TEXT COMMENT '홈페이지주소',
    
    -- Regulatory Information
    parcprc_uls_at CHAR(1) COMMENT '분양가상한제적용여부',
    speclt_rdn_earth_at CHAR(1) COMMENT '투기과열지구여부',
    public_house_earth_at CHAR(1) COMMENT '공공주택지구여부',
    lrscl_bldlnd_at CHAR(1) COMMENT '대규모택지개발지구여부',
    
    -- Coordinates (Added by geocoding)
    latitude DOUBLE COMMENT '위도',
    longitude DOUBLE COMMENT '경도',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255)
);
```

**Indexes:**
- `idx_house_manage_no` (house_manage_no)
- `idx_region_city` (region, city)
- `idx_reception_dates` (rcept_bgnde, rcept_endde)
- `idx_location` (latitude, longitude)
- `idx_deleted_at` (deleted_at)

#### `subscription_price_info`
```sql
CREATE TABLE planet.subscription_price_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_info_id BIGINT NOT NULL,
    
    housing_type VARCHAR(100) COMMENT '주택유형',
    supply_area DECIMAL(10,4) COMMENT '공급면적',
    supply_price BIGINT COMMENT '공급가격',
    supply_count INTEGER COMMENT '공급세대수',
    spsply_count INTEGER COMMENT '특별공급세대수',
    gnrl_spsply_count INTEGER COMMENT '일반공급세대수',
    
    -- Ranking Priority Payments
    gnrl_rnk1_etc_area_apt_cnt INTEGER COMMENT '1순위기타지역세대수',
    gnrl_rnk2_etc_area_apt_cnt INTEGER COMMENT '2순위기타지역세대수',
    
    -- Payment Information
    lfe_frst_lwet_top_amount BIGINT COMMENT '생애최초저소득층상위소득',
    old_parnts_suport_spsply_top_amount BIGINT COMMENT '노부모부양특별공급상위소득',
    nwwds_spsply_top_amount BIGINT COMMENT '신혼부부특별공급상위소득',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255),
    
    FOREIGN KEY (subscription_info_id) REFERENCES subscription_info(id)
);
```

#### `subscription_special_supply_target`
```sql
CREATE TABLE planet.subscription_special_supply_target (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_info_id BIGINT NOT NULL,
    
    -- Special Supply Categories
    supply_count_multichild INTEGER COMMENT '다자녀가구공급세대수',
    supply_count_newlywed INTEGER COMMENT '신혼부부공급세대수',
    supply_count_first INTEGER COMMENT '생애최초공급세대수',
    supply_count_old_parent INTEGER COMMENT '노부모부양공급세대수',
    supply_count_institution_recommend INTEGER COMMENT '기관추천공급세대수',
    supply_count_etc INTEGER COMMENT '기타공급세대수',
    supply_count_disabled INTEGER COMMENT '장애인공급세대수',
    supply_count_youth INTEGER COMMENT '청년공급세대수',
    supply_count_newborn INTEGER COMMENT '신생아공급세대수',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255),
    
    FOREIGN KEY (subscription_info_id) REFERENCES subscription_info(id)
);
```

#### `subscription_like`
```sql
CREATE TABLE planet.subscription_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    
    -- Cached subscription data for quick access
    house_nm VARCHAR(255),
    hssply_adres TEXT,
    region VARCHAR(50),
    rcept_bgnde DATE,
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255),
    
    UNIQUE KEY uk_subscription_user (subscription_id, user_email, deleted_at)
);
```

### 💬 Community Platform

#### `Post`
```sql
CREATE TABLE planet.Post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    views BIGINT DEFAULT 0,
    likes INTEGER DEFAULT 0,
    dislikes INTEGER DEFAULT 0,
    is_blind BOOLEAN DEFAULT FALSE,
    category ENUM('REVIEW', 'SUBSCRIPTION_INFO', 'SUBSCRIPTION_INQUIRY', 'INFO_SHARE', 'FREE_TALK') NOT NULL,
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255)
);
```

**Indexes:**
- `idx_category_created` (category, created_at DESC)
- `idx_username` (username)
- `idx_views_likes` (views DESC, likes DESC)

#### `Comment`
```sql
CREATE TABLE planet.Comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255),
    
    FOREIGN KEY (post_id) REFERENCES Post(id)
);
```

#### `Reply`
```sql
CREATE TABLE planet.Reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255),
    
    FOREIGN KEY (comment_id) REFERENCES Comment(id)
);
```

#### `post_reaction`
```sql
CREATE TABLE planet.post_reaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    reaction_type ENUM('LIKE', 'DISLIKE') NOT NULL,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (post_id) REFERENCES Post(id),
    UNIQUE KEY uk_post_user_reaction (post_id, email)
);
```

### 💰 Financial Products

#### `houseloan_product`
```sql
CREATE TABLE planet.houseloan_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Product Information
    fin_co_nm VARCHAR(255) COMMENT '금융회사명',
    fin_prdt_nm VARCHAR(255) COMMENT '금융상품명',
    join_way TEXT COMMENT '가입방법',
    loan_inci_expn TEXT COMMENT '대출부대비용',
    erly_rpay_fee TEXT COMMENT '중도상환수수료',
    dly_rate TEXT COMMENT '연체이자율',
    loan_lmt TEXT COMMENT '대출한도',
    
    -- Interest Rates
    lend_rate_type_nm VARCHAR(100) COMMENT '대출금리유형명',
    lend_rate_min DOUBLE COMMENT '대출최저금리',
    lend_rate_max DOUBLE COMMENT '대출최고금리',
    lend_rate_avg DOUBLE COMMENT '대출평균금리',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255)
);
```

#### `mortgage_product`
```sql
CREATE TABLE planet.mortgage_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Product Information
    fin_co_nm VARCHAR(255) COMMENT '금융회사명',
    fin_prdt_nm VARCHAR(255) COMMENT '금융상품명',
    mrtg_type VARCHAR(50) COMMENT '담보유형코드',
    mrtg_type_nm VARCHAR(100) COMMENT '담보유형명',
    
    -- Same structure as houseloan_product for rates and terms
    join_way TEXT COMMENT '가입방법',
    loan_inci_expn TEXT COMMENT '대출부대비용',
    erly_rpay_fee TEXT COMMENT '중도상환수수료',
    dly_rate TEXT COMMENT '연체이자율',
    loan_lmt TEXT COMMENT '대출한도',
    
    lend_rate_type_nm VARCHAR(100) COMMENT '대출금리유형명',
    lend_rate_min DOUBLE COMMENT '대출최저금리',
    lend_rate_max DOUBLE COMMENT '대출최고금리',
    lend_rate_avg DOUBLE COMMENT '대출평균금리',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted_at DATETIME,
    deleted_by VARCHAR(255)
);
```

### 🏫 Infrastructure Data

#### `school`
```sql
CREATE TABLE planet.school (
    school_id VARCHAR(50) PRIMARY KEY COMMENT '학교ID',
    school_name VARCHAR(255) NOT NULL COMMENT '학교명',
    category VARCHAR(50) COMMENT '학교급 (초등학교, 중학교, 고등학교)',
    type1 VARCHAR(50) COMMENT '설립구분 (국립, 공립, 사립)',
    type2 VARCHAR(50) COMMENT '운영구분',
    status VARCHAR(50) COMMENT '운영상태',
    address TEXT COMMENT '주소',
    latitude DOUBLE COMMENT '위도',
    longitude DOUBLE COMMENT '경도',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Indexes:**
- `idx_location` (latitude, longitude)
- `idx_category_type` (category, type1)
- `idx_address` (address(255))

#### `station`
```sql
CREATE TABLE planet.station (
    number VARCHAR(50) PRIMARY KEY COMMENT '역번호',
    operator VARCHAR(100) COMMENT '운영회사',
    line VARCHAR(100) COMMENT '노선명',
    type VARCHAR(50) COMMENT '교통수단 (지하철, 기차, 버스)',
    nm_kor VARCHAR(255) NOT NULL COMMENT '한국어역명',
    nm_eng VARCHAR(255) COMMENT '영어역명',
    is_transfer CHAR(1) COMMENT '환승역여부',
    platform TEXT COMMENT '플랫폼정보',
    longitude DOUBLE COMMENT '경도',
    latitude DOUBLE COMMENT '위도',
    addr_nm TEXT COMMENT '주소',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### `public_facility`
```sql
CREATE TABLE planet.public_facility (
    present_sn VARCHAR(50) PRIMARY KEY COMMENT '시설고유번호',
    lclas_cl VARCHAR(100) COMMENT '대분류',
    mlsfc_cl VARCHAR(100) COMMENT '중분류',
    sclas_cl VARCHAR(100) COMMENT '소분류',
    dgm_nm VARCHAR(255) COMMENT '시설명',
    dgm_ar DOUBLE COMMENT '시설면적',
    signgu_se VARCHAR(100) COMMENT '시군구구분',
    geometry TEXT COMMENT 'GIS좌표정보',
    latlon_geometry TEXT COMMENT '위경도좌표',
    longitude DOUBLE COMMENT '경도',
    latitude DOUBLE COMMENT '위도'
);
```

### 🏘️ Administrative Data

#### `sggcode`
```sql
CREATE TABLE planet.sggcode (
    sgg_cd5 INTEGER PRIMARY KEY COMMENT '5자리시군구코드',
    sgg_cd_nm TEXT COMMENT '시군구명',
    sgg_cd_nm_region VARCHAR(100) COMMENT '시도명',
    sgg_cd_nm_city VARCHAR(100) COMMENT '시군구명'
);
```

### 📊 Real Estate Price Data

#### `real_estate_price`
```sql
CREATE TABLE planet.real_estate_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region VARCHAR(50) COMMENT '시도',
    sgg_cd VARCHAR(10) COMMENT '시군구코드',
    umd_nm VARCHAR(100) COMMENT '읍면동명',
    apt_nm VARCHAR(255) COMMENT '아파트명',
    deal_amount BIGINT COMMENT '거래금액',
    exclu_use_ar DECIMAL(10,4) COMMENT '전용면적',
    deal_date DATE COMMENT '거래일',
    floor_info VARCHAR(10) COMMENT '층정보',
    build_year INTEGER COMMENT '건축년도',
    
    -- Audit Fields
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### `real_estate_price_summary`
```sql
CREATE TABLE planet.real_estate_price_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region VARCHAR(50) COMMENT '시도',
    sgg_cd_nm VARCHAR(100) COMMENT '시군구명',
    deal_year INTEGER COMMENT '거래년도',
    deal_month INTEGER COMMENT '거래월',
    deal_count INTEGER COMMENT '거래건수',
    price_per_ar BIGINT COMMENT '면적당평균가격',
    
    UNIQUE KEY uk_region_year_month (region, sgg_cd_nm, deal_year, deal_month)
);
```

## 🔗 Entity Relationships

### Primary Relationships
- `User` ← 1:N → `Post` (via username)
- `Post` ← 1:N → `Comment` ← 1:N → `Reply`
- `User` ← 1:N → `SubscriptionLike` ← N:1 → `SubscriptionInfo`
- `User` ← 1:N → `PostReaction` ← N:1 → `Post`
- `SubscriptionInfo` ← 1:N → `SubscriptionPriceInfo`
- `SubscriptionInfo` ← 1:N → `SubscriptionSpecialSupplyTarget`

### Referential Integrity
- **Soft Delete**: Related entities maintain references even after soft deletion
- **Cascade Rules**: Comments and Replies cascade delete with parent Post
- **Foreign Key Constraints**: Enforced at database level where appropriate

## 🚀 Performance Considerations

### Indexing Strategy
- **Composite Indexes**: For frequently queried combinations (region + city, date ranges)
- **Partial Indexes**: On deleted_at IS NULL for active records
- **Geographic Indexes**: Spatial indexing for location-based queries

### Query Optimization
- **Soft Delete Queries**: Always include `deleted_at IS NULL` in WHERE clauses
- **Pagination**: Use cursor-based pagination for large result sets
- **Batch Operations**: Bulk inserts for government API data updates

## 🔧 Maintenance Procedures

### Data Cleanup
- **Token Cleanup**: Expired tokens cleaned up weekly
- **Soft Delete Cleanup**: Archive old soft-deleted records quarterly
- **Log Retention**: Application logs retained for 90 days

### Backup Strategy
- **Daily Backups**: Full database backup daily at 3 AM KST
- **Transaction Log Backups**: Every 15 minutes during business hours
- **Testing**: Monthly backup restoration tests

---

**Last Updated**: 2024-06-19  
**Schema Version**: 1.0  
**Database Engine**: MySQL 8.0+