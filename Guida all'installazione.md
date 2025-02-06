# Guida per la replicazione dell'applicazione

## Requisiti

Per la corretta replicazione dell'applicazione, è necessario che siano installati i seguenti software:

- **Java**: OpenJDK 23.0.1
- **Apache Tomcat**: 9.0.89
- **MySQL**: Versione 8.0.40 per macOS 15.1 su architettura arm64 (installato tramite Homebrew)
- **Eclipse IDE per Java Enterprise e Web Developers**: Versione 2024-09 (4.33.0)
- **Vault (HashiCorp)**: Versione 1.18.2

### Librerie utilizzate:
- commons-text-1.12.0.jar
- mysql-connector-j-9.1.0.jar
- tika-app-3.0.0.jar
- vault-java-driver-5.1.0.jar

---

## Configurazione MySQL

Creare un file `conf_db.sql` con le seguenti righe:

```sql
CREATE DATABASE labSAOS;

USE labSAOS;

CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    nickname VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    img MEDIUMBLOB NOT NULL,
    salt BLOB NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE verifica_users (
    id INT NOT NULL,
    hash BLOB NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE proposte (
    id INT NOT NULL AUTO_INCREMENT,
    nickname VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_data BLOB NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    salt BLOB NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE verifica_proposte (
    id INT NOT NULL,
    hash BLOB NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES proposte(id) ON DELETE CASCADE
);

CREATE TABLE remember_me_tokens (
    nickname VARCHAR(255) NOT NULL,
    token BLOB NOT NULL,
    expiry_date DATETIME NOT NULL,
    PRIMARY KEY (nickname)
);

CREATE USER 'users_user'@'localhost' IDENTIFIED BY 'AHoQAm:1IvZ3xsWwzV3xA`Gi[y7bK9';
GRANT SELECT, INSERT, UPDATE ON labSAOS.users TO 'users_user'@'localhost';
```

Eseguire il comando:
```sh
SOURCE path/conf_db.sql
```

---

## Download HashiCorp Vault

### Windows

1. Installa Chocolatey:
   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
   ```
2. Installa Vault:
   ```powershell
   choco install vault -y
   ```

### MacOS

1. Installa Homebrew:
   ```sh
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```
2. Installa Vault:
   ```sh
   brew install vault
   ```

### Linux

Scaricare il binario dal link ufficiale: [Install Vault](https://developer.hashicorp.com/vault/install)

---

## Configurazione HashiCorp Vault

1. Creare una cartella `Vault` e un file `config_vault.hcl` al suo interno con il seguente contenuto:

```hcl
listener "tcp" {
  address = "127.0.0.1:8200"
  tls_cert_file = "tls/server.crt"
  tls_key_file = "tls/server.key"
}

storage "raft" {
  path = "./data"
  node_id = "node1"
}

disable_mlock = true
ui            = true
cluster_addr  = "https://127.0.0.1:8201"
api_addr      = "https://127.0.0.1:8200"
```

2. Creare le cartelle `data` e `tls` all'interno della directory `Vault`.
3. Generare i certificati TLS:

```sh
cd Vault
openssl genpkey -algorithm RSA -out tls/server.key -pkeyopt rsa_keygen_bits:2048
openssl req -new -key tls/server.key -out tls/server.csr
openssl x509 -req -days 365 -in tls/server.csr -signkey tls/server.key -out tls/server.crt
```

4. Impostare le variabili d'ambiente:

```sh
export VAULT_ADDR=https://localhost:8200
export VAULT_SKIP_VERIFY=true
```

5. Avviare Vault:

```sh
vault server -config=config_vault.hcl
```

6. In un nuovo terminale:

```sh
vault operator init
```

7. Salvare le chiavi generate e poi eseguire:

```sh
vault operator unseal
```
ed effeettuare il login con il roken di root
```sh
vault login
```

8. Accedere all'interfaccia web su `https://localhost:8200/`, inserire il token root, e attivare il motore KV.

9. Creare una policy Vault:

```hcl
path "secret/*" {
  capabilities = ["create", "read"]
}
```

10. Creare un token:

```sh
vault token create -policy=token_policy -ttl=720h
```

---

## Manutenzione del Token

- Rinnovo del token:
  ```sh
  vault token renew TOKEN
  ```
- Revoca del token:
  ```sh
  vault token revoke TOKEN
  ```

---

## Download libreria Vault Java Driver

Scaricare e importare la libreria `vault-java-driver-5.1.0.jar` da:
[Vault Java Driver 5.1.0](https://repo1.maven.org/maven2/com/bettercloud/vault-java-driver/5.1.0/vault-java-driver-5.1.0.jar)
