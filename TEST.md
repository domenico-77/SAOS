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
### Test scadenza cookie remember me:
Quando il cookie scade, il server rimuove automaticamente il token dalla tabella remember_me_tokens. L’utente verrà quindi reindirizzato sulla seguente pagina:  
![image](https://github.com/user-attachments/assets/295a9054-3e67-499a-a987-76b58d21bd40)  
### Test di logout:
Per effettuare il logout, l’utente loggato dovrà premere in alto a destra sul pulsante logout. Una volta premuto l’utente sarà reindirizzato sulla seguente pagina, verrà cancellata sia la sessione corrente che il cookie remember me (verrà cancellato anche dalla tabella remember me tokens):  
![image](https://github.com/user-attachments/assets/35011480-c0d8-4bcb-88bf-7143051daeb0)  
Se l’utente prova a tornare in /Proposte, verrà reindirizzato alla seguente pagina:  
![image](https://github.com/user-attachments/assets/6f1e75cf-76e7-4a66-ad0d-0b420ec698c3)  

# TEST D'ABUSO
Il Test di Abuso è una metodologia di testing volta a identificare le vulnerabilità di un sistema quando è esposto a condizioni d’uso improprie, intenzionali o non intenzionali. È particolarmente utilizzato per valutare la sicurezza, la robustezza e la resilienza di un’applicazione o di un sistema rispetto ad attacchi, errori di configurazione o input malformati.  
### Test di registrazione con file non valido (.exe)
Supponiamo che un attaccante voglia cercare di caricare un file eseguibile malevolo (ad esempio, un file .exe generato da Metasploit) sia sul form della registrazione che nelle proposte.  
![image](https://github.com/user-attachments/assets/3bae538c-a4b7-423c-a63d-eedbc236ebad)  

Notiamo che il form non ci consente di inserire file .exe; possiamo quidi effettuare due test  
- 1.	Cambiare estensione da .exe a .jpg (formato consentito):
![image](https://github.com/user-attachments/assets/7633c32e-9d68-49cc-a025-0689568a8064)

Provando a registrarsi otteniamo la seguente risposta:  

![image](https://github.com/user-attachments/assets/330ebc2c-66ee-4364-9843-14095fe8f8a8)  

Tika ha riconosciuto l’eseguibile caricato  

![image](https://github.com/user-attachments/assets/24a5c79b-599a-4c3c-9bb1-295a016e2153)

- 2.	Disabilitare Javascript ed eliminare l’attributo accept:
![image](https://github.com/user-attachments/assets/4d31f748-758c-4c55-8467-45a5c241ed86)
possiamo quindi caricare il file eseguibile:
![image](https://github.com/user-attachments/assets/a0751a5d-099a-4653-bd12-bb4ad1825b62)
Anche in questo caso otteniamo il seguente comportamento:
![image](https://github.com/user-attachments/assets/e86046bc-0af2-4637-9938-5b53023bf865)
Lo stesso test è stato effettuato sul form delle proposte ed è stato riscontrato lo stesso comportamento
### Test di registrazione con form aventi campi vuoti
In questo test, si cerca di inviare un modulo con uno o più campi vuoti per verificare se l’applicazione riesce a gestire correttamente l’errore e impedire la registrazione incompleta.  

Disabilito Javascript per evadere i controlli lato client. Successivamente modifico i form togliendo tutti i required:  
![image](https://github.com/user-attachments/assets/257deed2-438e-4303-9eea-a01dc219ff98)  
Otteniamo il seguente comportamento:  
![image](https://github.com/user-attachments/assets/c37c7e4d-223f-4247-81b7-ca80370adae1)  
Naturalmente in questo test sono stati cancellati tutti i required, in realtà il test è stato amplificato provando ad eliminare i required uno alla volta per ogni campo ed il comportamento ottenuto è lo stesso  
### Test di registrazione con form mancanti
In questo test, si cerca di inviare un modulo con uno o più campi mancanti per verificare se l’applicazione riesce a gestire correttamente l’errore.  

Disabilito Javascript per evadere i controlli lato client. Successivamente modifico il form:  
![image](https://github.com/user-attachments/assets/a98043a5-ddd8-4bc3-a9c8-dd45faeb1158)  
Ottenendo il seguente comportamento:  
![image](https://github.com/user-attachments/assets/6abc2fbd-c264-41f9-ad2f-7a87c4dc4883)  
Lo stesso test è stato ripetuto sui form della registrazione e sul caricamento delle proposte ottenendo lo stesso comportamento	  

# TEST DI ATTACCO

### Test di attacco DoS
Proviamo ad attaccare il server con un attacco DoS:  
![image](https://github.com/user-attachments/assets/a26d305f-b048-4a15-a653-9276122f0f52)  
Notiamo come l’indirizzo IP venga bloccato dal Packet Filter inserendolo in una blacklist  
![image](https://github.com/user-attachments/assets/15aef292-4b43-45ff-9a59-e197fa8dfb2e)  

### Test di attacco XSS
Proviamo a iniettare <script>alert('Hacked!');</script> nei form di login e registrazione, avendo disabilitato JavaScript  
![image](https://github.com/user-attachments/assets/219a37d5-33f9-4057-9ad0-514fae88c230)  
Ottenendo il seguente comportamento:  
![image](https://github.com/user-attachments/assets/1263bb23-df30-49d1-8677-66354efc08e6)  
Dal punto di vista lato server comunque per sicurezza è stato effettuato l’escape:  
![image](https://github.com/user-attachments/assets/3dee4845-fc1d-4c2c-8e57-010c51b68ccb)  
Naturalmente, questa email non valida viene bloccata dal controllo successivo, eseguito tramite il metodo isValidEmail(), che impedisce che l’email errata venga passata alla query del database.  

Lo stesso comportamento è stato ottenuto effettuando XSS dalla pagina di registrazione.  

Proviamo a iniettare una proposta dal nome <script>alert('Hacked!');</script> in modo tale da provare ad effettuare un attacco di tipo XSS Reflected  
![image](https://github.com/user-attachments/assets/a8eccf3d-0560-4a55-a305-30d6259abfb4)  
Ottenendo questo comportamento:  
![image](https://github.com/user-attachments/assets/3b6be722-b6ca-4d0b-ab8e-43494d683fdc)  
Proviamo ad effettuare un attacco di tipo XSS Stored, carichiamo un file txt con script malevolo  
![image](https://github.com/user-attachments/assets/652a52f7-68de-4e61-bcd6-dc0ea2ebd3e6)  
Ora proviamo a visualizzarlo:  
![image](https://github.com/user-attachments/assets/f71b4fed-f34d-4fd4-99ed-d9e8a91a9ef6)  
Ora proviamo a scaricarlo:  
![image](https://github.com/user-attachments/assets/b30becce-489d-4f1a-8745-0302c265030f)  
Sebbene il file venga memorizzato nel database, al momento della visualizzazione nel browser, il contenuto viene sottoposto a escape per evitare potenziali vulnerabilità di sicurezza, come attacchi XSS. Tuttavia, quando l’utente scarica il file, esso viene restituito integralmente, senza alcuna modifica.  
Questo approccio consente di non limitare l’utente nell’utilizzo del file, ad esempio, nel caso in cui desideri mostrare un codice all’interno della proposta, mantenendo comunque un elevato livello di sicurezza durante la visualizzazione.  
### Test di attacco SQLInjection
Proviamo a iniettare ’ OR 1=1 – nei form di login e registrazione, avendo disabilitato JavaScript:  
![image](https://github.com/user-attachments/assets/4964d23a-5400-48be-a736-8f02cb8323f4)  
Ottenendo il seguente comportamento:  
![image](https://github.com/user-attachments/assets/489f4efe-d30d-486f-a94d-0e38985b0559)  
Dal punto di vista lato server, comunque, per sicurezza è stato effettuato l’escape:  
![image](https://github.com/user-attachments/assets/5632abb7-2a09-4275-8e72-4999ae1f4621)  
Naturalmente, questa email non valida viene bloccata dal controllo successivo, eseguito tramite il metodo isValidEmail(), che impedisce che l’email errata venga passata alla query del database.Lo stesso comportamento è stato ottenuto effettuando SQLInjection dalla pagina di registrazione.











































