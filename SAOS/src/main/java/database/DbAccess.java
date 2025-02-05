package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;


/**
 * La classe `DbAccess` gestisce la connessione al database MySQL. 
 * Responsabilità principali:
 * 
 * 1. Configurare la connessione al database tramite i parametri come driver JDBC, 
 *    host, porta, nome del database e credenziali dell'utente.
 * 
 * 2. Verificare la validità delle credenziali fornite, assicurandosi che non siano 
 *    null o vuote.
 * 
 * 3. Inizializzare e mantenere una variabile di istanza `Connection` per interagire con il database.
 * 
 * 4. Gestire le eccezioni legate al caricamento del driver JDBC e alla connessione, 
 *    fornendo log dettagliati per aiutare nella diagnosi di errori comuni come:
 *    - Driver non trovato (`ClassNotFoundException`).
 *    - Errori di connessione (`SQLException` con SQLState specifico).
 *    - Credenziali non valide.
 * 
 * 
 * 5. Fornire accesso alla connessione tramite il metodo `getConn()` per consentire 
 *    alle altre classi di interagire con il database.
 * 
 */

public class DbAccess {
    private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final Logger logger = Logger.getLogger(LoginDao.class.getName());
    private final String DBMS = "jdbc:mysql";
    private final String SERVER = "localhost";
    private final int PORT = 3306;
    private final String DATABASE = "labSA";
    private Connection conn;

    public DbAccess(char[] user, char[] password) {
    	if (user == null || user.length == 0 || password == null || password.length == 0) {
            throw new IllegalArgumentException("Le credenziali non possono essere vuote o nulle");
        }
    	
        String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
                + "?useSSL=true&requireSSL=true&ssl-mode=REQUIRED&serverTimezone=UTC";
        try {
            Class.forName(DRIVER_CLASS_NAME);
            conn = DriverManager.getConnection(connectionString, new String(user), new String(password));
        } catch (ClassNotFoundException e) {
        	logger.severe("Driver non trovato: " + e.getMessage());
        } catch (SQLException e) {
        	if (e.getSQLState().startsWith("08")) {
        		logger.severe("Errore di connessione al server: " + e.getMessage());
            } else if (e.getSQLState().startsWith("28")) { 
            	logger.severe("Credenziali non valide: " + e.getMessage());
            } else {
            	logger.severe("Errore sconosciuto: " + e.getMessage());
            }
        } finally {
        	Arrays.fill(password, '0'); 
        	Arrays.fill(user, '0'); 
        }
    }

    public Connection getConn() {
        return conn;
    }
}