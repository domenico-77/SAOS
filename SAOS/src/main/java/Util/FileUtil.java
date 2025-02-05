package Util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

/**
 * La classe FileUtil gestisce le operazioni relative ai file caricati,
 * come la verifica del tipo MIME supportato, l'analisi del contenuto del file 
 * per rilevare potenziali minacce di sicurezza (es. codice malevolo come script o funzioni JavaScript pericolose),
 * l'estrazione di metadati e la conversione di immagini in formati binari e viceversa.
 * Utilizza la libreria Apache Tika per analizzare il contenuto del file, 
 * identificare il tipo MIME e per rilevare contenuti pericolosi all'interno del file stesso.
 */
public class FileUtil {
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	private static final String[] ALLOWED_TYPES = {"text/plain",
			"image/jpeg", "image/png", "application/pdf"};
	private Tika tika; 
    private Part filePart;
    
    public FileUtil (Part filePart) {
    	tika = new Tika();
    	this.filePart = filePart;
    }
    
    // Metodo per controllare se il MIME è supportato
    public boolean isMimeTypeSupported() throws IOException {
		boolean isAllowedType = false;
		String mimeType = tika.detect(this.filePart.getInputStream());
		logger.info("Tipo MIME rilevato: " + mimeType);
		
		for (String allowedType : ALLOWED_TYPES) {
			if (allowedType.equals(mimeType)) {
				isAllowedType = true;
				break;
			}
		}
		return isAllowedType;
	}
    
    // Metodo per l'analisi del contenuto del file
    public String analyzeFileContent() throws IOException, TikaException {
		String content = tika.parseToString(filePart.getInputStream());
		StringBuilder report = new StringBuilder();
		boolean malicious = false;
		
		int scriptIndex = content.indexOf("<script>");
		if (scriptIndex != -1) {
			malicious = true;
			String scriptContext = extractContext(content, scriptIndex);
			report.append("Errore: file contenente codice malevolo non accettato! ")
			.append("Il file contiene il tag <script> nel contesto: ")
			.append(scriptContext).append("\n");
		}
		/*
		* Controllo per l'uso di eval() funzione JavaScript usata per eseguire
		* codice a partire da una stringa
		*/
		int evalIndex = content.indexOf("eval(");
		if (evalIndex != -1) {
				malicious = true;
				String evalContext = extractContext(content, evalIndex);
				report.append("Errore: file contenente codice malevolo non accettato! ")
				.append("Il file contiene l'uso di eval() nel contesto: ")
				.append(evalContext).append("\n");
		}
		// Controllo per l'attributo onerror
		/**
		* onerror è un evento JavaScript utilizzato in HTML che permette di eseguire
		* codice JavaScript quando un errore di caricamento si verifica su un elemento
		*/
		int onerrorIndex = content.indexOf("onerror");
		if (onerrorIndex != -1) {
			malicious = true;
			String onerrorContext = extractContext(content, onerrorIndex);
			report.append("Errore: file contenente codice malevolo non accettato! ")
			.append("Il file contiene l'attributo onerror nel contesto: ")
			.append(onerrorContext).append("\n");
		}
		
		return malicious ? report.toString() : "File privo di codice malevolo.";
	}
    
    private String extractContext(String content, int index) {
		// Estrae 30 caratteri prima e dopo l'elemento sospetto per dare un contesto
		int start = Math.max(index - 30, 0);
		int end = Math.min(index + 30, content.length());
		return content.substring(start, end).replace("\n", " ");
	}
    
    // Metodo che restituisce il tipo di MIME
    public String getMimeType() throws IOException {
		return tika.detect(this.filePart.getInputStream());
	}
	
    // Metodo che restituisce i metadata
	public List<String> getMetaData() throws IOException {
		List<String> metaDataList = new ArrayList<String>();
		Metadata metadata = new Metadata();
		
		tika.parse(filePart.getInputStream(), metadata);
		
		for (String name : metadata.names()) {
			System.out.println(name + ": " + metadata.get(name));
			metaDataList.add(metadata.get(name));
		}
		return metaDataList;
	}
	
	// Metodo che restituisce il nome del file 
    public static String getFileName(Part part) {
        for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
            if (contentDisposition.trim().startsWith("filename")) {
                return contentDisposition.substring(contentDisposition.indexOf('=') + 2, contentDisposition.length() - 1);
            }
        }
        return null;
    }
    
    // Metodo che restituisce l'estensione del file
    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return ""; // nessuna estensione
        }
        return fileName.substring(index + 1);
    }
    
    // Metodo che converte l'immagine in binario
    public static byte[] convertImageToBinary(Part imagePart) throws IOException {
        try (InputStream inputStream = imagePart.getInputStream()) {
            return inputStream.readAllBytes(); 
        }
    }
    
    // Metodo che converte un immagine da binario in immagine
    public static BufferedImage convertBinaryToImage(byte[] imageBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        return bufferedImage;
    }
}
