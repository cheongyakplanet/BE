# Deployment Guide

## 🚀 Environment Setup & Deployment

This guide provides comprehensive instructions for setting up development, testing, and production environments for CheonYakPlanet.

## 📋 Prerequisites

### System Requirements
- **Java**: OpenJDK 17 or higher
- **Database**: MySQL 8.0+
- **Build Tool**: Gradle 8.0+
- **Memory**: Minimum 4GB RAM (8GB recommended)
- **Storage**: 10GB+ available space

### Development Tools
- **IDE**: IntelliJ IDEA or VS Code
- **Git**: Version 2.20+
- **Docker**: For containerized deployment (optional)
- **Postman**: For API testing

## 🔧 Environment Configuration

### 1. Local Development Environment

#### Java Installation
```bash
# Install OpenJDK 17
sudo apt update
sudo apt install openjdk-17-jdk

# Verify installation
java -version
javac -version
```

#### MySQL Database Setup
```bash
# Install MySQL 8.0
sudo apt install mysql-server-8.0

# Secure installation
sudo mysql_secure_installation

# Create database and user
mysql -u root -p
```

```sql
-- Create database
CREATE DATABASE planet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'planet_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON planet.* TO 'planet_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Project Setup
```bash
# Clone repository
git clone https://github.com/your-org/cheonyakplanet-be.git
cd cheonyakplanet-be

# Make gradlew executable
chmod +x gradlew

# Build project
./gradlew build
```

### 2. Environment Variables

#### Required Environment Variables
Create `.env` file in project root:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=planet
DB_USERNAME=planet_user
DB_PASSWORD=secure_password

# JWT Configuration
JWT_SECRET=your_super_secure_jwt_secret_key_min_256_bits
JWT_ACCESS_TOKEN_EXPIRY=3600000     # 60 minutes
JWT_REFRESH_TOKEN_EXPIRY=86400000   # 24 hours

# External API Keys
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret
KAKAO_API_KEY=your_kakao_api_key
LH_API_KEY=your_lh_api_key
REALESTATE_API_KEY=your_realestate_api_key
GEMINI_API_KEY=your_gemini_api_key

# Email Configuration (Gmail SMTP)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Application Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
NEWS_CRAWL_ENABLED=true
CHAT_DAILY_LIMIT=15
SCHEDULER_ENABLED=true

# Logging Configuration
LOG_LEVEL=DEBUG
LOG_FILE_PATH=./logs/application.log
```

#### Environment-Specific Configuration

**Development (`application-dev.yml`):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=false&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  logging:
    level:
      org.cheonyakplanet.be: DEBUG
      org.springframework.web: DEBUG
```

**Production (`application-prod.yml`):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=true&requireSSL=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  logging:
    level:
      org.cheonyakplanet.be: INFO
      org.springframework.web: WARN
```

### 3. External API Setup

#### Naver News API
1. Visit [Naver Developers](https://developers.naver.com/)
2. Create application and get Client ID/Secret
3. Add to environment variables

#### Kakao Developers
1. Visit [Kakao Developers](https://developers.kakao.com/)
2. Create application for Maps API
3. Get REST API key

#### LH Corporation API
1. Visit [Korea Data Portal](https://www.data.go.kr/)
2. Register for LH Corporation APIs
3. Get service key

#### Google Gemini API
1. Visit [Google AI Studio](https://makersuite.google.com/)
2. Create project and get API key
3. Enable Gemini Pro API

## 🏃‍♂️ Running the Application

### Development Mode
```bash
# Run with development profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Or using environment variable
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

### Production Mode
```bash
# Build production JAR
./gradlew clean build -Pprod

# Run production JAR
java -jar -Dspring.profiles.active=prod build/libs/be-1.0.0.jar
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests UserServiceTest

# Generate coverage report
./gradlew jacocoTestReport

# Verify coverage thresholds
./gradlew jacocoTestCoverageVerification
```

## 🐳 Docker Deployment

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy built JAR
COPY build/libs/be-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - DB_USERNAME=planet_user
      - DB_PASSWORD=secure_password
    depends_on:
      - mysql
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: planet
      MYSQL_USER: planet_user
      MYSQL_PASSWORD: secure_password
      MYSQL_ROOT_PASSWORD: root_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    restart: unless-stopped

volumes:
  mysql_data:
```

### Docker Commands
```bash
# Build and run
docker-compose up --build

# Run in background
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Clean up
docker-compose down -v
```

## ☁️ Cloud Deployment

### AWS Deployment

#### EC2 Setup
```bash
# Update system
sudo yum update -y

# Install Java 17
sudo yum install java-17-amazon-corretto-devel -y

# Install MySQL client
sudo yum install mysql -y

# Create application user
sudo useradd -m -s /bin/bash cheonyak
sudo mkdir -p /opt/cheonyakplanet
sudo chown cheonyak:cheonyak /opt/cheonyakplanet
```

#### RDS Database Setup
1. Create RDS MySQL 8.0 instance
2. Configure security groups for application access
3. Create database and user:

```sql
CREATE DATABASE planet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'planet_user'@'%' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON planet.* TO 'planet_user'@'%';
FLUSH PRIVILEGES;
```

#### Application Deployment
```bash
# Copy JAR to server
scp build/libs/be-1.0.0.jar ec2-user@your-server:/opt/cheonyakplanet/

# Create systemd service
sudo vim /etc/systemd/system/cheonyakplanet.service
```

```ini
[Unit]
Description=CheonYakPlanet Application
After=network.target

[Service]
Type=simple
User=cheonyak
WorkingDirectory=/opt/cheonyakplanet
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod be-1.0.0.jar
Restart=always
RestartSec=10

Environment=DB_HOST=your-rds-endpoint
Environment=DB_USERNAME=planet_user
Environment=DB_PASSWORD=secure_password
Environment=JWT_SECRET=your_jwt_secret
# ... other environment variables

[Install]
WantedBy=multi-user.target
```

```bash
# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable cheonyakplanet
sudo systemctl start cheonyakplanet

# Check status
sudo systemctl status cheonyakplanet
```

### Load Balancer Setup
```bash
# Install nginx
sudo yum install nginx -y

# Configure nginx
sudo vim /etc/nginx/nginx.conf
```

```nginx
upstream cheonyakplanet {
    server localhost:8080;
}

server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://cheonyakplanet;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /ws/ {
        proxy_pass http://cheonyakplanet;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
}
```

## 🔒 Security Configuration

### SSL/TLS Setup
```bash
# Install certbot
sudo yum install certbot python3-certbot-nginx -y

# Get SSL certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

### Firewall Configuration
```bash
# Configure security groups (AWS)
# Allow ports: 22 (SSH), 80 (HTTP), 443 (HTTPS), 8080 (Application)

# Or using ufw (Ubuntu)
sudo ufw allow ssh
sudo ufw allow http
sudo ufw allow https
sudo ufw allow 8080
sudo ufw enable
```

### Database Security
```sql
-- Remove default users
DELETE FROM mysql.user WHERE User='';
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');

-- Update root password
ALTER USER 'root'@'localhost' IDENTIFIED BY 'strong_password';

-- Create application user with limited privileges
CREATE USER 'planet_app'@'%' IDENTIFIED BY 'app_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON planet.* TO 'planet_app'@'%';
```

## 📊 Monitoring & Observability

### Application Monitoring
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### Log Configuration
```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/opt/cheonyakplanet/logs/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/opt/cheonyakplanet/logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connectivity
curl http://localhost:8080/actuator/health/db

# Custom health indicators
curl http://localhost:8080/actuator/health/external-apis
```

## 🔄 CI/CD Pipeline

### GitHub Actions
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run tests
        run: ./gradlew test
      
      - name: Generate coverage report
        run: ./gradlew jacocoTestReport
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Build application
        run: ./gradlew build -Pprod
      
      - name: Build Docker image
        run: docker build -t cheonyakplanet:${{ github.sha }} .
      
      - name: Deploy to server
        run: |
          # Deploy script here
```

## 🛠️ Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check MySQL status
sudo systemctl status mysql

# Check connectivity
mysql -h $DB_HOST -P $DB_PORT -u $DB_USERNAME -p$DB_PASSWORD

# Check firewall
sudo ufw status
```

#### Application Startup Issues
```bash
# Check logs
sudo journalctl -u cheonyakplanet -f

# Check port usage
sudo netstat -tlnp | grep 8080

# Check Java version
java -version
```

#### Memory Issues
```bash
# Increase heap size
java -Xmx2048m -Xms1024m -jar app.jar

# Monitor memory usage
top -p $(pgrep java)
```

### Performance Tuning

#### JVM Options
```bash
# Production JVM settings
java -jar \
  -Xmx4g \
  -Xms2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  app.jar
```

#### Database Optimization
```sql
-- Optimize queries
EXPLAIN SELECT * FROM subscription_info WHERE region = 'Seoul';

-- Add indexes
CREATE INDEX idx_subscription_region_city ON subscription_info(region, city);

-- Update statistics
ANALYZE TABLE subscription_info;
```

## 📋 Deployment Checklist

### Pre-deployment
- [ ] Environment variables configured
- [ ] Database setup and migrated
- [ ] External API keys obtained and tested
- [ ] SSL certificates configured
- [ ] Firewall rules configured
- [ ] Monitoring setup
- [ ] Backup strategy implemented

### Post-deployment
- [ ] Application health check passed
- [ ] All endpoints responding correctly
- [ ] WebSocket connections working
- [ ] Scheduled tasks running
- [ ] Logs generating properly
- [ ] Monitoring alerts configured
- [ ] Performance baseline established

### Rollback Plan
- [ ] Previous version JAR available
- [ ] Database backup created
- [ ] Rollback script tested
- [ ] Team notification process
- [ ] Health check validation

---

**Deployment Guide Version**: 1.0  
**Last Updated**: 2024-06-19  
**Environment**: Production-ready