# Render Deployment Guide

## Prerequisites
- GitHub repository với code
- Render account (free tier available)
- Maven và Java 21

## Step 1: Build Services
```powershell
.\build-for-render.ps1
```

## Step 2: Deploy to Render

### Backend Service
1. **Truy cập**: https://render.com/dashboard
2. **Click "New +" → "Web Service"**
3. **Connect GitHub repository**
4. **Configure service**:
   - **Name**: `datn-backend`
   - **Root Directory**: `backend`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/backend-0.0.1-SNAPSHOT.jar`
   - **Environment**: `Docker`
5. **Environment Variables**:
   ```
   SPRING_DATASOURCE_URL=jdbc:sqlserver://34.124.165.193:1433;databaseName=datn;encrypt=true;trustServerCertificate=true
   SPRING_PROFILES_ACTIVE=production
   PORT=8080
   ```
6. **Click "Create Web Service"**

### Core Banking Service
1. **Click "New +" → "Web Service"**
2. **Connect GitHub repository**
3. **Configure service**:
   - **Name**: `datn-core-banking`
   - **Root Directory**: `core-banking-service`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/core-banking-service-0.0.1-SNAPSHOT.jar`
   - **Environment**: `Docker`
4. **Environment Variables**:
   ```
   SPRING_DATASOURCE_URL=jdbc:sqlserver://34.124.165.193:1433;databaseName=core_banking;encrypt=true;trustServerCertificate=true
   SPRING_PROFILES_ACTIVE=production
   PORT=8081
   ```
5. **Click "Create Web Service"**

### Notification Service
1. **Click "New +" → "Web Service"**
2. **Connect GitHub repository**
3. **Configure service**:
   - **Name**: `datn-notification`
   - **Root Directory**: `notification-service`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/Notification-service-0.0.1-SNAPSHOT.jar`
   - **Environment**: `Docker`
4. **Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=production
   PORT=9821
   ```
5. **Click "Create Web Service"**

## Step 3: Get Service URLs
Sau khi deploy thành công, bạn sẽ có URLs:
- Backend: `https://datn-backend.onrender.com`
- Core Banking: `https://datn-core-banking.onrender.com`
- Notification: `https://datn-notification.onrender.com`

## Environment Variables

### Backend
- `SPRING_DATASOURCE_URL`: Database connection
- `SPRING_PROFILES_ACTIVE`: production
- `PORT`: 8080

### Core Banking Service
- `SPRING_DATASOURCE_URL`: Database connection
- `SPRING_PROFILES_ACTIVE`: production
- `PORT`: 8081

### Notification Service
- `SPRING_PROFILES_ACTIVE`: production
- `PORT`: 9821

## Render Features
- **Free Tier**: 750 hours/month
- **Auto-deploy**: Tự động deploy khi push code
- **Custom domains**: Có thể dùng domain riêng
- **SSL**: Tự động có HTTPS
- **Logs**: Xem logs real-time

## Troubleshooting
1. **Build fails**: Kiểm tra Maven và Java version
2. **Service không start**: Kiểm tra environment variables
3. **Database connection fails**: Kiểm tra database URL
4. **Port issues**: Đảm bảo PORT environment variable đúng

## Notes
- Render sẽ tự động detect Dockerfile
- Services sẽ restart khi có lỗi
- Có thể scale services trong dashboard
- Logs có thể xem real-time 