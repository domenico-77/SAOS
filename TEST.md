# TEST D'USO
Questa sezione “Test d’uso” si occupa di verificare l’applicazione dal punto di vista dell’utilizzo pratico, ovvero simulando e analizzando scenari realistici di interazione da parte degli utenti finali.  
### Risultato test di registrazione andata a buon fine:
Dopo aver compilato i vari campi richiesti, una volta che l’utente cliccherà su registrati, l’utente verrà reindirizzato sulla seguente pagina:  
![image](https://github.com/user-attachments/assets/f5923627-55f9-4063-b3c7-85c1eda388af)  
### Test di registrazione con file non valido .gif:
![image](https://github.com/user-attachments/assets/6246f8fd-21e1-46dd-9320-308abcec399f) 
Il modulo di registrazione permette di selezionare solamente i file con estensione .jpg, .jpeg o .png, grazie all’attributo accept presente nell’elemento <input>. Questo filtro aiuta a limitare i tipi di file visibili nella finestra di selezione, migliorando l’esperienza utente e riducendo il rischio di caricamenti errati.   
![image](https://github.com/user-attachments/assets/d6bb2a98-4f9b-4e5b-8994-6f61d79544ac)  
### Risultato test di registrazione con email o nickname già utilizzati:
Se l’utente tenta di registrarsi utilizzando un’email o un nickname già associati a un altro account, verrà reindirizzato alla seguente pagina:  
![image](https://github.com/user-attachments/assets/c983f579-405c-47c8-a4d2-3ae9cbcfd1ba)  
### Risultato test di login andato a buon fine:
Una volta che l’utente avrà inserito le credenziali utilizzate per registrarsi e cliccherà su login, l’utente verrà reindirizzato sulla seguente pagina:  
![image](https://github.com/user-attachments/assets/9e8d4fcd-aa0f-4b33-99f3-882ddcb5309f)  
### Risultato test di login con credenziali errate:
Una volta che l’utente clicchera su login, con le credenziali errate, l’utente verrà reindirizzato sulla seguente pagina:  
![image](https://github.com/user-attachments/assets/2299aed6-6899-424f-916c-a6b8993138ad)  
### Test di caricamento proposta progettuale con estensione .txt:
L’utente una volta loggato potrà caricare la proposta in formato txt, una volta che l’utente cliccherà su carica, il file verrà caricato e verrà visualizzata la proposta nella sezione apposita:  
![image](https://github.com/user-attachments/assets/cf704d4f-cfe0-4825-95bf-71d91a26ffa8)  
### Test di visualizzazione del contenuto della proposta:
L’utente per poter visualizzare il contenuto della proposta dovrà premere sul tasto Visualizza  
![image](https://github.com/user-attachments/assets/f6da77d1-eab1-4343-8b4a-7ad19682d952)
### Test di caricamento proposta progettuale con estensione .pdf:
![image](https://github.com/user-attachments/assets/11425abf-e33b-48ce-beb0-ea2eb9670109)  
Il modulo permette di selezionare solamente i file con estensione .txt, grazie all’attributo accept presente nell’elemento <input>. Questo filtro aiuta a limitare i tipi di file visibili nella finestra di selezione, migliorando l’esperienza utente e riducendo il rischio di caricamenti errati.  
![image](https://github.com/user-attachments/assets/4bc5afcc-7a94-4a68-8ec6-dfeb2a923163)  
### Test di download di una proposta progettuale
Una volta effettuato l’accesso, l’utente potrà visualizzare tutte le proposte caricate dagli altri utenti e avrà la possibilità di scaricarle per consultarne il contenuto.  
![image](https://github.com/user-attachments/assets/282b9922-76a5-41aa-98c0-71df38a41a74)  
L’utente Domenico12 per scaricare la proposta di RobertoCapo07 dovrà cliccare sul pulsante “Scarica” accanto alla proposta desiderata:  
![image](https://github.com/user-attachments/assets/c81dfd7f-2bb4-4f37-8ee1-d24cfa92b4e5)  
### Test sessione scaduta (senza aver creato un rememberMe cookie):
Aspettiamo che la sessione scada:  
![image](https://github.com/user-attachments/assets/91efeb20-2e4e-4f99-9505-4683e6ef07c9)  
Una volta che la sessione è scaduta, se l’utente prova a effettuare qualsiasi operazione, verrà reindirizzato alla seguente pagina:  
![image](https://github.com/user-attachments/assets/667ef19e-8d1a-4131-b966-9dd7480709e2)  
### Test sessione scaduta (creato un rememberMe cookie):
Aspettiamo che la sessione scada:  
![image](https://github.com/user-attachments/assets/96dff66c-bcc9-4197-9be2-2dbaddb4c8a4)  
Una volta che la sessione scade, se è presente il token “Remember Me”, la sessione viene automaticamente ricreata.  
![image](https://github.com/user-attachments/assets/23818b26-4f44-4ea9-a9e5-05eeb9710cdf)













