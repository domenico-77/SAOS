package Util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * La classe TokenUtils fornisce un metodo per la generazione di stringhe di token sicure e casuali.
 */
public class TokenUtils {
	
	// Genera una stringa casuale di lunghezza fissa in modo sicuro
	public static char[] getRandomString() {
        int length = 16;
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).toCharArray();
    }
}
