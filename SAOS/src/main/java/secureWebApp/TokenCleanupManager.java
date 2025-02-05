package secureWebApp;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import com.bettercloud.vault.VaultException;
import database.RememberMeTokenDao;

/**
 * La classe TokenCleanupManager si occupa della gestione della pulizia periodica dei token "Remember Me" scaduti.
 */
@WebListener
public class TokenCleanupManager implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(RegisterServlet.class.getName());
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private RememberMeTokenDao tokenDao;


    @Override
    public void contextInitialized(ServletContextEvent sce) {
		try {
			tokenDao = new RememberMeTokenDao();
			start(); 
			System.out.println("Token cleanup task started.");
		} catch (VaultException e) {
			logger.severe("Errore Vault: " + e.getMessage());
		}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    	stop();
        System.out.println("Token cleanup task stopped.");
    }
    
    private void start() {
        scheduler.scheduleAtFixedRate(() -> {
                tokenDao.deleteExpiredTokens();
        }, 0, 5, TimeUnit.MINUTES); 
    }

    private void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
        	logger.severe("Errore Vault: " + e.getMessage());
            scheduler.shutdownNow();
        }
    }
}