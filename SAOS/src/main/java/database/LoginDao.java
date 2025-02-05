package database;

import java.sql.*;
import java.util.Arrays;
import java.util.logging.Logger;

import com.bettercloud.vault.VaultException;

import Util.HashUtils;
import secureWebApp.VaultManager;
import secureWebApp.VaultSecret;

/**
 * La classe `LoginDao` gestisce le operazioni relative al login e all'accesso 
 * ai dati utente nel database. Utilizza un approccio sicuro per l'accesso ai dati, 
 * integrando Vault per la gestione delle credenziali.
 * 
 */

public class LoginDao {
	private static final Logger logger = Logger.getLogger(LoginDao.class.getName());
	private final VaultManager vault;
	
	public LoginDao() throws VaultException {
		vault = VaultManager.getInstance();
	}
	
	// Verifica la validità delle credenziali inserite di un utente confrontandole con i dati salvati
    public boolean isUserValid(String email, char[] pass) {
    	if (email == null || email.isEmpty() || pass == null || pass.length == 0) {
            logger.warning("Email o password non validi");
            return false;
        }
    	
        boolean isValid = false;
        try {     	
        	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_USERS);
            Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
            vaultSecret.clearSecret();
            
            PreparedStatement ps = con.prepareStatement("SELECT id, salt FROM users WHERE email=?");
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            boolean status = rs.next();

            if (status) {
	        	int id = rs.getInt("id");
	        	byte[] storedSalt = rs.getBytes("salt");
	        	
	        	vaultSecret = vault.getSecret(VaultManager.PATH_DB_VERIFICA_USERS);
	            con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	            vaultSecret.clearSecret();
	            
	        	ps = con.prepareStatement("SELECT hash FROM verifica_users WHERE id = ?");
	        	ps.setInt(1, id);
            	rs = ps.executeQuery();
                status = rs.next();
                if (status) {
	            	byte[] storedHash = rs.getBytes("hash");
	                 
	                isValid = HashUtils.validatePassword(pass, storedHash, storedSalt);
	                 
	                java.util.Arrays.fill(pass, '0'); 
	                Arrays.fill(storedHash, (byte) 0);
	                Arrays.fill(storedSalt, (byte) 0);
	     
	                return isValid;
                }
            } 
        } catch (Exception e) {
        	logger.severe("Errore: " + e.getMessage());
        }
        return isValid;
    }
    
    // Ottiene il nickname di un utente dato l'email
    public String getNicknameByEmail(String email) {
    	if (email == null || email.isEmpty()) {
            logger.warning("Email non valida");
            return null;
        }
    	
        try {
        	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_USERS);
            Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
            vaultSecret.clearSecret();

            PreparedStatement ps = con.prepareStatement("SELECT nickname FROM users WHERE email=?");
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            boolean status = rs.next();

            if (status) {
                return rs.getString("nickname");
            } 
        } catch (Exception e) {
        	logger.severe("Errore generico: " + e.getMessage());
        }
        return null;
    }
}