package secureWebApp;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import Util.AESUtils;
import database.ProposteDao;
import database.RememberMeTokenDao;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * La classe `AuthenticationFilter` è un filtro servlet che gestisce l'autenticazione degli utenti tramite sessione
 * o cookie "Remember Me".
 */
@WebFilter(urlPatterns = {"/proposte", "/proposte.jsp", "/downloadProposal", "/viewProposal", "/visualizzaProposta.jsp"})
public class AuthenticationFilter implements Filter {
	private static final Logger logger = Logger.getLogger(ProposteDao.class.getName());
	private static final String COOKIE_NAME = "rememberme";
	private static final int SESSION_TIMEOUT = 60 * 15;
	private RememberMeTokenDao tokenDao;
	private Credentials userCredentials;

	public void doFilter(ServletRequest requestServlet, ServletResponse responseServlet, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) requestServlet;
        HttpServletResponse response = (HttpServletResponse) responseServlet;


        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            chain.doFilter(request, response);
            return;
        }
        else {
        	try {
        		tokenDao = new RememberMeTokenDao();
            	userCredentials = new Credentials();
				if (validateRememberMeCookie(request.getCookies(), response)) {
					session = request.getSession(true);
					request.changeSessionId();
				    session.setMaxInactiveInterval(SESSION_TIMEOUT);
					session.setAttribute("user", userCredentials.getNickname());
				    chain.doFilter(request, response);
				}
				else {
				    response.sendRedirect("sessionExpired.jsp");
				}
			} catch (Exception e) {
				logger.severe("Errore: " + e.getMessage());
			}
        }
    }

	/* Controlla se il cookie "Remember Me" è presente, decodifica il valore del cookie e verifica la corrispondenza 
	 * con il valore crittografato memorizzato nel database. Restituisce `true` se i valori decifrati corrispondono, altrimenti `false`.
	 */
    private boolean validateRememberMeCookie(Cookie[] cookies, HttpServletResponse response) throws Exception {
        if (cookies == null) return false;

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
            	userCredentials.setNickname(tokenDao.findNicknameByToken(Base64.getDecoder().decode(cookie.getValue())));
            	
                byte[] encryptedValue = Base64.getDecoder().decode(cookie.getValue());
                byte[] decryptedValue = AESUtils.decrypt(encryptedValue, VaultManager.PATH_AES_REMEMBER_ME);
                
                byte[] encryptedStoredValue = tokenDao.findTokenByNickname(userCredentials.getNickname());

                if (encryptedStoredValue == null) {
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