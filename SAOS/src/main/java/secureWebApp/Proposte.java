package secureWebApp;

import java.io.InputStream;
import java.sql.Timestamp;

/**
 * Classe che rappresenta una proposta.
 * Contiene le informazioni relative alla proposta come l'ID, il nome dell'utente che ha caricato la proposta, 
 * il nome del file, il file stesso (come InputStream) e la data di caricamento.
 */
public class Proposte {
    private int id;
    private String username;
    private String fileName;
    private InputStream file;
    private Timestamp uploadDate;
    
    // Metodo get id della proposta
    public int getId() {
        return id;
    }
    
    // Metodo set id della proposta
    public void setId(int id) {
        this.id = id;
    }
    
    // Metodo get dell'username della proposta
    public String getUsername() {
        return username;
    }
    
    // Metodo set dell'username della proposta
    public void setUsername(String username) {
        this.username = username;
    }
    
    // Metodo get del file
    public InputStream getFile() {
        return file;
    }
    
    // Metodo set del file
    public void setFile(InputStream file) {
        this.file = file;
    }
    
    // Metodo get per il file name
    public String getFileName() {
        return fileName;
    }
    
    // Metodo set per il file name
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    // Metodo get per la data di upload
    public Timestamp getUploadDate() {
        return uploadDate;
    }
    
    // Metodo set per la data di upload
    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }
}