package secureWebApp;

import database.RegisterDao;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.tika.exception.TikaException;
import Util.FileUtil;
import Util.HashUtils;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Servlet di gestione della registrazione per gli utenti. Permette la registrazione
 * dell'utente tramite credenziali e foto
 */
@WebServlet("/register")
@MultipartConfig
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RegisterServlet.class.getName());
    private static final int MAX_DIM_FOTO = 16777215;
    private RegisterDao registerDao;
    private TelegramBotService tg = new TelegramBotService();
    private Credentials userCredentials;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not supported.");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	if (request.getParameter("email") == null || request.getParameter("nickname") == null 
    			|| request.getParameter("password") == null || request.getParameter("confirmPassword") == null){
    		response.sendRedirect(request.getContextPath() + "/erroreRegistrazioneCredenziali.jsp");
    		return;
    	}
    	
    	userCredentials = new Credentials();
    	userCredentials.setEmail(StringEscapeUtils.escapeHtml4(request.getParameter("email")));
    	userCredentials.setNickname(StringEscapeUtils.escapeHtml4(request.getParameter("nickname")));
    	userCredentials.setPassword(request.getParameter("password").toCharArray());
        char[] confirmPassword = request.getParameter("confirmPassword").toCharArray();
        
        Part profileImage = request.getPart("profileImage");
   
        if (!Arrays.equals(userCredentials.getPassword(),confirmPassword)) {
        	response.sendRedirect(request.getContextPath() + "/erroreRegistrazioneCredenziali.jsp");
            Arrays.fill(confirmPassword, '\0');
            userCredentials.clearPassword();
            return;
        }
        
        if (!userCredentials.isValidEmail()) {
        	response.sendRedirect(request.getContextPath() + "/erroreRegistrazioneCredenziali.jsp");
            Arrays.fill(confirmPassword, '\0');
            userCredentials.clearPassword();
            return;
        }
        
        if (!userCredentials.isValidNickname()) {
        	response.sendRedirect(request.getContextPath() + "/erroreRegistrazioneCredenziali.jsp");
            Arrays.fill(confirmPassword, '\0');
            userCredentials.clearPassword();
            return;
        }
        
        if (!userCredentials.isValidPassword()) {
        	response.sendRedirect(request.getContextPath() + "/erroreRegistrazioneCredenziali.jsp");
            Arrays.fill(confirmPassword, '\0');
            userCredentials.clearPassword();
            return;
        }
        
        int exitCode = 0;
        
        if (profileImage == null) {
        	response.sendRedirect(request.getContextPath() + "/erroreRegistrazioneCredenziali.jsp");
            Arrays.fill(confirmPassword, '\0');
            userCredentials.clearPassword();
        	return;
        }
        else {
        	FileUtil fileUtil = new FileUtil(profileImage);
        
        	if (!fileUtil.isMimeTypeSupported())
        		exitCode=1; 
        	else {
        		exitCode=2;
        	}
        
	        switch(exitCode) {
	        	case 1: {
	        		try {
		        		response.sendRedirect(request.getContextPath() + "/erroreFormatoFoto.jsp");
		        		logger.severe(fileUtil.getMimeType()+ " tipo rilevato, non supportato" + "Metadati rilevati:\n" + fileUtil.getMetaData().toString());
		        		tg.sendMessage("File caricato:" + fileUtil.getMimeType()+ " tipo rilevato, non supportato" + "Metadati rilevati: " + fileUtil.getMetaData().toString());
	        		} catch (IOException e) {
						logger.severe("Errore: " + e.getMessage());
					}
	        		break;
	        	}
	        	case 2: {
	        		String resultMessage;
	        		try {
	        			long fileSize = profileImage.getSize();
	        	        if (fileSize > MAX_DIM_FOTO) { 
	        	        	response.sendRedirect(request.getContextPath() + "/erroreDimensioneFoto.jsp");
	        	            Arrays.fill(confirmPassword, '\0');
	        	            userCredentials.clearPassword();
	        	            return;
	        	        }
	        	        
	        	        String fileName = FileUtil.getFileName(profileImage);
	        	        String fileExtension = FileUtil.getFileExtension(fileName).toLowerCase();


	        	        if (!((fileUtil.getMimeType().equals("image/jpeg") && (fileExtension.equals("jpg") || fileExtension.equals("jpeg"))) 
	        	        			|| (fileUtil.getMimeType().equals("image/png") && fileExtension.equals("png")))) {
	        	        	response.sendRedirect(request.getContextPath() + "/erroreFormatoFoto.jsp");
	        	            Arrays.fill(confirmPassword, '\0');
	        	            userCredentials.clearPassword();
	        	        	return;
	        	        }        
	        	        
	        	        resultMessage = fileUtil.analyzeFileContent();
	        	        logger.severe(resultMessage);
	        	        
	        	        byte[] img = Util.FileUtil.convertImageToBinary(profileImage);
	        	        
	        			registerDao = new RegisterDao();
						
						byte[] salt = HashUtils.generateSalt();
						byte[] hashedPassword = HashUtils.hashPassword(userCredentials.getPassword(), salt);
						    
						boolean registrationSuccess = registerDao.registerUser(userCredentials.getNickname(), userCredentials.getEmail(), hashedPassword, salt, img);
						
						Arrays.fill(hashedPassword, (byte) 0);
						Arrays.fill(salt, (byte) 0);
						userCredentials.clearPassword();
						Arrays.fill(confirmPassword, '\0');
						
						if (registrationSuccess) {
						    logger.info("User registered successfully!");
						    tg.sendMessage(userCredentials.getEmail() + " ha effettuato la registrazione!");
						    response.sendRedirect("registrazioneCompletata.jsp"); 
						} else {
						    logger.severe("User registration failed.");
						    response.sendRedirect(request.getContextPath() + "/erroreRegistrazione.jsp");
						}	        			
	    
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