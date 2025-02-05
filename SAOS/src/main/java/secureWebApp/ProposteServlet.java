package secureWebApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.tika.exception.TikaException;
import Util.AESUtils;
import Util.FileUtil;
import Util.HashUtils;
import Util.TokenUtils;
import database.LoginDao;
import database.ProposteDao;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Questa servlet gestisce la visualizzazione e il caricamento delle
 * proposte degli utenti
 */
@WebServlet("/proposte")
@MultipartConfig
public class ProposteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
	private static final int MAX_DIM_PROPOSTA = 65535;
	private TelegramBotService tg = new TelegramBotService();
    private ProposteDao proposteDao;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    	    
            char[] csrfToken = TokenUtils.getRandomString();
            request.getSession().setAttribute("csrfToken", StringEscapeUtils.escapeHtml4(new String(csrfToken)));
            request.setAttribute("csrfToken",  StringEscapeUtils.escapeHtml4(new String(csrfToken)));
            
            proposteDao = new ProposteDao();
            List<Proposte> proposalList = proposteDao.getProposte(); 

            if (proposalList.isEmpty()) {
                logger.severe("Non sono state caricate proposte!.");
            } else {
                logger.info("Proposte recuperate con successo.");
                request.setAttribute("otherProposals", proposalList);
            }
        } catch (Exception e) {
            logger.severe("Errore durante il recupero delle proposte: " + e.getMessage());
        }

        request.getRequestDispatcher("/proposte.jsp").forward(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionToken = (String) request.getSession().getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");

        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            logger.severe("Richiesta CSRF non valida.");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF Token non valido");
            return;
        }
        request.getSession().removeAttribute("csrfToken");
    	
    	Part filePart = request.getPart("proposalFile");

        int exitCode = 0;
        if (filePart == null) {
        	response.sendRedirect(request.getContextPath() + "/erroreFormatoProposta.jsp");
        	return;
        }
        else {
        	FileUtil fileUtil = new FileUtil(filePart);
        
        	if (!fileUtil.isMimeTypeSupported())
        		exitCode=1; 
        	else {
        		exitCode=2; 
        	}
        
	        switch(exitCode) {
	        	case 1: {
	        		try {
	        			response.sendRedirect(request.getContextPath() + "/erroreFormatoProposta.jsp");
	        			logger.severe(fileUtil.getMimeType()+ " tipo rilevato, non supportato" + "Metadati rilevati:\n" + fileUtil.getMetaData().toString());
	        			tg.sendMessage( " SALVATAGGIO NON AUTORIZZATO! \n" +"Utente: "+ proposteDao.getEmailByNickname((String) request.getSession().getAttribute("user"))+"\n" +"File caricato: " + fileUtil.getMimeType() + "\n" + "Metadati rilevati: " + fileUtil.getMetaData().toString());
					} catch (IOException e) {
						logger.severe("Errore: " + e.getMessage());
					}
	        		break;
	        	}
	        	case 2: {
	        		String resultMessage;
	        		try {
	        			long fileSize = filePart.getSize();
	        	        if (fileSize > MAX_DIM_PROPOSTA) { 
	        	        	response.sendRedirect(request.getContextPath() + "/erroreDimensioneProposta.jsp");
	        	            return;
	        	        }
	        	        
	        	        String fileName = FileUtil.getFileName(filePart);
	        	        String fileExtension = FileUtil.getFileExtension(fileName).toLowerCase();
	        	        fileName = StringEscapeUtils.escapeHtml4(fileName);

	        	        if (!((fileUtil.getMimeType().equals("text/plain") && (fileExtension.equals("txt"))))) {
	        	        	response.sendRedirect(request.getContextPath() + "/erroreFormatoProposta.jsp");
	        	        	return;
	        	        }
	        	         
	        	        resultMessage = fileUtil.analyzeFileContent();
	        	        System.out.println(resultMessage);
	        	        logger.severe(resultMessage);
	        	        
	        	        String username = (String) request.getSession().getAttribute("user"); 

	        	        InputStream fileContent = filePart.getInputStream();
	        	        byte[] plainText = fileContent.readAllBytes();
	        	        
	        	        byte[] salt = HashUtils.generateSalt();
	        	        byte[] hashProposta = HashUtils.hashProposta(plainText, salt);
	                    byte[] cipherText = AESUtils.encrypt(plainText, VaultManager.PATH_AES_PROPOSTE);
	                    
        				proposteDao = new ProposteDao();
        				if (proposteDao.saveProposalToDatabase(username, fileName, cipherText, hashProposta, salt) == -1) {
        					response.sendRedirect(request.getContextPath() + "/erroreCaricamentoProposta.jsp");
	        	        	return;
        				}
        				
        				Arrays.fill(plainText, (byte) 0);
        				Arrays.fill(cipherText, (byte) 0);
        				Arrays.fill(hashProposta, (byte) 0);
        				Arrays.fill(salt, (byte) 0);
        				
        				tg.sendMessage(proposteDao.getEmailByNickname(username) + " ha caricato il file " + fileName + "!");
        				
        				response.sendRedirect("proposte");
	       			
	        			break;
	        		} catch (IOException | TikaException e) {
	        				response.getWriter().println("File non salvato");
	        				logger.severe("Errore: " + e.getMessage());
	        		} catch (Exception e) {
	        			response.getWriter().println("Errore! File non salvato");
	        			logger.severe("Errore: " + e.getMessage());
	        		}
	        	} default: {
	        		response.getWriter().println("Elaborazione del file non corretta");
	        	}
	        }

		}
    }
}
