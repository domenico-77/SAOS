package secureWebApp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.commons.text.StringEscapeUtils;

import Util.AESUtils;
import Util.TokenUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

import database.LoginDao;
import database.ProposteDao;
import database.RememberMeTokenDao;

/**
 * Servlet di gestione del login per l'autenticazione degli utenti.
 * Supporta l'autenticazione tramite email e password, oltre alla funzionalità di "ricordami" tramite un cookie sicuro.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ProposteDao.class.getName());
    private static final int SESSION_TIMEOUT = 60 * 15; 
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7;
    private static final String COOKIE_NAME = "rememberme";
    private TelegramBotService tg = new TelegramBotService();
    private Credentials userCredentials;
    private LoginDao loginDao;
    private RememberMeTokenDao tokenDao;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not supported.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
    	
    	if (session != null && session.getAttribute("user") != null) {
	         response.sendRedirect(request.getContextPath() + "/sessioneAttiva.jsp");
	         return;
        }
    	
    	if (request.getParameter("email") == null || request.getParameter("pass") == null) {
    		response.sendRedirect(request.getContextPath() + "/errore.jsp");
    		return;
    	}
    	
    	userCredentials = new Credentials();
    	userCredentials.setEmail(StringEscapeUtils.escapeHtml4(request.getParameter("email")));
    	userCredentials.setPassword(request.getParameter("pass").toCharArray());
    	
        if (!userCredentials.isValidEmail()) {
        	response.sendRedirect(request.getContextPath() + "/errore.jsp");
            return;
        }

        if (!userCredentials.isValidPassword()) {
        	response.sendRedirect(request.getContextPath() + "/errore.jsp");
            return;
        }

        boolean rememberMe = "true".equals(request.getParameter("rememberme"));
        
   	  
        try {
            loginDao = new LoginDao();
            userCredentials.setNickname(loginDao.getNicknameByEmail(userCredentials.getEmail()));
            boolean validated = authenticateUser(request, response, rememberMe);
            if (validated) {
            	tg.sendMessage(userCredentials.getEmail() + " ha effettuato l'accesso!"); 
            	request.changeSessionId();
                session = request.getSession(true);
                session.setMaxInactiveInterval(SESSION_TIMEOUT);
                session.setAttribute("user", userCredentials.getNickname());
                response.sendRedirect(request.getContextPath() + "/proposte");
            } else {
            	response.sendRedirect(request.getContextPath() + "/errore.jsp");
            }
        } catch (Exception e) {
        	logger.severe("Errore: " + e.getMessage());
        	response.sendRedirect(request.getContextPath() + "/errore.jsp");
        } finally {
            userCredentials.clearPassword();
        }
    }
    
    // Verifica se l'utente è autenticato
    private boolean authenticateUser(HttpServletRequest request, HttpServletResponse response, boolean rememberMe)
            throws Exception {
        boolean isAuthenticated = false;

        if (rememberMe) {
            Cookie[] cookies = request.getCookies();
            tokenDao = new RememberMeTokenDao();
            boolean isValid = validateRememberMeCookie(cookies, response);
            if (!isValid) {
            	isAuthenticated = loginDao.isUserValid(userCredentials.getEmail(), userCredentials.getPassword());

                if (isAuthenticated) {
                    char[] randomString = TokenUtils.getRandomString();
                    char[] emailChars = userCredentials.getEmail().toCharArray();
                    char[] separator = {':'};
                    char[] combinedValueArray = new char[emailChars.length + separator.length + randomString.length];
                    
                    System.arraycopy(emailChars, 0, combinedValueArray, 0, emailChars.length);
                    System.arraycopy(separator, 0, combinedValueArray, emailChars.length, separator.length);
                    System.arraycopy(randomString, 0, combinedValueArray, emailChars.length + separator.length, randomString.length);
                    
                    Arrays.fill(randomString, '\0');
                    Arrays.fill(separator, '\0');
                    Arrays.fill(emailChars, '\0');

                    byte[] combinedValueBytes = new byte[combinedValueArray.length];
                    for (int i = 0; i < combinedValueArray.length; i++) {
                        combinedValueBytes[i] = (byte) combinedValueArray[i]; 
                    }
                    
                    byte[] encryptedValue = AESUtils.encrypt(combinedValueBytes, VaultManager.PATH_AES_REMEMBER_ME);
                    
                    Cookie loginCookie = new Cookie(COOKIE_NAME, Base64.getEncoder().encodeToString(encryptedValue));
                    loginCookie.setMaxAge(COOKIE_MAX_AGE);
                    loginCookie.setPath("/");
                    loginCookie.setHttpOnly(true);
                    loginCookie.setSecure(true);
                    response.addCookie(loginCookie);
                    
                    tokenDao.save(userCredentials.getNickname(), encryptedValue, COOKIE_MAX_AGE);
                    
                    Arrays.fill(combinedValueBytes, (byte) 0);
                    Arrays.fill(encryptedValue, (byte) 0);
                }
            }
            else {
            	isAuthenticated = true;
            }
        } else {
        	isAuthenticated = loginDao.isUserValid(userCredentials.getEmail(), userCredentials.getPassword());
        }

        return isAuthenticated;
    }
    
    /* Controlla se il cookie "Remember Me" è presente, decodifica il valore del cookie e verifica la corrispondenza 
	 * con il valore crittografato memorizzato nel database. Restituisce `true` se i valori decifrati corrispondono, altrimenti `false`.
	 */
    private boolean validateRememberMeCookie(Cookie[] cookies, HttpServletResponse response) throws Exception {
        if (cookies == null) return false;
        
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {              
                byte[] encryptedValue = Base64.getDecoder().decode(cookie.getValue());
                byte[] decryptedValue = AESUtils.decrypt(encryptedValue, VaultManager.PATH_AES_REMEMBER_ME);
                
                byte[] encryptedStoredValue = tokenDao.findTokenByNickname(userCredentials.getNickname());
                
                if(encryptedStoredValue == null) {
                	return false;
                }
                
                byte[] decryptedStoredValue = AESUtils.decrypt(encryptedStoredValue, VaultManager.PATH_AES_REMEMBER_ME);
            
                if (Arrays.equals(decryptedValue, decryptedStoredValue)) {       
                    return true;
               }
                
                Arrays.fill(encryptedValue, (byte) 0);
                Arrays.fill(decryptedValue, (byte) 0);
                Arrays.fill(encryptedStoredValue, (byte) 0);
                Arrays.fill(decryptedStoredValue, (byte) 0);
            }
        }

        return false;
    } 
}