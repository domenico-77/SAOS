package secureWebApp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Util.AESUtils;
import database.LoginDao;
import database.ProposteDao;

/**
 * Questa servlet gestisce il download di una proposta tramite il suo ID.
 * Il flusso di lavoro include:
 * - Recupero dell'ID dalla richiesta.
 * - Recupero della proposta dal database.
 * - Decrittazione del file associato alla proposta.
 * - Verifica dell'integrità del file tramite hash.
 * - Inviare il file decrittato come allegato al client.
 * Se il file non è disponibile o non è stato trovato, viene restituito un errore.
 * L'integrità dei file viene validata utilizzando un hash e un sale specifico per ciascuna proposta.
 */
@WebServlet("/downloadProposal")
public class DownloadProposteServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(LoginDao.class.getName());
    private static final long serialVersionUID = 1L;
    private ProposteDao proposteDao;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        if (request.getParameter("id") != null) {
			try {
				proposteDao = new ProposteDao();
				int id = Integer.parseInt(request.getParameter("id"));
				Proposte proposta = proposteDao.getPropostaById(id);
				
				if (proposta != null && proposta.getFile() != null) {
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + proposta.getFileName() + "\"");

                    byte[] encryptedFile = proposta.getFile().readAllBytes();
                    byte[] decryptedFile = AESUtils.decrypt(encryptedFile, VaultManager.PATH_AES_PROPOSTE);
                    
                    byte[] storedHash = proposteDao.getPropostaHashById(id);
                    byte[] salt = proposteDao.getPropostaSaltById(id);
                    
                    if(!Util.HashUtils.validateProposta(decryptedFile, storedHash, salt)) {
                    	response.sendError(HttpServletResponse.SC_NOT_FOUND, "Proposta non disponibile");
                    	return;
                    }
                    
                    OutputStream outputStream = response.getOutputStream();
                    outputStream.write(decryptedFile);

                    outputStream.flush();
                    outputStream.close();
                   
                    Arrays.fill(encryptedFile, (byte) 0);
                    Arrays.fill(decryptedFile, (byte) 0);
                    Arrays.fill(storedHash, (byte) 0);
                    Arrays.fill(salt, (byte) 0);
				} else {
				    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Proposta non trovata o file non disponibile.");
				}
			} catch (Exception e) {
				logger.severe("Errore: " + e.getMessage());
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Proposta non trovata o file non disponibile.");
			} 
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID proposta non fornito.");
        }
    }
}