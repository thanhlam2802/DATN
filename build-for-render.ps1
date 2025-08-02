Write-Host "=== Building all services for Render deployment ===" -ForegroundColor Green

# Build Backend
Write-Host "1. Building Backend..." -ForegroundColor Yellow
Set-Location backend
./mvnw clean package -DskipTests
Write-Host "Backend built successfully!" -ForegroundColor Green
Set-Location ..

# Build Core Banking Service
Write-Host "2. Building Core Banking Service..." -ForegroundColor Yellow
Set-Location core-banking-service
./mvnw clean package -DskipTests
Write-Host "Core Banking Service built successfully!" -ForegroundColor Green
Set-Location ..

# Build Notification Service
Write-Host "3. Building Notification Service..." -ForegroundColor Yellow
Set-Location notification-service
./mvnw clean package -DskipTests
Write-Host "Notification Service built successfully!" -ForegroundColor Green
Set-Location ..

Write-Host "=== All services built successfully! ===" -ForegroundColor Green
Write-Host "Now you can deploy to Render:" -ForegroundColor Cyan
Write-Host "1. Go to https://render.com" -ForegroundColor Cyan
Write-Host "2. Create new Web Service" -ForegroundColor Cyan
Write-Host "3. Connect your GitHub repository" -ForegroundColor Cyan
Write-Host "4. Set build command and start command" -ForegroundColor Cyan 