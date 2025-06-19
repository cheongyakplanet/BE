# Error Code Reference

## 📋 Overview

This document provides a comprehensive reference for all error codes used in the CheonYakPlanet application, including their meanings, typical causes, and resolution guidance.

## 🔢 Error Code Format

Error codes follow the pattern: `[CATEGORY][NUMBER]`
- **Category**: 3-4 letter prefix indicating the functional area
- **Number**: 3-digit sequential number (001-999)

## 🔐 Authentication & Authorization (AUTH)

### AUTH001 - 인증이 필요한 서비스입니다
**Description**: Authentication required for this service  
**HTTP Status**: 401 Unauthorized  
**Typical Causes**:
- Missing JWT token in Authorization header
- Request to protected endpoint without authentication
- Malformed Authorization header

**Resolution**:
- Include valid JWT token: `Authorization: Bearer {token}`
- Login first to obtain token
- Check token format

**Example Response**:
```json
{
    "status": "fail",
    "data": {
        "code": "AUTH001",
        "message": "인증이 필요한 서비스입니다",
        "details": "Authorization header is missing"
    }
}
```

### AUTH002 - 권한이 없습니다
**Description**: Insufficient permissions  
**HTTP Status**: 403 Forbidden  
**Typical Causes**:
- User lacks required role (e.g., ADMIN required)
- Attempting to access other user's data
- Role-based access control restriction

**Resolution**:
- Contact administrator for role upgrade
- Ensure request is for own user data
- Check endpoint permissions

### AUTH003 - 유효하지 않은 JWT 서명
**Description**: Invalid JWT signature  
**HTTP Status**: 401 Unauthorized  
**Typical Causes**:
- Token tampered with or corrupted
- Wrong signing key used
- Token from different environment

**Resolution**:
- Re-login to get new token
- Check token integrity
- Verify environment configuration

### AUTH004 - 만료된 JWT 토큰
**Description**: Expired JWT token  
**HTTP Status**: 401 Unauthorized  
**Typical Causes**:
- Access token expired (60 minutes)
- System clock skew
- Token used after expiry

**Resolution**:
- Use refresh token to get new access token
- Re-login if refresh token also expired
- Check system time synchronization

### AUTH005 - JWT 토큰이 블랙리스트에 있습니다
**Description**: JWT token is blacklisted  
**HTTP Status**: 401 Unauthorized  
**Typical Causes**:
- Token invalidated by logout
- Security revocation of token
- Force logout by administrator

**Resolution**:
- Login again to get new token
- Contact support if unexpected
- Clear local token storage

### AUTH006 - 잘못된 토큰 형식
**Description**: Invalid token format  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Malformed JWT token
- Missing Bearer prefix
- Base64 encoding issues

**Resolution**:
- Check Authorization header format
- Ensure "Bearer " prefix included
- Verify token string integrity

## 👤 User Management (SIGN)

### SIGN001 - 이미 존재하는 회원입니다
**Description**: User already exists  
**HTTP Status**: 409 Conflict  
**Typical Causes**:
- Email already registered
- Duplicate registration attempt
- Previous soft-deleted account exists

**Resolution**:
- Use different email address
- Try login with existing credentials
- Contact support for account recovery

### SIGN002 - 존재하지 않는 회원입니다
**Description**: User not found  
**HTTP Status**: 404 Not Found  
**Typical Causes**:
- Email not registered
- Account was deleted
- Typo in email address

**Resolution**:
- Check email spelling
- Register new account
- Contact support if account missing

### SIGN003 - 비밀번호가 일치하지 않습니다
**Description**: Password mismatch  
**HTTP Status**: 401 Unauthorized  
**Typical Causes**:
- Incorrect password entered
- Password was changed
- Caps lock or keyboard layout issues

**Resolution**:
- Try password again carefully
- Use password reset feature
- Check keyboard settings

### SIGN004 - 유효하지 않은 이메일 형식입니다
**Description**: Invalid email format  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Malformed email address
- Missing @ symbol
- Invalid domain format

**Resolution**:
- Use valid email format (user@domain.com)
- Check for typos
- Verify email address

### SIGN005 - 비밀번호가 정책에 맞지 않습니다
**Description**: Password doesn't meet policy requirements  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Password too short
- Missing required character types
- Common password used

**Resolution**:
- Use 8+ characters
- Include uppercase, lowercase, numbers
- Avoid common passwords

### SIGN006 - 계정이 비활성화되었습니다
**Description**: Account is deactivated  
**HTTP Status**: 403 Forbidden  
**Typical Causes**:
- Account suspended by administrator
- User requested account deletion
- Policy violation

**Resolution**:
- Contact support for reactivation
- Create new account if permitted
- Review terms of service

### SIGN007 - 인증 코드가 유효하지 않습니다
**Description**: Invalid verification code  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Expired verification code
- Incorrect code entered
- Code already used

**Resolution**:
- Request new verification code
- Check code carefully
- Verify within time limit

### SIGN008 - 인증 코드가 만료되었습니다
**Description**: Verification code expired  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Code not used within time limit
- System time issues
- Delayed email delivery

**Resolution**:
- Request new verification code
- Use code immediately
- Check email spam folder

## 💬 Community (POST, COMMENT)

### POST001 - 게시글을 찾을 수 없습니다
**Description**: Post not found  
**HTTP Status**: 404 Not Found  
**Typical Causes**:
- Post ID doesn't exist
- Post was deleted
- Access to private post denied

**Resolution**:
- Check post ID
- Verify post still exists
- Contact post author

### POST002 - 게시글 작성 권한이 없습니다
**Description**: No permission to create post  
**HTTP Status**: 403 Forbidden  
**Typical Causes**:
- User not authenticated
- Account restrictions
- Role limitations

**Resolution**:
- Login to account
- Check account status
- Contact administrator

### POST003 - 게시글 수정 권한이 없습니다
**Description**: No permission to edit post  
**HTTP Status**: 403 Forbidden  
**Typical Causes**:
- Not the post author
- Insufficient permissions
- Post locked by moderator

**Resolution**:
- Only author can edit
- Contact moderator if needed
- Create new post instead

### POST004 - 게시글 삭제 권한이 없습니다
**Description**: No permission to delete post  
**HTTP Status**: 403 Forbidden  
**Typical Causes**:
- Not the post author
- Insufficient admin rights
- Post has replies

**Resolution**:
- Only author/admin can delete
- Remove replies first
- Contact moderator

### POST005 - 게시글 내용이 비어있습니다
**Description**: Post content is empty  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Title or content missing
- Only whitespace submitted
- Content below minimum length

**Resolution**:
- Provide meaningful title
- Add substantial content
- Meet minimum length requirements

### COMMENT001 - 댓글을 찾을 수 없습니다
**Description**: Comment not found  
**HTTP Status**: 404 Not Found  
**Typical Causes**:
- Comment ID doesn't exist
- Comment was deleted
- Access denied

**Resolution**:
- Check comment ID
- Verify comment exists
- Refresh page

### COMMENT002 - 댓글 작성 권한이 없습니다
**Description**: No permission to create comment  
**HTTP Status**: 403 Forbidden  
**Typical Causes**:
- Not authenticated
- Post locked for comments
- Account restrictions

**Resolution**:
- Login to account
- Check post status
- Contact moderator

### COMMENT003 - 이미 반응하였습니다
**Description**: Already reacted to this content  
**HTTP Status**: 409 Conflict  
**Typical Causes**:
- User already liked/disliked
- Duplicate reaction attempt
- Race condition in UI

**Resolution**:
- Remove existing reaction first
- Refresh page to see current state
- One reaction per user allowed

## 🏠 Subscription (SUB)

### SUB001 - 청약 정보를 찾을 수 없습니다
**Description**: Subscription information not found  
**HTTP Status**: 404 Not Found  
**Typical Causes**:
- Subscription ID doesn't exist
- Data not yet synchronized
- Subscription expired/closed

**Resolution**:
- Check subscription ID
- Wait for data sync
- Search for active subscriptions

### SUB002 - 청약 기간이 아닙니다
**Description**: Not in subscription period  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Subscription not yet open
- Subscription closed
- Wrong date/time

**Resolution**:
- Check subscription dates
- Wait for opening period
- Verify system time

### SUB003 - 중복된 주택관리번호입니다
**Description**: Duplicate house management number  
**HTTP Status**: 409 Conflict  
**Typical Causes**:
- Data synchronization error
- Duplicate government data
- System integration issue

**Resolution**:
- Skip duplicate entry
- Report to system admin
- Use existing record

### SUB004 - 청약 자격이 없습니다
**Description**: Not eligible for subscription  
**HTTP Status**: 403 Forbidden  
**Typical Causes**:
- Income exceeds limits
- Regional requirements not met
- House ownership restrictions

**Resolution**:
- Review eligibility criteria
- Update financial profile
- Check regional requirements

### SUB005 - 이미 찜한 청약입니다
**Description**: Subscription already favorited  
**HTTP Status**: 409 Conflict  
**Typical Causes**:
- User already liked subscription
- Duplicate like attempt
- UI synchronization issue

**Resolution**:
- Remove existing like first
- Refresh subscription list
- Check favorite status

## 💰 Finance (FIN)

### FIN001 - 금융 상품을 찾을 수 없습니다
**Description**: Financial product not found  
**HTTP Status**: 404 Not Found  
**Typical Causes**:
- Product ID invalid
- Product discontinued
- Data sync delay

**Resolution**:
- Check product ID
- Search current products
- Contact financial institution

### FIN002 - 대출 자격이 없습니다
**Description**: Not eligible for loan  
**HTTP Status**: 403 Forbidden  
**Typical Causes**:
- Income insufficient
- Credit score too low
- Debt-to-income ratio high

**Resolution**:
- Improve financial profile
- Consider different loan products
- Contact financial advisor

### FIN003 - 금리 정보가 만료되었습니다
**Description**: Interest rate information expired  
**HTTP Status**: 410 Gone  
**Typical Causes**:
- Rates updated by bank
- Data refresh needed
- Promotional rate ended

**Resolution**:
- Refresh rate information
- Check with bank directly
- Look for current promotions

## 📰 News (NEWS)

### NEWS001 - 뉴스 크롤링 실패
**Description**: News crawling failed  
**HTTP Status**: 500 Internal Server Error  
**Typical Causes**:
- External API unavailable
- Rate limit exceeded
- Network connectivity issues

**Resolution**:
- Retry after delay
- Check API service status
- Contact system administrator

### NEWS002 - 뉴스 필터링 오류
**Description**: News filtering error  
**HTTP Status**: 500 Internal Server Error  
**Typical Causes**:
- Content processing failure
- Invalid news format
- Filter rule error

**Resolution**:
- Skip problematic content
- Update filter rules
- Log for manual review

## 🔧 System (SYS)

### SYS001 - 시스템 오류가 발생했습니다
**Description**: System error occurred  
**HTTP Status**: 500 Internal Server Error  
**Typical Causes**:
- Unexpected system failure
- Database connectivity issue
- Infrastructure problem

**Resolution**:
- Retry request
- Contact support
- Check system status

### SYS002 - 외부 API 연결 실패
**Description**: External API connection failed  
**HTTP Status**: 502 Bad Gateway  
**Typical Causes**:
- Government API unavailable
- Network timeout
- API service maintenance

**Resolution**:
- Wait and retry
- Check API service status
- Use cached data if available

### SYS003 - 데이터베이스 연결 오류
**Description**: Database connection error  
**HTTP Status**: 503 Service Unavailable  
**Typical Causes**:
- Database server down
- Connection pool exhausted
- Network issues

**Resolution**:
- Retry after delay
- Contact system administrator
- Check system resources

## ✅ Validation (VAL)

### VAL001 - 필수 입력값이 누락되었습니다
**Description**: Required input missing  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Required field empty
- Null value submitted
- Missing request parameter

**Resolution**:
- Provide all required fields
- Check field names
- Validate input data

### VAL002 - 입력값 형식이 올바르지 않습니다
**Description**: Invalid input format  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- Wrong data type
- Invalid format pattern
- Out of range value

**Resolution**:
- Use correct data format
- Check value ranges
- Validate input constraints

### VAL003 - 입력값이 너무 깁니다
**Description**: Input value too long  
**HTTP Status**: 400 Bad Request  
**Typical Causes**:
- String exceeds maximum length
- Text content too large
- File size too big

**Resolution**:
- Reduce input length
- Check length limits
- Split into smaller parts

## 🚨 Error Response Format

### Standard Error Response
```json
{
    "status": "fail",
    "data": {
        "code": "ERROR_CODE",
        "message": "User-friendly error message in Korean",
        "details": "Technical details for debugging (optional)"
    }
}
```

### Validation Error Response
```json
{
    "status": "fail",
    "data": {
        "code": "VAL001",
        "message": "입력값 검증에 실패했습니다",
        "details": "Validation failed",
        "fieldErrors": [
            {
                "field": "email",
                "message": "이메일 형식이 올바르지 않습니다"
            },
            {
                "field": "password",
                "message": "비밀번호는 8자 이상이어야 합니다"
            }
        ]
    }
}
```

## 🛠️ Error Handling Best Practices

### For Developers
1. **Use Specific Error Codes**: Don't use generic codes
2. **Provide Context**: Include helpful details for debugging
3. **Korean Messages**: User-facing messages in Korean
4. **Consistent Format**: Follow standard error response structure
5. **Log Errors**: Include error codes in application logs

### For Frontend
1. **Handle All Codes**: Implement handlers for all possible error codes
2. **User-Friendly Display**: Show Korean messages to users
3. **Retry Logic**: Implement retry for transient errors
4. **Fallback UI**: Graceful degradation for system errors
5. **Error Reporting**: Report unexpected errors to monitoring

### For Users
1. **Read Messages**: Error messages provide helpful guidance
2. **Retry When Suggested**: Some errors are temporary
3. **Contact Support**: Include error code when reporting issues
4. **Check Input**: Validation errors indicate input problems

---

**Error Code Reference Version**: 1.0  
**Last Updated**: 2024-06-19  
**Total Error Codes**: 50+