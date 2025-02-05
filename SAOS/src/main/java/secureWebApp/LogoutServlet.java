package secureWebApp;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.bettercloud.vault.VaultException;

import database.ProposteDao;
import database.RememberMeTokenDao;

/**
 * Servlet di gestione del logout degli utenti.
 * Questa servlet gestisce la terminazione della sessione e la rimozione del cookie "remember me".
 * - Se presente, cancella il token "remember me" dal database.
 * - Rimuove l'attributo della sessione "user" e invalida la sessione.
 * - Reindirizza l'utente alla pagina di logout.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ProposteDao.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Il metodo GET non è supportato per il logout.");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String nickname = null;
       

        Cookie rememberMeCookie = new Cookie("rememberme", null);
        rememberMeCookie.setMaxAge(0); 
        rememberMeCookie.setPath("/"); 
        rememberMeCookie.setHttpOnly(true);
        rememberMeCookie.setSecure(true);
        response.addCookie(rememberMeCookie);
        
        if (session != null) {
            nickname = (String) session.getAttribute("user");
            session.removeAttribute("user");
            session.invalidate(); 
        }
        
        if (nickname != null) {
            try {
                RememberMeTokenDao tokenDao = new RememberMeTokenDao();
                tokenDao.deleteTokenByNickname(nickname); 
            } catch (VaultException e) {
            	logger.severe("Errore: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante la cancellazione del token.");
                return;
            }
        }
        
        response.sendRedirect("logout.jsp");
    }
}