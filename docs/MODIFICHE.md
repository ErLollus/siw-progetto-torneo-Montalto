# Modifiche apportate al progetto SiwTorneo

Adeguamento del progetto alle linee guida del PDF (Progetto assegnato dal docente,
Giugno/Luglio 2026) e modernizzazione dell'interfaccia.

## Credenziali di accesso (dati iniziali)

All'avvio, se il database è vuoto, vengono creati automaticamente:

| Ruolo | Username | Password |
|-------|----------|----------|
| ADMIN | `admin`  | `admin`  |
| USER  | `user`   | `user`   |

## Come eseguire

1. Avviare PostgreSQL con il database indicato in `application.properties`
   (`jdbc:postgresql://localhost:5434/siw-progetto-torneo`, utente/pw `postgres`).
2. `./mvnw spring-boot:run` (o avvio da IntelliJ). App su `http://localhost:8081`.
3. Test (usano H2 in-memory, nessun DB richiesto): `./mvnw test`.
   - Analisi N+1: `./mvnw -Dtest=NPlusUnoAnalysisTest test`.

## Requisiti del PDF — cosa è stato fatto

### 3. Entità
Aggiunta l'entità **`Commento`** (testo, data, relazioni `@ManyToOne` verso
`Partita` e `Utente`). Totale entità: Torneo, Squadra, Giocatore, Partita,
Arbitro, Utente, Commento (7).

### 4.2 Funzionalità utenti registrati (prima assenti)
- Pagina **dettaglio partita** (`/partita/{id}`) con elenco commenti.
- **Inserimento** commento (utente autenticato).
- **Modifica/eliminazione** dei *soli* commenti propri (controllo di proprietà
  nel `CommentoService`).

### 4.1 Funzionalità pubbliche
- Aggiunta la pagina di **dettaglio torneo** (`/torneo/{id}`) con squadre
  partecipanti e calendario.

### 5. Sicurezza (corretta)
- Prima: `/admin/**` era accessibile a *qualsiasi* utente autenticato.
  Ora è protetto con `hasRole('ADMIN')`.
- Autenticazione basata su entità `Utente` tramite `TorneoUserDetailsService`
  (ruoli mappati su `ROLE_USER` / `ROLE_ADMIN`).
- `findByUsername` implementato correttamente (prima restituiva `null`).
- CSRF riabilitato (i form Thymeleaf includono il token).

### 7. Transazioni
- Metodi di sola lettura annotati con `@Transactional(readOnly = true)`;
  metodi di scrittura con `@Transactional`.

### 8. Prestazioni / N+1 (obbligatorio)
- Metodi repository con **JOIN FETCH** ed **EntityGraph** in `PartitaRepository`.
- Test `NPlusUnoAnalysisTest` che misura numero di query e tempi.
- Documento [`ANALISI-NPLUS1.md`](ANALISI-NPLUS1.md) con discussione dei risultati.
- La vista `/partite` usa la strategia join fetch per evitare le N+1.

### 9. Frontend
- Thymeleaf modernizzato (layout condiviso, Bootstrap 5, tema personalizzato).
- Parte in **React** mantenuta e restilizzata (classifica live via API REST).

### 11. Validazione e gestione errori
- Bean Validation (`@NotBlank`, `@NotNull`, `@Past`, `@Min`/`@Max`…) su tutte le
  entità; `@Valid` + `BindingResult` nei controller; messaggi di errore nei form.
- `GlobalExceptionHandler` + pagina `error.html`.

### pom.xml
- Aggiunti `spring-boot-starter-validation` e `H2` (scope test).

## Malfunzionamenti noti
- Nessuno noto. La build non è stata eseguita nell'ambiente di preparazione
  (mancava JDK 25/Maven): si consiglia una verifica con `./mvnw clean test`
  su IntelliJ prima della consegna.
