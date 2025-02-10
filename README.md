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
•	Integrità dei dati: A differenza di altre modalità di crittografia, GCM offre una funzionalità di autenticazione che consente di verificare l’integrità dei dati criptati, garantendo che i dati non siano stati alterati durante il trasferimento o l’archiviazione. Questo viene realizzato tramite un tag di autenticazione che viene calcolato insieme alla crittografia dei dati. In questo modo, è possibile verificare che i dati non siano stati manomessi prima della decrittazione.
•	Efficienza: La modalità GCM è progettata per operare in modo parallelo, consentendo di crittografare e autenticare i dati in modo più veloce rispetto ad altre modalità come CBC. Questo rende GCM particolarmente adatta per scenari in cui le performance sono un fattore critico, senza compromettere la sicurezza.
•	Resistenza agli attacchi: GCM è progettata per resistere ad attacchi di tipo padding oracle e ad altri tipi di attacchi comuni contro modalità meno sicure, come CBC, rendendo così la scelta ideale per la protezione dei dati sensibili.
La crittografia AES-GCM utilizza un IV (Initialization Vector) per garantire che ogni operazione di crittografia produca un risultato unico, anche quando i dati da cifrare sono identici. L’IV viene generato casualmente per ogni operazione di crittografia e, insieme ai dati cifrati, viene memorizzato nel risultato finale. Durante la decrittografia, l’IV viene recuperato e utilizzato per restituire i dati al loro stato originale.
La crittografia AES con modalità GCM è stata implementata per proteggere due tipi di dati particolarmente sensibili:
•	File delle Proposte: I file che contengono le proposte degli utenti vengono cifrati per prevenire accessi non autorizzati al database “proposte” che li contiene. L’uso di AES-GCM garantisce che, anche in caso di accesso non autorizzato al file, i dati non possano essere letti senza la corretta chiave di decrittazione. Inoltre, l’integrità dei file viene preservata grazie al tag di autenticazione GCM.
•	Cookie “Remember Me”: Il cookie “Remember Me” migliora l’esperienza utente permettendo di mantenere l’autenticazione tra una sessione e l’altra, evitando di dover reinserire le credenziali ad ogni accesso. Per garantire la sicurezza, il cookie è cifrato con AES-GCM, proteggendolo da eventuali attacchi che tentano di decifrarlo senza la chiave corretta. Inoltre, il sistema di autenticazione AES-GCM assicura l’integrità del cookie, prevenendo qualsiasi tentativo di manomissione o alterazione del suo contenuto.

Per garantire la sicurezza delle credenziali dei database e delle due chiavi di AES, è stato scelto di adottare HashiCorp Vault, una delle soluzioni di gestione dei segreti più affidabili e sicure disponibili. Vault è progettato per proteggere, gestire e distribuire in modo sicuro informazioni sensibili come password, chiavi di crittografia, certificati e token di accesso. In un contesto in cui la sicurezza dei dati è fondamentale, Vault fornisce un’architettura solida che permette di centralizzare la gestione dei segreti, riducendo al minimo i rischi di esposizione non autorizzata.
Vault offre anche funzioni di auditing avanzate, che registrano tutte le operazioni di accesso e gestione delle credenziali, permettendo un controllo completo sulle attività relative ai segreti. Ogni richiesta di accesso a una chiave o a una credenziale viene registrata e monitorata, con la possibilità di eseguire analisi forensi in caso di incidenti di sicurezza. Inoltre, grazie all’integrazione di politiche di accesso basate su ruoli, Vault assicura che le credenziali e le chiavi siano disponibili solo per gli utenti o i servizi che ne hanno effettivamente bisogno, minimizzando la superficie di attacco.
In questo modo, la sicurezza delle chiavi di AES utilizzate per la cifratura dei dati e delle credenziali dei database è rafforzata, proteggendo gli accessi alle risorse sensibili e prevenendo eventuali attacchi di tipo man-in-the-middle, o altri tentativi di compromettere la riservatezza e l’integrità dei dati.
Per migliorare la sicurezza e garantire che nessun token di accesso venga salvato o archiviato in modo permanente, è stata implementata una procedura di sicurezza che prevede che l’amministratore del server debba inserire manualmente il token corretto al momento dell’avvio del server. L’amministratore avrà a disposizione tre tentativi per inserire il token correttamente. Questo approccio impedisce il rischio che un token venga compromesso o accessibile a terze parti, poiché non viene mai memorizzato in modo permanente nel sistema o nei file di configurazione del server. Se i tentativi di inserimento del token falliscono, l’avvio del server verrà interrotto, impedendo l’accesso non autorizzato e garantendo una maggiore protezione contro attacchi di tipo brute-force o l’accesso non autorizzato al sistema. In questo modo, il sistema è progettato per ridurre al minimo la possibilità di esposizione dei dati sensibili e assicurare che solo il server legittimo abbia accesso alle risorse protette.

