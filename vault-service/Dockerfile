FROM hashicorp/vault:1.19.4

# Install bash for the init script
RUN apk add --no-cache bash

# Copy initialization script
COPY init-vault.sh /init-vault.sh
RUN chmod +x /init-vault.sh

# Expose Vault port
EXPOSE 8200

# Set environment variables
ENV VAULT_DEV_ROOT_TOKEN_ID=myroot
ENV VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200

# Start Vault and initialize secrets
CMD ["sh", "-c", "vault server -dev -dev-root-token-id=myroot -dev-listen-address=0.0.0.0:8200 & sleep 5 && /init-vault.sh && wait"] 