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
