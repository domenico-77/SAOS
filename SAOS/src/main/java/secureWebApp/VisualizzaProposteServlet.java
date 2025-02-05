package secureWebApp;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import Util.AESUtils;
import database.ProposteDao;

/**
 * La classe VisualizzaProposteServlet gestisce le richieste HTTP per visualizzare una proposta cifrata.
 * Quando una richiesta GET contiene un ID di una proposta, la servlet recupera il file associato alla proposta, 
 * lo decritta e lo visualizza in formato leggibile dall'utente.
 */
@WebServlet("/viewProposal")
public class VisualizzaProposteServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(VisualizzaProposteServlet.class.getName());
    private static final long serialVersionUID = 1L;
    private ProposteDao proposteDao;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("id") != null) {
            try {
                proposteDao = new ProposteDao();
                int id = Integer.parseInt(request.getParameter("id"));
                Proposte proposta = proposteDao.getPropostaById(id);

                if (proposta != null && proposta.getFile() != null) {
                    byte[] encryptedFile = proposta.getFile().readAllBytes();
                    byte[] decryptedFile = AESUtils.decrypt(encryptedFile, VaultManager.PATH_AES_PROPOSTE);

                    byte[] storedHash = proposteDao.getPropostaHashById(id);
                    byte[] salt = proposteDao.getPropostaSaltById(id);

                    if (!Util.HashUtils.validateProposta(decryptedFile, storedHash, salt)) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Proposta non disponibile.");
                        return;
                    }

                    request.setAttribute("content", StringEscapeUtils.escapeHtml4(new String(decryptedFile, "UTF-8")));
                    request.setAttribute("proposalName", proposta.getFileName()); 
                    
                    Arrays.fill(encryptedFile, (byte) 0);
                    Arrays.fill(decryptedFile, (byte) 0);
                    Arrays.fill(storedHash, (byte) 0);
                    Arrays.fill(salt, (byte) 0);

                    request.getRequestDispatcher("/visualizzaProposta.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Proposta non trovata o file non disponibile.");
                }
            } catch (Exception e) {
                logger.severe("Errore: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore nel recupero della proposta.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID proposta non fornito.");
        }
    }
}