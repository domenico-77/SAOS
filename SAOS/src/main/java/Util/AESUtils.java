package Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import secureWebApp.VaultManager;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;


/**
 * La classe AESUtils fornisce metodi per generare una chiave AES, cifrare e decifrare dati utilizzando l'algoritmo AES in modalità GCM.
 * I metodi di cifratura e decifratura utilizzano una chiave AES recuperata da Vault e un IV (Initialization Vector) casuale per garantire la sicurezza.
 */
public class AESUtils {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_LENGHT = 256;
    private static final int AUTH_TAG_LENGHT = 128;
    private static final int IV_LENGHT = 12;
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_LENGHT);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }
    
    // Metodo di cifratura AES
    public static byte[] encrypt(byte[] data, String path_vault) throws Exception {
    	VaultManager vaultManager = VaultManager.getInstance();
    	SecretKey secretKey = vaultManager.retrieveSecretKey(path_vault);
        
        byte[] iv = new byte[IV_LENGHT];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(AUTH_TAG_LENGHT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        byte[] ciphertext = cipher.doFinal(data);
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
        byteBuffer.put(iv);
        byteBuffer.put(ciphertext);
 
        return byteBuffer.array();
    }

    // Metodo di decifratura AES
    public static byte[] decrypt(byte[] encryptedData, String path_vault) throws Exception {
    	VaultManager vaultManager = VaultManager.getInstance();
    	SecretKey secretKey = vaultManager.retrieveSecretKey(path_vault);
        
        ByteBuffer byteBufferDec = ByteBuffer.wrap(encryptedData);
        byte[] ivDec = new byte[IV_LENGHT];
        byteBufferDec.get(ivDec);
        byte[] ciphertextDec = new byte[byteBufferDec.remaining()];
        byteBufferDec.get(ciphertextDec);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(AUTH_TAG_LENGHT, ivDec));
        return cipher.doFinal(ciphertextDec);
        
    }
    
    // Main per la generazione di nuove chiavi AES e test di crittografia
    public static void main(String[] args) {
        try {
           VaultManager vaultManager = VaultManager.getInstance();
           vaultManager.saveSecretKey(AESUtils.generateKey(), VaultManager.PATH_AES_REMEMBER_ME);
           vaultManager.saveSecretKey(AESUtils.generateKey(), VaultManager.PATH_AES_PROPOSTE);
           
           String plaintext = "Test crittografia AES/GCM";
           byte[] encryptedData = AESUtils.encrypt(plaintext.getBytes(), VaultManager.PATH_AES_REMEMBER_ME);
           System.out.println("Dati cifrati: " + java.util.Base64.getEncoder().encodeToString(encryptedData));
           
           byte[] decryptedData = AESUtils.decrypt(encryptedData, VaultManager.PATH_AES_REMEMBER_ME);
           System.out.println("Dati decifrati: " + new String(decryptedData));
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

    
    
