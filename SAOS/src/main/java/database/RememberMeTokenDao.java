package database;
import java.sql.*;
import java.util.logging.Logger;

import com.bettercloud.vault.VaultException;

import secureWebApp.VaultManager;
import secureWebApp.VaultSecret;

/**
 * La classe `RememberMeTokenDao` gestisce la logica per i token "Remember Me" utilizzati per
 * l'autenticazione persistente degli utenti nel sistema.
 */
public class RememberMeTokenDao {
	private static final Logger logger = Logger.getLogger(ProposteDao.class.getName());
    private static final int SECONDS_IN_A_DAY = 60 * 60 * 24;
    private final VaultManager vault;
    
    public RememberMeTokenDao() throws VaultException {
		vault = VaultManager.getInstance();
	}
    
    // Salva o aggiorna il token "Remember Me" per un utente specifico, includendo una data di scadenza.
    public void save(String nickname, byte[] rememberMe, int COOKIE_MAX_AGE) {
    	int daysToExpire = COOKIE_MAX_AGE / (SECONDS_IN_A_DAY);
    	
    	try {
    		VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_REMEMBER_ME);
    	
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
       
        	PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM remember_me_tokens WHERE nickname = ?");
        	ps.setString(1, nickname);
        	 
            ResultSet rs = ps.executeQuery();
            rs.next();
             
            int count = rs.getInt(1);
            if (count > 0) {
            	 ps = con.prepareStatement("DELETE FROM remember_me_tokens WHERE nickname = ?");
                 ps.setString(1, nickname);
                 ps.executeUpdate();
            }
     
            ps = con.prepareStatement("INSERT INTO remember_me_tokens (nickname, token, expiry_date) VALUES (?, ?, ?)");
            ps.setString(1, nickname);
            ps.setBytes(2, rememberMe);
            ps.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now().plusDays(daysToExpire)));
            ps.executeUpdate();
    	} catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore vault: " + e.getMessage());
		}
    }
    
    // Recupera il token associato a un nickname.
    public byte[] findTokenByNickname(String nickname) {
    	try {
    		VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_REMEMBER_ME);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
	        
	        PreparedStatement ps = con.prepareStatement("SELECT token FROM remember_me_tokens WHERE nickname = ?");
            ps.setString(1, nickname);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getBytes("token"); 
            }
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore vault: " + e.getMessage());
		}
        
        return null;
    }
    
    // Recupera il nickname associato a un token specifico.
    public String findNicknameByToken(byte[] token) {
    	try {
    		VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_REMEMBER_ME);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();

	        PreparedStatement ps = con.prepareStatement("SELECT nickname FROM remember_me_tokens WHERE token = ?");
            ps.setBytes(1, token);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore vault: " + e.getMessage());
		}
        
        return null;
    }
    
    // Verifica se un token specifico è valido (esiste nel database).
    public boolean isTokenValid(String token) {
    	try {
	    	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_REMEMBER_ME);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
            
	        PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM remember_me_tokens WHERE token = ?");
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;  
            }
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore vault: " + e.getMessage());
		}
        
        return false;
    }
    
    // Recupera la data di scadenza di un token specifico.
    public Timestamp getTokenTimeStamp(String nickname) {
    	try {
	    	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_REMEMBER_ME);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
            
	        PreparedStatement ps = con.prepareStatement("SELECT expiry_date FROM remember_me_tokens WHERE nickname = ?");
            ps.setString(1, nickname);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
            	return rs.getTimestamp("expiry_date");
            }
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore vault: " + e.getMessage());
		}
        
        return null;
    }
   
    // Elimina i token scaduti.
    public void deleteExpiredTokens() {
    	try {
	    	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_REMEMBER_ME);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
	        
	        PreparedStatement stmt = con.prepareStatement("DELETE FROM remember_me_tokens WHERE expiry_date <= NOW()"); 
	        stmt.executeUpdate();
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore vault: " + e.getMessage());
		}
    	
    }
    
    // Elimina il token associato a un nickname specifico.
    public void deleteTokenByNickname(String nickname) {      
        try {
        	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_REMEMBER_ME);
            Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
            vaultSecret.clearSecret();
        
        	PreparedStatement stmt = con.prepareStatement("DELETE FROM remember_me_tokens WHERE nickname = ?"); 
            stmt.setString(1, nickname);
            stmt.executeUpdate();
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore vault: " + e.getMessage());
		}
    }
}