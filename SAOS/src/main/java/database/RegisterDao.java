package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.bettercloud.vault.VaultException;

import secureWebApp.VaultManager;
import secureWebApp.VaultSecret;

/**
 * La classe `RegisterDao` si occupa della registrazione di nuovi utenti nel sistema,
 * gestendo l'inserimento delle loro informazioni nel database e proteggendo i dati sensibili.
 * 
 */

public class RegisterDao {    
    private static final Logger logger = Logger.getLogger(RegisterDao.class.getName());
    private final VaultManager vault;
    
    public RegisterDao() throws VaultException {
		vault = VaultManager.getInstance();
	}
    
    // Registra un nuovo utente nel database, salvando il nickname, l'email, l'immagine profilo, il salt e l'hash della password. 
    public boolean registerUser(String nickname, String email, byte[] hashPass, byte[] salt, byte[] img) {
        boolean status = false;

        try {
            VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_USERS);

            Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
            vaultSecret.clearSecret();

            PreparedStatement ps = con.prepareStatement("INSERT INTO users (nickname, email, img, salt) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nickname);
            ps.setString(2, email);
            ps.setBytes(3, img);
            ps.setBytes(4, salt);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    vaultSecret = vault.getSecret(VaultManager.PATH_DB_VERIFICA_USERS);
                    con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
                    vaultSecret.clearSecret();

                    ps = con.prepareStatement("INSERT INTO verifica_users (id, hash) VALUES (?, ?)");
                    ps.setInt(1, userId);
                    ps.setBytes(2, hashPass);
                    ps.executeUpdate();
                    status = true;  
                }
            } else {
                logger.warning("Inserimento utente fallito");
            }  
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                logger.warning("Email già esistente: " + email);
            } else {
                logger.severe("Errore SQL: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.severe("Errore durante la registrazione dell'utente: " + e.getMessage());
        }
        
        return status;
    }
}