# Deploy to Render with Infrastructure Services
Write-Host "Deploying to Render with infrastructure services..." -ForegroundColor Green

# Check if render.yaml exists
if (-not (Test-Path "render.yaml")) {
    Write-Host "Error: render.yaml not found!" -ForegroundColor Red
    exit 1
}

# Deploy using Render Blueprint
Write-Host "Deploying services to Render..." -ForegroundColor Yellow
Write-Host "This will deploy:" -ForegroundColor Cyan
Write-Host "- datn-backend (Spring Boot)" -ForegroundColor White
Write-Host "- datn-core-banking (Spring Boot)" -ForegroundColor White
Write-Host "- datn-notification (Spring Boot)" -ForegroundColor White
Write-Host "- datn-redis (Redis Cache)" -ForegroundColor White
Write-Host "- datn-kafka (Apache Kafka)" -ForegroundColor White
Write-Host "- datn-vault (HashiCorp Vault)" -ForegroundColor White

Write-Host "`nNote: This deployment includes infrastructure services (Redis, Kafka, Vault) that are required for payment functionality." -ForegroundColor Yellow
Write-Host "The services will be configured to communicate with each other using Render's internal networking." -ForegroundColor Yellow

Write-Host "`nDeploying..." -ForegroundColor Green

# You can deploy this using Render's web interface or CLI
Write-Host "`nTo deploy:" -ForegroundColor Cyan
Write-Host "1. Go to https://render.com" -ForegroundColor White
Write-Host "2. Create a new Blueprint Instance" -ForegroundColor White
Write-Host "3. Connect your GitHub repository" -ForegroundColor White
Write-Host "4. Select this render.yaml file" -ForegroundColor White
Write-Host "5. Deploy!" -ForegroundColor White

Write-Host "`nOr use Render CLI:" -ForegroundColor Cyan
Write-Host "render blueprint apply" -ForegroundColor White

Write-Host "`nAfter deployment, your services will be available at:" -ForegroundColor Green
Write-Host "- Backend API: https://datn-backend.onrender.com" -ForegroundColor White
Write-Host "- Core Banking: https://datn-core-banking.onrender.com" -ForegroundColor White
Write-Host "- Notification: https://datn-notification.onrender.com" -ForegroundColor White
Write-Host "- Redis: datn-redis.onrender.com:6379" -ForegroundColor White
Write-Host "- Kafka: datn-kafka.onrender.com:9094" -ForegroundColor White
Write-Host "- Vault: https://datn-vault.onrender.com" -ForegroundColor White

Write-Host "`nImportant Notes:" -ForegroundColor Yellow
Write-Host "- Vault will be initialized with all necessary secrets" -ForegroundColor White
Write-Host "- Kafka will be configured for message queuing between services" -ForegroundColor White
Write-Host "- Redis will be used for caching in the core banking service" -ForegroundColor White
Write-Host "- All services will be configured to communicate with each other" -ForegroundColor White

Write-Host "`nDeployment script completed!" -ForegroundColor Green 