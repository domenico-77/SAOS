package Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * La classe HashUtils fornisce metodi per la gestione della sicurezza tramite hashing e salatura,
 * utilizzando tecniche di hashing sicure per proteggere password e dati sensibili.
 * La classe implementa il metodo PBKDF2 con HMAC-SHA-512 per l'hashing delle password, 
 * che include l'uso di un sale (salt) per aumentare la sicurezza contro gli attacchi di rainbow table.
 * Inoltre, fornisce metodi per la validazione delle password e per l'hashing di dati generici (come proposte) 
 * con un sale aggiuntivo.
 */
public class HashUtils {
    private static final int SALT_LENGTH = 128;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 512;
    
    // Generazione sicura di un sale
    public static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH]; 
        sr.nextBytes(salt);
        return salt;
    }
    
    // Hashing di una password 
    public static byte[] hashPassword(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

            byte[] hash = skf.generateSecret(spec).getEncoded();

            spec.clearPassword();
            
            return hash;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }
    
    // Metodo per la validazione di una password 
    public static boolean validatePassword(char[] enteredPassword, byte[] storedHash, byte[] storedSalt) {
    	 byte[] computedHash = hashPassword(enteredPassword, storedSalt);
    	 return Arrays.equals(storedHash, computedHash); 
    }
    
    // Hashing di una proposta 
    public static byte[] hashProposta(byte[] data, byte[] salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        
        byte[] dataWithSalt = new byte[data.length + salt.length];
        System.arraycopy(salt, 0, dataWithSalt, 0, salt.length);
        System.arraycopy(data, 0, dataWithSalt, salt.length, data.length);
        
        return digest.digest(dataWithSalt);
    }
    
    // Metodo per la validazione di una proposta
    public static boolean validateProposta(byte[] data, byte[] hashProposta, byte[] storedSalt) throws Exception {
    	byte[] hashFile = hashProposta(data, storedSalt);
    	return Arrays.equals(hashProposta, hashFile);   
    }
}