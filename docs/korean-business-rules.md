# Korean Real Estate Business Rules

## 🏠 Overview

This document explains the Korean real estate subscription system (청약) business rules and terminology to help developers understand the domain context of CheonYakPlanet.

## 📚 Korean Housing Subscription System (청약 시스템)

### Core Concepts

#### 청약 (Subscription)
Korean public housing subscription system where citizens compete for newly constructed apartments at government-regulated prices. This is fundamentally different from private real estate markets.

#### 분양 (Bunyang) 
Developer-led apartment sales process. Can be public (regulated) or private (market rate).

#### 특별공급 (Special Supply)
Reserved units for specific demographic groups with priority access before general public.

#### 일반공급 (General Supply)
Regular subscription process open to general public after special supply allocation.

### Administrative Hierarchy

#### 시/도 (Si/Do) - Province Level
- **서울특별시** (Seoul Special City)
- **부산광역시** (Busan Metropolitan City)  
- **경기도** (Gyeonggi Province)
- **강원도** (Gangwon Province)
- etc.

#### 시/군/구 (Si/Gun/Gu) - City/County/District Level
- **구** (Gu): District within metropolitan cities
- **시** (Si): City
- **군** (Gun): County (rural areas)

#### 읍/면/동 (Eup/Myeon/Dong) - Neighborhood Level
- **동** (Dong): Urban neighborhood
- **읍** (Eup): Town
- **면** (Myeon): Township (rural)

### Ranking System (순위제)

#### 1순위 (First Priority)
- **청약통장 가입기간**: Subscription savings account duration
- **해당지역**: Local area residents (typically 2+ years)
- **기타지역**: Other area residents
- **특별공급 우선권**: Special supply priority

#### 2순위 (Second Priority)
- Lower priority applicants
- Usually those with shorter savings account history
- Secondary consideration after 1순위 filled

### Special Supply Categories (특별공급 유형)

#### 다자녀 가구 (Multi-child Families)
- **자녀 수**: Number of children (3+ typically)
- **소득 기준**: Income requirements
- **거주 조건**: Residency requirements

#### 신혼부부 (Newlyweds)
- **결혼 기간**: Marriage duration (typically ≤7 years)
- **소득 기준**: Combined household income limits
- **자녀 우대**: Additional points for children

#### 생애최초 (First-time Homebuyers)
- **무주택 기간**: Period without house ownership
- **소득 조건**: Income requirements
- **연령 제한**: Age restrictions

#### 노부모 부양 (Elderly Parent Support)
- **부모 연령**: Parent age requirements (65+)
- **동거 조건**: Cohabitation requirements
- **소득 기준**: Income limits

#### 청년 (Youth)
- **연령 제한**: Age requirements (typically 19-39)
- **소득 기준**: Income limits for young adults
- **지역 조건**: Regional requirements

### Regional Eligibility (지역 자격)

#### 해당지역 (Local Area)
- **거주 기간**: Minimum residency period (typically 2+ years)
- **주민등록**: Resident registration requirements
- **우선 순위**: Higher priority in ranking

#### 기타지역 (Other Areas)
- **경기지역**: Gyeonggi Province (special category for Seoul subscriptions)
- **전국**: Nationwide (lowest priority)

### Financial Requirements

#### Income Limits (소득 기준)
- **도시근로자 월평균소득**: Urban worker average monthly income
- **100% 이하**: ≤100% of median income
- **130% 이하**: ≤130% of median income (with children)

#### Asset Limits (자산 기준)
- **부동산 자산**: Real estate assets
- **일반자산**: General assets (savings, stocks, etc.)
- **자동차 가액**: Vehicle value

#### House Ownership (주택 보유 현황)
- **무주택자**: Non-homeowner
- **1주택자**: Single homeowner
- **주택 처분 조건**: House disposal requirements

### Timeline Process (청약 일정)

#### Key Dates
1. **모집공고일**: Recruitment announcement date
2. **특별공급 접수**: Special supply application period
3. **일반공급 1순위**: First priority general supply
4. **일반공급 2순위**: Second priority general supply
5. **당첨자 발표**: Winner announcement
6. **계약 체결**: Contract signing period

#### Typical Timeline
- **공고 → 특별공급**: ~1 week
- **특별공급 → 1순위**: ~1 week  
- **1순위 → 2순위**: ~1 week
- **접수 → 발표**: ~1 week
- **발표 → 계약**: ~1-2 weeks

### Payment Structure (분양가 구조)

#### Supply Price Components
- **분양가**: Base supply price
- **발코니 확장비**: Balcony extension fee
- **옵션 선택품목**: Optional items
- **중도금**: Interim payments
- **잔금**: Final payment

#### Government Regulations
- **분양가상한제**: Supply price ceiling system
- **투기과열지구**: Speculation overheating areas
- **조정대상지역**: Adjustment target areas

### Subscription Savings (청약통장)

#### Types of Accounts
- **주택청약종합저축**: Comprehensive housing subscription savings
- **청약예금**: Subscription deposits
- **청약부금**: Subscription installments

#### Requirements
- **가입 기간**: Subscription period
- **납입 횟수**: Number of payments
- **납입 금액**: Payment amounts
- **예치 금액**: Deposit requirements

### Regulatory Framework

#### Government Agencies
- **국토교통부**: Ministry of Land, Infrastructure and Transport
- **한국토지주택공사 (LH)**: Korea Land & Housing Corporation
- **주택도시보증공사 (HUG)**: Housing & Urban Guarantee Corporation

#### Legal Framework
- **주택법**: Housing Act
- **주택공급에 관한 규칙**: Housing Supply Rules
- **공동주택관리법**: Apartment Management Act

### Geographic Considerations

#### Seoul Metropolitan Area
- **수도권**: Seoul Capital Area (Seoul + Gyeonggi + Incheon)
- **강남 3구**: Gangnam 3 districts (high-demand areas)
- **서북권/동북권/서남권/동남권**: Seoul region divisions

#### Regional Characteristics
- **신도시**: New towns (planned cities)
- **재건축/재개발**: Reconstruction/redevelopment areas
- **도심권**: Urban center areas
- **강남권**: Gangnam area (premium locations)

### Market Dynamics

#### Price Factors
- **입지**: Location desirability
- **교통**: Transportation accessibility
- **교육환경**: Educational facilities
- **개발호재**: Development prospects

#### Competition Levels
- **경쟁률**: Competition ratio (applicants per unit)
- **인기 단지**: Popular complexes
- **미분양**: Unsold units
- **청약 열기**: Subscription enthusiasm

### User Financial Profiling

#### Income Categories
- **월소득**: Monthly income
- **연소득**: Annual income
- **가구소득**: Household income
- **소득 증빙**: Income verification

#### Asset Assessment
- **금융자산**: Financial assets
- **부동산 자산**: Real estate assets
- **부채**: Liabilities
- **순자산**: Net worth

#### Family Status Impact
- **혼인 여부**: Marriage status
- **자녀 수**: Number of children
- **자녀 연령**: Children's ages
- **부양가족**: Dependents

### Technology Integration

#### Government APIs
- **청약Home**: Official subscription portal
- **LH 청약센터**: LH subscription center
- **SH 청약포털**: Seoul Housing (SH) portal

#### Data Sources
- **실거래가**: Actual transaction prices
- **시세 정보**: Market price information
- **분양 정보**: Pre-sale information

### Business Rules in System

#### Eligibility Calculation
```pseudocode
function calculateEligibility(user, subscription) {
    // Check regional requirements
    if (user.residency.matches(subscription.region)) {
        priority += LOCAL_AREA_BONUS
    }
    
    // Check special supply qualification
    if (user.hasChildren >= 3) {
        specialSupply.add(MULTI_CHILD)
    }
    
    if (user.marriageDuration <= 7 && user.age <= 39) {
        specialSupply.add(NEWLYWED)
    }
    
    // Check financial requirements
    if (user.monthlyIncome <= subscription.incomeLimit) {
        eligible = true
    }
    
    return eligibility_result
}
```

#### Ranking Calculation
```pseudocode
function calculateRanking(user, subscription) {
    ranking = SECOND_PRIORITY  // default
    
    if (user.subscriptionAccount.duration >= MINIMUM_DURATION) {
        ranking = FIRST_PRIORITY
        
        if (user.residency.isLocalArea(subscription.region)) {
            ranking = FIRST_PRIORITY_LOCAL
        }
    }
    
    return ranking
}
```

### Common User Scenarios

#### Scenario 1: 신혼부부 (Newlyweds)
- **나이**: 32세, 30세 부부
- **결혼**: 2년차
- **자녀**: 1명
- **소득**: 월 600만원
- **거주지**: 경기도 (서울 청약 희망)
- **청약통장**: 5년 가입

**Eligibility**: 신혼부부 특별공급 + 일반공급 1순위 기타지역

#### Scenario 2: 생애최초 (First-time Buyer)
- **나이**: 35세
- **결혼**: 미혼
- **무주택**: 10년
- **소득**: 월 400만원
- **거주지**: 서울 강남구
- **청약통장**: 8년 가입

**Eligibility**: 생애최초 특별공급 + 일반공급 1순위 해당지역

#### Scenario 3: 다자녀가구 (Multi-child Family)
- **나이**: 38세, 36세 부부
- **자녀**: 3명
- **소득**: 월 800만원
- **거주지**: 서울 송파구
- **청약통장**: 10년 가입

**Eligibility**: 다자녀 특별공급 + 일반공급 1순위 해당지역

### System Implementation Considerations

#### Data Validation
- Verify Korean address format
- Validate administrative district codes
- Check date ranges for subscription periods
- Confirm financial data formats

#### User Experience
- Provide Korean terminology with explanations
- Guide users through complex eligibility rules
- Show timeline visualization for subscription process
- Offer personalized recommendations based on profile

#### Integration Points
- Government API data synchronization
- Real-time subscription status updates
- Financial institution loan product matching
- Geographic proximity calculations

---

**Business Rules Version**: 1.0  
**Last Updated**: 2024-06-19  
**Domain Expert Review**: Required for updates