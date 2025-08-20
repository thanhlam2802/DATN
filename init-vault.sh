#!/bin/sh
set -e

# Thiết lập biến môi trường
export VAULT_ADDR=${VAULT_ADDR:-http://vault-server:8200}
export VAULT_TOKEN=${VAULT_TOKEN:-myroot}

# Chờ Vault sẵn sàng
until vault status > /dev/null 2>&1; do
  echo "Waiting for Vault to be ready..."
  sleep 2
done

# Kích hoạt KV v2, nếu đã mount thì bỏ qua
if vault secrets list -format=json | grep -q 'secret/'; then
  echo "KV v2 at 'secret/' already enabled"
else
  vault secrets enable -path=secret kv-v2
fi

echo "Putting backend-db secret..."
vault kv put secret/backend-db \
  username=admin \
  password=chucmoinguoidautotnghiep \
  url="jdbc:sqlserver://datn.ct4sc6oi2i77.ap-southeast-1.rds.amazonaws.com:1433;databaseName=datn;encrypt=true;trustServerCertificate=true"

echo "Putting corebanking-db secret..."
vault kv put secret/corebanking-db \
  username=sa \
  password=2257 \
  url="jdbc:sqlserver://localhost:1433;databaseName=core_banking;encrypt=false"

echo "Putting corebanking-api secret..."
vault kv put secret/corebanking-api \
  api-key=test-api-key \
  accountNumber=66666 \
  bankCode="MB Bank"

echo "Putting notification-mail secret..."
vault kv put secret/notification-mail \
  username=25112002abcas@gmail.com \
  password="impc jrnp whgh agxk" \
  host=smtp.gmail.com \
  port=587

echo "All secrets have been written to Vault."