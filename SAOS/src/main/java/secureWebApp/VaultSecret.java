package secureWebApp;

import java.util.Arrays;

/**
 * La classe VaultSecret rappresenta un segreto che può essere recuperato o memorizzato in Vault. 
 * Contiene una coppia di chiave e valore, dove sia la chiave che il valore sono memorizzati come array 
 * di caratteri (char[]) per garantire la sicurezza e la gestione corretta dei dati sensibili.
 */
public class VaultSecret {
    private char[] key;
    private char[] value;

    public VaultSecret() {
    }

    // Metodo get della chiave
    public char[] getKey() {
        return key;
    }
    
    // Metodo get del valore
    public char[] getValue() {
        return value;
    }
    	
    // Metodo set della chiave
    public void setKey(char[] key) {
    	this.key = key;
    }
    
    // Metodo set del valore
    public void setValue(char[] value) {
    	this.value = value;
    }
    
    // Metodo per il clear dei segreti
    public void clearSecret() {
    	Arrays.fill(key, '0'); 
    	Arrays.fill(value, '0'); 
    }
    
}