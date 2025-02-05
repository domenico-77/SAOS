package secureWebApp;

import java.util.Arrays;


/**
 * La classe Credentials rappresenta le credenziali di accesso di un utente
 * e fornisce metodi per la gestione e la validazione dei dati.
 */
class Credentials {
	private String nickname;
	private String email;
	private char[] password;
	private static String EMAIL_PATTERN = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
	private static final String NICKNAME_PATTERN = "^[a-zA-Z0-9._-]{3,20}$";
	private static final int MAX_PASS_LENGHT = 8;
	
	// Metodo set della password
	void setPassword(char[] password) {
		this.password = password;
	}
	
	// Metodo set della email
	void setEmail(String email) {
		this.email = email;
	}
	
	// Metodo get della email
	String getEmail() {
		return this.email;
	}
	
	// Metodo set del nickname
	void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	// Metodo get del nickname
	String getNickname() {
		return this.nickname;
	}
	
	// Metodo get della password
	char[] getPassword() {
		return this.password;
	}
	
	// Metodo che effettua il clear della password
	void clearPassword() {
		Arrays.fill(getPassword(), '0');
	}
	
	// Metodo di verifica della email
	boolean isValidEmail() {
        return email != null && email.matches(EMAIL_PATTERN);
    }
	
	// Metodo di verifica della password 
	boolean isValidPassword() {
        if (password == null || password.length < MAX_PASS_LENGHT) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        String specialChars = "@$!%*?&";

        for (char c : password) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (specialChars.indexOf(c) >= 0) {
                hasSpecialChar = true;
            }

            if (hasUppercase && hasLowercase && hasDigit && hasSpecialChar) {
                break;
            }
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
	 }
	
	// Metodo di verifica del nickname
	boolean isValidNickname() {
	    return nickname != null && nickname.matches(NICKNAME_PATTERN);
	}
}
