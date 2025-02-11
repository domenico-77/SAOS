# PROGETTO SAOS di VICENTI DOMENICO
Nel presente documento viene descritta l'attività svolta di messa in sicurezza di un'applicazione web. L'obiettivo principale è stato l'identificazione e la mitigazione di criticità legate alla gestione dei dati sensibili degli utenti, ma anche alla sicurezza delle richieste HTTPs, alla protezione contro attacchi XSS e all'upload arbitrario di file.  

L'applicazione web oggetto dell’analisi è stata progettata come un punto di condivisione di idee di miglioramento dell’attività aziendale. Si occupa quindi di gestire i file contenenti proposte, inviati dai dipendenti tramite documenti di testo in formato txt. Il sistema consente agli utenti di registrarsi, accedere alla piattaforma e caricare le proprie proposte, che vengono archiviate e rese disponibili per la consultazione anche da parte di altri utenti.  

Questa funzionalità permette ai dipendenti di visualizzare le proposte già inserite, favorendo il confronto e la collaborazione sulle idee per migliorare l’attività aziendale. Tuttavia, la gestione di questi file e delle informazioni sensibili inserite dagli utenti ha reso necessaria l’implementazione di specifiche misure di sicurezza per prevenire vulnerabilità riscontrate:  

1.	**Protezione dei dati sensibili e limitazione della durata in memoria**

È stato analizzato il rischio derivante dalla persistenza di credenziali e file contenenti informazioni sensibili all'interno della memoria dell'applicazione. Le credenziali venivano archiviate in variabili globali o log senza crittografia. 

3.	**Protezione contro la memorizzazione di dati sensibili lato client**

È stata rilevata la possibilità che informazioni critiche venissero archiviate nei cookie senza protezione adeguata, esponendo gli utenti ad attacchi di session hijacking. Una vulnerabilità comune riscontrata è la seguente:
```js
document.cookie = "sessionID=123456; path=/";
```

4.	**Prevenzione degli attacchi XSS (Cross-Site Scripting)**

La vulnerabilità XSS si verifica quando un'applicazione permette l'inserimento e l'esecuzione di script malevoli nel browser di un utente Un esempio è il seguente:
```JS
<input type="text" name="username" value="<?php echo $_GET['name']; ?>">
```
5.	**Mitigazione del caricamento arbitrario di file**

È stata riscontrata una vulnerabilità che permetteva agli utenti di caricare file pericolosi senza validazione. Un attaccante potrebbe caricare un file PHP malevolo come:
```js
<?php system($_GET['cmd']); ?>
```
6.	**Sicurezza nelle richieste HTTPS**

E’ stato necessario implementare una gestione sicura delle sessioni, con time-out appropriato per evitare sessioni di lunga durata non necessarie. Gestire quindi in modo sicuro la creazione e la distruzione delle sessioni, prevenendo la session fixation.  

7.	**Mancanza di un sistema di alert per la gestione degli accessi**  

È stata rilevata l'assenza di un meccanismo di monitoraggio e allerta per accessi sospetti o tentativi di azioni anomale. Questa lacuna impedisce il monitoraggio del sistema in tempo reale.  

## Misure di sicurezza adottate
### **Gestione sicura delle chiavi e Crittografia**  

Per garantire la sicurezza dei dati sensibili gestiti nell’applicazione, è stata adottata la crittografia AES (Advanced Encryption Standard) con modalità GCM (Galois/Counter Mode), gestita dalla classe AESUtils. Questa scelta è stata motivata da diversi fattori legati alla sicurezza, all’efficienza e alla facilità di gestione delle chiavi.  

AES è uno degli algoritmi di crittografia più sicuri e ampiamente utilizzati per proteggere i dati sensibili. La modalità GCM (Galois/Counter Mode) è stata selezionata in quanto fornisce numerosi vantaggi rispetto ad altre modalità di AES, come ECB (Electronic Codebook) e CBC (Cipher Block Chaining):  

•	**Integrità dei dati**: A differenza di altre modalità di crittografia, GCM offre una funzionalità di autenticazione che consente di verificare l’integrità dei dati criptati, garantendo che i dati non siano stati alterati durante il trasferimento o l’archiviazione. Questo viene realizzato tramite un tag di autenticazione che viene calcolato insieme alla crittografia dei dati. In questo modo, è possibile verificare che i dati non siano stati manomessi prima della decrittazione.  

•	**Efficienza**: La modalità GCM è progettata per operare in modo parallelo, consentendo di crittografare e autenticare i dati in modo più veloce rispetto ad altre modalità come CBC. Questo rende GCM particolarmente adatta per scenari in cui le performance sono un fattore critico, senza compromettere la sicurezza.  

•	**Resistenza agli attacchi**: GCM è progettata per resistere ad attacchi di tipo padding oracle e ad altri tipi di attacchi comuni contro modalità meno sicure, come CBC, rendendo così la scelta ideale per la protezione dei dati sensibili.
La crittografia AES-GCM utilizza un IV (Initialization Vector) per garantire che ogni operazione di crittografia produca un risultato unico, anche quando i dati da cifrare sono identici. L’IV viene generato casualmente per ogni operazione di crittografia e, insieme ai dati cifrati, viene memorizzato nel risultato finale. Durante la decrittografia, l’IV viene recuperato e utilizzato per restituire i dati al loro stato originale.  

La crittografia AES con modalità GCM è stata implementata per proteggere due tipi di dati particolarmente sensibili:  

•	**File delle Proposte**: I file che contengono le proposte degli utenti vengono cifrati per prevenire accessi non autorizzati al database “proposte” che li contiene. L’uso di AES-GCM garantisce che, anche in caso di accesso non autorizzato al file, i dati non possano essere letti senza la corretta chiave di decrittazione. Inoltre, l’integrità dei file viene preservata grazie al tag di autenticazione GCM.  

•	**Cookie “Remember Me”**: Il cookie “Remember Me” migliora l’esperienza utente permettendo di mantenere l’autenticazione tra una sessione e l’altra, evitando di dover reinserire le credenziali ad ogni accesso. Per garantire la sicurezza, il cookie è cifrato con AES-GCM, proteggendolo da eventuali attacchi che tentano di decifrarlo senza la chiave corretta. Inoltre, il sistema di autenticazione AES-GCM assicura l’integrità del cookie, prevenendo qualsiasi tentativo di manomissione o alterazione del suo contenuto.  

Per garantire la sicurezza delle credenziali dei database e delle due chiavi di AES, è stato scelto di adottare **HashiCorp Vault**, una delle soluzioni di gestione dei segreti più affidabili e sicure disponibili. Vault è progettato per proteggere, gestire e distribuire in modo sicuro informazioni sensibili come password, chiavi di crittografia, certificati e token di accesso. In un contesto in cui la sicurezza dei dati è fondamentale, Vault fornisce un’architettura solida che permette di centralizzare la gestione dei segreti, riducendo al minimo i rischi di esposizione non autorizzata.  
![image](https://github.com/user-attachments/assets/48cebe90-dbb0-4326-bafe-6949351b36fd)  


Vault offre anche funzioni di auditing avanzate, che registrano tutte le operazioni di accesso e gestione delle credenziali, permettendo un controllo completo sulle attività relative ai segreti.  
Ogni richiesta di accesso a una chiave o a una credenziale viene registrata e monitorata, con la possibilità di eseguire analisi forensi in caso di incidenti di sicurezza. Inoltre, grazie all’integrazione di politiche di accesso basate su ruoli, Vault assicura che le credenziali e le chiavi siano disponibili solo per gli utenti o i servizi che ne hanno effettivamente bisogno, minimizzando la superficie di attacco.  

In questo modo, la sicurezza delle chiavi di AES utilizzate per la cifratura dei dati e delle credenziali dei database è rafforzata, proteggendo gli accessi alle risorse sensibili e prevenendo eventuali attacchi di tipo man-in-the-middle, o altri tentativi di compromettere la riservatezza e l’integrità dei dati.  

Per migliorare la sicurezza e garantire che nessun token di accesso venga salvato o archiviato in modo permanente, è stata implementata una procedura di sicurezza che prevede che l’amministratore del server debba inserire manualmente il token corretto al momento dell’avvio del server. L’amministratore avrà a disposizione tre tentativi per inserire il token correttamente. Questo approccio impedisce il rischio che un token venga compromesso o accessibile a terze parti, poiché non viene mai memorizzato in modo permanente nel sistema o nei file di configurazione del server. Se i tentativi di inserimento del token falliscono, l’avvio del server verrà interrotto, impedendo l’accesso non autorizzato e garantendo una maggiore protezione contro attacchi di tipo brute-force o l’accesso non autorizzato al sistema. In questo modo, il sistema è progettato per ridurre al minimo la possibilità di esposizione dei dati sensibili e assicurare che solo il server legittimo abbia accesso alle risorse protette.  

### Hashing delle password e delle proposte 
La classe HashUtil è stata progettata per gestire il processo di hashing sicuro e validazione delle password degli utenti e delle proposte.  

1.	**Password**: Le password degli utenti sono sottoposte a un processo di hashing utilizzando l’algoritmo PBKDF2WITHHMACSHA512, una scelta attentamente ponderata per diversi motivi di sicurezza e affidabilità:

  -	**Elevato livello di sicurezza**: PBKDF2WITHHMACSHA512 offre una protezione robusta contro gli attacchi di forza bruta e dizionario. Ciò è reso possibile grazie all’utilizzo di una funzione di derivazione della chiave che esegue un numero significativo di iterazioni (65536) prima di generare l’hash finale della password. Questa caratteristica rende l’algoritmo particolarmente resistente agli attacchi hardware accelerati, come quelli effettuati tramite GPU, dove la velocità di calcolo delle funzioni hash potrebbe essere potenzialmente sfruttata per compromettere la sicurezza delle password.

  -	**Uso del salt**: Ogni password viene combinata con un salt unico di lunghezza 128 bit, che viene generato casualmente utilizzando la classe SecureRandom, e concatenato alla password prima che venga sottoposta al processo di hashing. Questo approccio è fondamentale per prevenire attacchi tramite tabelle rainbow, che utilizzano una lista precomputata di hash di password comuni. Con l’uso del salt, anche nel caso in cui due utenti scelgano la stessa password, gli hash risultanti saranno completamente differenti, poiché il salt è diverso per ciascun caso, garantendo così l’unicità degli hash.

  -	**Standard di sicurezza riconosciuto**: PBKDF2 è un algoritmo che segue gli standard internazionali definiti nel RFC 2898, ed è considerato uno degli algoritmi più robusti per la protezione delle password. La sua adozione è diffusa in contesti ad alta sicurezza, come nei sistemi bancari, nelle applicazioni aziendali e nelle piattaforme online. La conformità a standard rigorosi assicura che l’algoritmo sia una scelta consolidata e accettata a livello globale per garantire la protezione delle credenziali sensibili degli utenti.

  -	**SHA-512**: Utilizzando SHA-512, si ottiene un output di 512 bit (64 byte). Questo è importante perché una maggiore lunghezza dell’hash rende più difficile per un attaccante determinare la password originale tramite attacchi di forza bruta o collisione, poiché aumenta significativamente lo spazio di ricerca.

2.	**Proposte**: Nonostante le proposte vengano crittografate utilizzando l’algoritmo AES in modalità GCM (Galois/Counter Mode), che offre una protezione sia contro l’accesso non autorizzato ai dati che contro le modifiche, è stato deciso di eseguire un passo aggiuntivo per garantire un ulteriore livello di sicurezza. Prima che il contenuto del file venga crittografato, è stato applicato un algoritmo di hashing, SHA-512, al fine di creare un’impronta unica del contenuto del file stesso. Questo hash viene calcolato e memorizzato separatamente dal file crittografato.

Una volta che il file criptato viene prelevato dal database e decrittografato, il sistema ricalcola l’hash del contenuto del file e lo confronta con l’hash precedentemente memorizzato. Se i due valori non corrispondono, significa che il contenuto del file è stato alterato in qualche modo. In tal caso, il sistema può rilevare la modifica e prevenire l’uso di dati compromessi.
Questa strategia consente di risolvere una problematica critica legata alla vulnerabilità Time-of-Check to Time-of-Use (TOC/TOU)  

### Gestione sicura del Database
Nel database labSAOS sono state create cinque tabelle: 

![image](https://github.com/user-attachments/assets/53e80ba0-ada4-43dc-9307-a6594961331d)

![image](https://github.com/user-attachments/assets/a2088950-1e25-4caa-a115-3891944e0470)

![image](https://github.com/user-attachments/assets/6b207d92-7c72-4a72-9ecf-992dfb28798c)

![image](https://github.com/user-attachments/assets/04348d86-a790-43df-bec5-5abd005e50d8)

![image](https://github.com/user-attachments/assets/211ae5bf-f346-4137-8ec2-96bb7232c674)

Per ciascuna di esse è stato configurato un account SQL separato, protetto da password robuste, con l’obiettivo di garantire un elevato livello di sicurezza e rendere difficili eventuali tentativi di compromissione. È stato applicato in oltre il principio del minimo privilegio, ovvero che ogni utente ha solo i privilegi necessari per svolgere le proprie funzioni. Questo riduce la superficie di attacco e minimizza i danni potenziali in caso di compromissione.  

![image](https://github.com/user-attachments/assets/1519fea2-361a-4805-ab5b-1227af658b78)


Le tabelle verifica_user e verifica_proposte contengono rispettivamente gli hash delle credenziali degli utenti e dei file delle proposte. Per aumentare la sicurezza, queste tabelle sono separate dalle altre, poiché il sale utilizzato per generare gli hash è conservato nelle tabelle user e proposte. Questa separazione rende più difficile per un attaccante ottenere tutte le informazioni sensibili, poiché dovrebbe compromettere entrambe le tabelle e i relativi account: quella con gli hash e quella con i dati originali (compreso il sale).
Per garantire la sicurezza delle connessioni al database, è stato attivato il supporto SSL. Questa configurazione permette di proteggere le comunicazioni tra client e server tramite crittografia, prevenendo l’intercettazione e la manipolazione dei dati sensibili
![image](https://github.com/user-attachments/assets/333b959b-0d1e-4117-aa98-584980869234)  

### Gestione sicura cookie e sessione
Il cookie “remember me” viene impostato con le seguenti opzioni di sicurezza:  

-	**HttpOnly e Secure** sono configurati direttamente nel codice.

-	**SameSite=Strict** è impostato nella configurazione di Apache, nel file context.xml con la seguente direttiva:
```js
<CookieProcessor sameSiteCookies="strict" />
```
Impostare SameSite=Strict per i cookie aumenta la sicurezza impedendo che vengano inviati in richieste provenienti da altri siti (cross-site). Questo protegge da attacchi come il CSRF, dove un sito esterno potrebbe cercare di sfruttare i cookie per compiere azioni non autorizzate a nome dell’utente. Impostando Strict, i cookie vengono inviati solo se la richiesta proviene dallo stesso dominio, migliorando la privacy.  

Il cookie “remember me” viene crittografato utilizzando l’algoritmo AES GCM e viene salvato nel database con una scadenza pari a 7 giorni. Questo consente agli utenti di rimanere autenticati per un periodo prolungato senza la necessità di effettuare nuovamente il login, migliorando l’esperienza utente senza compromettere la sicurezza.  

La classe **TokenCleanupManager** è progettata per gestire la cancellazione automatica dei token “remember me” scaduti dal database.  

-	Ogni 5 minuti, viene eseguito un task che chiama la funzione deleteExpiredTokens per rimuovere i token scaduti. Questo processo garantisce che il database non contenga token inutilizzabili, migliorando la sicurezza.
  
-	L’intervallo di 5 minuti è stato scelto per bilanciare la necessità di mantenere il database pulito e l’efficienza del sistema. Eseguire questa operazione troppo frequentemente potrebbe causare un carico elevato sul sistema, mentre farlo troppo raramente potrebbe lasciare token scaduti per un tempo eccessivo.

Quando l’utente effettua il login e dispone di un cookie “remember me”, il sistema verifica se il cookie è presente nel database. Se il cookie è presente (ovvero non è scaduto), vengono decrittografati sia il cookie inviato dal client che quello memorizzato nel database, e i due valori vengono confrontati per verificarne la corrispondenza.  

Nel caso in cui l’utente abbia già un cookie “remember me” ed effettui un nuovo login selezionando la checkbox ricordami, il sistema verifica se il cookie esistente è presente nel database. Se il cookie è trovato nel database, viene eliminato e un nuovo cookie “remember me” viene generato e memorizzato nel database. Questo processo garantisce che il vecchio cookie venga rimosso e sostituito con uno nuovo, mantenendo la sicurezza e aggiornando la sessione dell’utente.  


Per garantire una maggiore protezione della privacy, nella sessione viene memorizzato l’attributo nickname al posto dell’e-mail. Il nickname, essendo meno sensibile rispetto all’email, non espone direttamente l’identità dell’utente, riducendo il rischio in caso di esposizione accidentale della sessione. Se l’ID della sessione venisse divulgato o se un attaccante riuscisse a ottenere l’accesso alla sessione, non avrebbe accesso a informazioni personali sensibili, come l’e-mail dell’utente. Al contrario, l’e-mail è un dato unico e facilmente identificabile, e se compromessa, potrebbe essere utilizzata in attacchi come brute force o phishing, mettendo a rischio l’account dell’utente.
La durata della sessione è stata impostata a 15 minuti, in modo che scada automaticamente dopo un periodo di inattività dell’utente.  

Per prevenire attacchi di **session fixation**, viene utilizzato il metodo **request.changeSessionId()**, che cambia l’ID di sessione corrente ogni volta che l’utente effettua il login o passa dal filtro AuthenticationFilter. In pratica, quando l’utente accede al sistema, viene generato un nuovo session ID, rendendo invalido quello precedente. Questo è fondamentale per evitare che un attaccante, anche se conosce l’ID di sessione di un utente, possa sfruttarlo, poiché l’ID precedente viene sostituito con uno nuovo durante la creazione della sessione.  

Il filtro **AuthenticationFilter** ha lo scopo di gestire l’autenticazione dell’utente e la validità del cookie “remember me” quando un utente accede a una delle URL protette (/proposte, /downloadProposal):  

-	Verifica se esiste una sessione attiva e se l’utente è già autenticato. Se la sessione è valida, l’utente è considerato autenticato e il filtro permette il passaggio della richiesta alla servlet.
  
-	Se l’utente non ha una sessione attiva, il filtro controlla se è presente un cookie “remember me”. Se il cookie esiste, il filtro cerca di validarlo: Se il cookie è valido, il filtro crea una nuova sessione per l’utente. Se il cookie “remember me” non è presente o non valido, l’utente viene reindirizzato alla pagina sessionExpired.jsp.
  

Quando l’utente clicca sul pulsante di logout, viene attivata la servlet LogoutServlet, che gestisce l’intero processo di disconnessione seguendo questi passaggi:  

1.	**Rimozione del cookie “remember me” dal browser**: Se l’utente possiede un remember me cookie questo viene eliminato dal browser dell’utente impostando il suo MaxAge a 0, garantendo la sua cancellazione.

2.	**Invalidazione della sessione**: La sessione dell’utente viene invalidata, rimuovendo tutti gli attributi, inclusi quelli relativi all’utente.
   
3.	**Rimozione del token “remember me” dal database**: Se l’utente ha un cookie “remember me”, questo viene rimosso dal database
   
4.	**Reindirizzamento post-logout**: Una volta completato il processo di logout, l’utente viene reindirizzato alla pagina logout.jsp, dove viene confermato il successo dell’operazione.
   
### Caricamento sicuro dei file
Per garantire caricamenti sicuri dei file, sono stati implementati i seguenti controlli:  

1.	**Controllo delle estensioni dei file**: Nel form di caricamento dell’immagine del profilo (registrazione.jsp) e nel form di caricamento della proposta (proposte.jsp), è stato implementato un controllo sull’estensione dei file per ridurre il numero di richieste al server.
   
2.	**Controllo delle dimensioni dei file**: Sia lato client che lato server sono stati effettuati controlli sulle dimensioni delle immagini e delle proposte per evitare che superino le dimensioni massime impostate nel database (BLOB).
	
3.	**Sanitizzazione del nome della proposta**: È stato applicato un processo di sanitizzazione sul nome della proposta prima di inserirlo nel database, per prevenire eventuali vulnerabilità come l’iniezione di codice.
   
4.	**Sicurezza tramite Apache Tika**: Apache Tika è stato utilizzato lato server in RegisterServlet e ProposteServlet per garantire la sicurezza durante il caricamento delle foto del profilo e delle proposte. La libreria analizza il contenuto dei file per determinare il tipo MIME corretto, prevenendo così il caricamento di file dannosi mascherati da immagini o documenti legittimi.
   
5.	**Protezione tramite token CSRF (Cross-Site Request Forgery)**: Per il caricamento delle proposte, è stato implementato un token CSRF come misura di sicurezza per proteggere l’applicazione dagli attacchi di tipo CSRF. Quando l’utente carica una proposta, il server genera un token CSRF unico e lo associa alla sessione dell’utente. Questo token viene inviato come parte del modulo HTML (in un campo nascosto). Quando il modulo viene inviato, il server verifica che il token CSRF inviato corrisponda a quello associato alla sessione dell’utente. Se il token non è valido o manca, la richiesta viene respinta, impedendo l’attacco CSRF. Poiché il token è unico per ogni sessione e richiesta, un attaccante non può facilmente indovinare o replicare il token valido. Di conseguenza, anche se un attaccante riesce a indurre l’utente a inviare una richiesta, questa verrà rifiutata dal server a causa della mancanza o invalidità del token CSRF. Dopo l’uso, il token viene rimosso dalla sessione, riducendo il rischio di riutilizzo e prevenendo attacchi di tipo reply.

 

