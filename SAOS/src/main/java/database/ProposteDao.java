package database;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.bettercloud.vault.VaultException;
import Util.AESUtils;
import secureWebApp.Proposte;
import secureWebApp.VaultManager;
import secureWebApp.VaultSecret;

/**
 * La classe `ProposteDao` gestisce le operazioni di accesso e manipolazione dei dati relativi 
 * alle proposte caricate dagli utenti nel database. Include funzionalità di gestione sicura 
 * delle credenziali e crittografia dei dati sensibili.
 * 
 */

public class ProposteDao {
	private static final Logger logger = Logger.getLogger(ProposteDao.class.getName());
	private final VaultManager vault;

	public ProposteDao() throws VaultException {
	    this.vault = VaultManager.getInstance();
	}
	
	// Salva una proposta nel database, verificando che l'utente non abbia superato il limite massimo di 5 proposte.
    public int saveProposalToDatabase(String nickname, String fileName, byte[] fileContent, byte[] hashProposta, byte[] salt) {
    	try {
    		VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_PROPOSTE);
    		Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
    		vaultSecret.clearSecret();

	    	PreparedStatement stmt = con.prepareStatement("SELECT COUNT(*) FROM proposte WHERE nickname = ?"); 
	    	stmt.setString(1, nickname);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next() && rs.getInt(1) >= 5) {
	        	return -1; 
	        }
            else {
        		stmt = con.prepareStatement("INSERT INTO proposte (nickname, file_name, file_data, upload_date, salt) VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, nickname);
                stmt.setString(2, fileName);
                stmt.setBytes(3, fileContent);
                stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                stmt.setBytes(5, salt);
                stmt.executeUpdate();
	                
	            stmt = con.prepareStatement("SELECT id FROM proposte WHERE nickname = ? AND file_name = ? AND file_data = ? ");
        		stmt.setString(1, nickname);
        		stmt.setString(2, fileName);
                stmt.setBytes(3, fileContent);
                ResultSet rSet = stmt.executeQuery();
	                    
                if (rSet.next()) { 
                    int id = rSet.getInt("id");
                	
                	vaultSecret = vault.getSecret(VaultManager.PATH_DB_VERIFICA_PROPOSTE);
                    con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
                    vaultSecret.clearSecret();
	                        
                    stmt = con.prepareStatement("INSERT INTO verifica_proposte (id, hash) VALUES (?, ?)");
                    stmt.setInt(1, id);
                    stmt.setBytes(2, hashProposta);
                    stmt.executeUpdate();
	    	     }
	         }
	    } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore Vault: " + e.getMessage());
		}
    	
    	return 0;
    }

    // Restituisce un elenco di tutte le proposte ordinate per data di caricamento.
    public List<Proposte> getProposte() {
        List<Proposte> proposalsList = new ArrayList<>();
       
        try {
        	VaultManager vault = VaultManager.getInstance();
	    	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_PROPOSTE);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
        
	        PreparedStatement stmt = con.prepareStatement("SELECT id, nickname, file_name, file_data, upload_date FROM proposte ORDER BY upload_date DESC");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Proposte proposta = new Proposte();
                proposta.setId(rs.getInt("id"));
                proposta.setUsername(rs.getString("nickname"));
                proposta.setFileName(rs.getString("file_name"));
                proposta.setUploadDate(rs.getTimestamp("upload_date"));
                
                byte[] encryptedData = rs.getBytes("file_data");  
                byte[] decryptedData = AESUtils.decrypt(encryptedData, VaultManager.PATH_AES_PROPOSTE);

                proposta.setFile(new ByteArrayInputStream(decryptedData));
                proposalsList.add(proposta);
                
                Arrays.fill(encryptedData, (byte) 0);
                Arrays.fill(decryptedData, (byte) 0);
            }
        } catch (SQLException | ClassNotFoundException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore Vault: " + e.getMessage());
		} catch (Exception e) {
			logger.severe("Errore: " + e.getMessage());
		}
        
        return proposalsList;
    }
    
    // Restituisce i dettagli di una proposta specifica dato il suo ID.
    public Proposte getPropostaById(int id) {
        Proposte proposta = new Proposte();
       
        try {
        	VaultManager vault = VaultManager.getInstance();
	    	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_PROPOSTE);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
        
	        PreparedStatement stmt = con.prepareStatement("SELECT nickname, file_name, file_data, upload_date FROM proposte WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                proposta.setId(id);
                proposta.setUsername(rs.getString("nickname"));
                proposta.setFileName(rs.getString("file_name"));
                proposta.setUploadDate(rs.getTimestamp("upload_date"));
                proposta.setFile(new ByteArrayInputStream(rs.getBytes("file_data")));
                return proposta;
            }
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore Vault: " + e.getMessage());
		}

        return proposta;
    }
    
    // Restituisce l'hash associato a una proposta per verificarne l'integrità.
    public byte[] getPropostaHashById(int id) {
    	try {
    		VaultManager vault = VaultManager.getInstance();
    	
	    	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_VERIFICA_PROPOSTE);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
        
	        PreparedStatement stmt = con.prepareStatement("SELECT hash FROM verifica_proposte WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBytes("hash");
            }
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore Vault: " + e.getMessage());
		}

        return null;
    }
    
    // Recupera il salt utilizzato per la generazione dell'hash di una proposta.
    public byte[] getPropostaSaltById(int id) {
    	try {
    		VaultManager vault = VaultManager.getInstance();
    	
	    	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_PROPOSTE);
	        Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
	        vaultSecret.clearSecret();
        
	        PreparedStatement stmt = con.prepareStatement("SELECT salt FROM proposte WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBytes("salt");
            }
        } catch (SQLException e) {
        	logger.severe("Errore SQL: " + e.getMessage());
        } catch (VaultException e) {
        	logger.severe("Errore Vault: " + e.getMessage());
		}
    	
        return null;
    }
    
 // Ottiene l'email di un utente dato il nickname
    public String getEmailByNickname(String nickname) {
    	if (nickname == null || nickname.isEmpty()) {
            logger.warning("Nickname non valido");
            return null;
        }
    	
        try {
        	VaultSecret vaultSecret = vault.getSecret(VaultManager.PATH_DB_USERS);
            Connection con = new DbAccess(vaultSecret.getKey(), vaultSecret.getValue()).getConn();
            vaultSecret.clearSecret();

            PreparedStatement ps = con.prepareStatement("SELECT email FROM users WHERE nickname=?");
            ps.setString(1, nickname);

            ResultSet rs = ps.executeQuery();
            boolean status = rs.next();

            if (status) {
                return rs.getString("email");
            } 
        } catch (Exception e) {
        	logger.severe("Errore generico: " + e.getMessage());
        }
        return null;
    }
}


