#!/bin/bash

# Set Vault address and token
export VAULT_ADDR=${VAULT_ADDR:-http://localhost:8200}
export VAULT_TOKEN=${VAULT_TOKEN:-myroot}

# Wait for Vault to be ready
until vault status > /dev/null 2>&1; do
  echo "Waiting for Vault to be ready..."
  sleep 2
done

echo "Vault is ready!"

# Enable KV secrets engine if not already enabled
if ! vault secrets list -format=json | grep -q 'secret/'; then
  echo "Enabling KV secrets engine..."
  vault secrets enable -path=secret kv-v2
fi

# Write secrets
echo "Writing secrets to Vault..."

# Backend database secrets
vault kv put secret/backend-db \
  url="jdbc:sqlserver://datn.ct4sc6oi2i77.ap-southeast-1.rds.amazonaws.com:1433;databaseName=datn;encrypt=true;trustServerCertificate=true" \
  username="admin" \
  password="chucmoinguoidautotnghiep"

# Core banking database secrets
vault kv put secret/corebanking-db \
  url="jdbc:sqlserver://datn.ct4sc6oi2i77.ap-southeast-1.rds.amazonaws.com:1433;databaseName=core_banking;encrypt=true;trustServerCertificate=true" \
  username="admin" \
  password="chucmoinguoidautotnghiep"

# Core banking API secrets
vault kv put secret/corebanking-api \
  api-key="test-api-key" \
  accountNumber="66666" \
  bankCode="MB Bank"

# Notification mail secrets
vault kv put secret/notification-mail \
  host="smtp.gmail.com" \
  port="587" \
  username="25112002abcas@gmail.com" \
  password="impc jrnp whgh agxk"

echo "All secrets have been written to Vault." 