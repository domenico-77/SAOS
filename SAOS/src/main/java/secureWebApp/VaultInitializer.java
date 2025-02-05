package secureWebApp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.bettercloud.vault.VaultException;

import java.io.Console;

/**
 * La classe VaultInitializer è responsabile dell'inizializzazione della connessione a Vault al momento
 * dell'avvio del contesto servlet. Gestisce l'autenticazione dell'utente tramite token per interagire
 * con Vault, con un massimo di TENTATIVI_LOGIN per inserire correttamente il token.
 */
@WebListener
public class VaultInitializer implements ServletContextListener {
	public final static int TENTATIVI_LOGIN = 3;
	private TelegramBotService tg = new TelegramBotService();
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	Console console = System.console();
        if (console == null) {
            throw new RuntimeException("Console non disponibile");
        }
        
        tg.sendMessage("Server Avviato!"); 
        
        System.out.println("Inserisci il token di Vault");
        VaultManager vaultManager;
        boolean auth = false;
        for (int i = 0; i < TENTATIVI_LOGIN; i++) {
        	System.out.println("Tentativi rimanenti: " + (TENTATIVI_LOGIN - i));
	        try {
	        	vaultManager = VaultManager.getInstance();
	            auth = vaultManager.auth(console.readPassword("Token: "));
	            
	            if(auth == true) {
	            	  tg.sendMessage("Accesso al Vault effettuato!"); 
	            	return;
	            }
	        } catch (VaultException e) {
	            e.printStackTrace();
	            System.err.println("Token di Vault non corretto o errore di connessione");
	            tg.sendMessage("Token Vault non corretto!"); 
	        }
        }
        System.out.println("Il server verrà stoppato!");
        tg.sendMessage("3 TENTATIVI DI ACCESSO AL VAULT FALLITI \n" + "Arresto server..."); 
        System.exit(1);
    }
}