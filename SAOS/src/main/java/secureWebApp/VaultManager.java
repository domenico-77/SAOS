package secureWebApp;

import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.LogicalResponse;

/**
 * La classe VaultManager è responsabile per la gestione delle interazioni con Vault, un sistema di gestione 
 * dei segreti. Offre funzionalità per autenticarsi, leggere e scrivere segreti nel Vault, nonché per 
 * gestire chiavi di crittografia e credenziali utenti db. È implementata come un singleton per garantire una 
 * sola istanza di accesso a Vault nell'applicazione.
 */
public class VaultManager {
    private static VaultManager instance;
    private Vault vault;
    public static final String PATH_DB_USERS = "secret/DBUsers";
    public static final String PATH_DB_VERIFICA_USERS = "secret/DBVerificaUsers";
    public static final String PATH_DB_PROPOSTE = "secret/DBProposte";
    public static final String PATH_DB_VERIFICA_PROPOSTE = "secret/DBVerificaProposte";
    public static final String PATH_DB_REMEMBER_ME = "secret/DBRememberMe";
    public static final String PATH_AES_REMEMBER_ME = "secret/AES1";
    public static final String PATH_AES_PROPOSTE = "secret/AES2";
    
    private VaultManager() throws VaultException {
    	if (instance != null) {
            throw new IllegalStateException("Classe già istanziata!");
        }
    }
    
    // Metodo per l'ottenimento dell'istanza della classe VaultManager
    public static VaultManager getInstance() throws VaultException {
        if (instance == null) {
            synchronized (VaultManager.class) {
                if (instance == null) {
                    instance = new VaultManager();
                }
            }
        }
        return instance;
    }
    
    // Metodo di autenticazione tramite token per stabilire una connessione con Vault.
    boolean auth(char[] token) throws VaultException  {
        VaultConfig config = new VaultConfig()
                .address("https://localhost:8200")
                .sslConfig(new SslConfig().verify(true)) 
                .token(new String(token))  
                .engineVersion(1)  
                .build();
        
        Arrays.fill(token, '0'); 
        vault = new Vault(config);
        
    	LogicalResponse response = vault.logical().read("secret/test");
    
    	if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return false;
        } 
        
    	System.out.println("Connessione a Vault riuscita. Dati: " + response.getData());
        return true;
    }
    
    // Lettura di segreti da Vault tramite il metodo `getSecret`.
    public VaultSecret getSecret(String pathSecretVault) throws VaultException {
        LogicalResponse response = vault.logical().read(pathSecretVault);

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            VaultSecret vaultSecret = new VaultSecret();
            Map<String, String> data = response.getData();

            for (Map.Entry<String, String> entry : data.entrySet()) {
                vaultSecret.setKey(entry.getKey().toCharArray());
                vaultSecret.setValue(entry.getValue().toCharArray());
            }

            return vaultSecret;

        } else {
            throw new VaultException("Error: No data found for the secret or secret does not exist.");
        }
    }
    
    // Memorizzazione di chiavi segrete in Vault 
    public void saveSecretKey(SecretKey secretKey, String pathSecretVault) throws VaultException, NoSuchAlgorithmException {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        Map<String, Object> data = new HashMap<>();
        data.put("data", encodedKey);

        LogicalResponse response = vault.logical().write(pathSecretVault, data);
        System.out.println("Key saved in Vault: " + response.getRestResponse().getStatus());
    }
    
    // Recupero delle chiavi segrete da Vault con il metodo
    public SecretKey retrieveSecretKey(String pathSecretVault) throws VaultException {
        LogicalResponse response = vault.logical().read(pathSecretVault);

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            byte[] decodedKey = Base64.getDecoder().decode(response.getData().get("data"));
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            Arrays.fill(decodedKey, (byte) 0);
            return secretKey;
        } else {
            throw new VaultException("Error: No data found for the secret or secret does not exist.");
        }
    }
}
