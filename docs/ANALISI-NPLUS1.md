# Analisi sperimentale sulle strategie di accesso ai dati (N+1)

*Progetto SiwTorneo — Sistemi Informativi su Web, a.a. 2025/2026*
*Riferimento: Sezione 8 delle linee guida del progetto.*

## 1. Obiettivo

Dimostrare, su un caso d'uso significativo, la differenza tra diverse strategie
di accesso ai dati con JPA/Hibernate, misurando in particolare l'impatto del
**problema delle N+1 query**.

## 2. Caso d'uso analizzato

**Visualizzazione del calendario delle partite** (`GET /partite`).

È un caso d'uso che coinvolge più entità: per ogni `Partita` la vista naviga
verso quattro associazioni `@ManyToOne`:

- `torneo`
- `squadraCasa`
- `squadraTrasferta`
- `arbitro`

Poiché le associazioni `@ManyToOne` in Hibernate sono di default `EAGER`, ma le
query derivate e `findAll()` non fanno join automatici su tutte le associazioni,
la semplice iterazione sulle partite può generare una query aggiuntiva per ogni
associazione non ancora presente nel persistence context: è il classico
**problema N+1**.

## 3. Strategie confrontate

| # | Strategia | Implementazione |
|---|-----------|-----------------|
| 1 | **LAZY / default** | `partitaRepository.findAll()` seguito dalla navigazione delle associazioni |
| 2 | **JOIN FETCH** | `@Query("SELECT DISTINCT p FROM Partita p LEFT JOIN FETCH p.torneo LEFT JOIN FETCH p.squadraCasa LEFT JOIN FETCH p.squadraTrasferta LEFT JOIN FETCH p.arbitro")` |
| 3 | **EntityGraph** | `@EntityGraph(attributePaths = {"torneo","squadraCasa","squadraTrasferta","arbitro"})` |

I metodi sono definiti in
[`PartitaRepository`](../src/main/java/it/uniroma3/siw/torneo/repository/PartitaRepository.java)
(`findAllWithDettagli()` e `findAllWithEntityGraph()`).

## 4. Metodologia della misurazione

Il confronto è automatizzato nel test
[`NPlusUnoAnalysisTest`](../src/test/java/it/uniroma3/siw/torneo/NPlusUnoAnalysisTest.java)
(`@DataJpaTest` su database H2 in-memory):

1. si popolano 20 partite, 6 squadre, 4 arbitri, 1 torneo;
2. si svuota il persistence context (`entityManager.clear()`) prima di ogni
   strategia, così da partire sempre da cache fredda;
3. si esegue la strategia e si **navigano tutte le associazioni** (per forzare
   l'eventuale caricamento lazy);
4. si conta il numero di statement SQL con le **Hibernate Statistics**
   (`Statistics.getPrepareStatementCount()`) e si misura il tempo di esecuzione.

Per eseguire l'analisi:

```bash
mvn -Dtest=NPlusUnoAnalysisTest test
```

## 5. Risultati (valori rappresentativi)

Con 20 partite si ottengono risultati dell'ordine di grandezza seguente:

| Strategia | Query SQL eseguite | Tempo |
|-----------|:------------------:|:-----:|
| LAZY (findAll + navigazione) | ~12 | maggiore |
| JOIN FETCH | **1** | minore |
| EntityGraph | **1** | minore |

> I valori esatti vengono stampati a console eseguendo il test; il numero di
> query per la strategia LAZY dipende dal numero di entità distinte associate
> (Hibernate riusa le entità già presenti nel persistence context, quindi il
> conteggio non è esattamente 1+4·N ma cresce comunque con i dati).

## 6. Discussione

- **LAZY**: emette una query per la lista delle partite e poi ulteriori query
  per caricare torneo, squadre e arbitri non ancora in cache. All'aumentare del
  numero di partite/associazioni distinte il numero di query cresce: è il
  comportamento tipico del problema N+1, con impatto negativo sulle prestazioni.
- **JOIN FETCH**: recupera partite e associazioni in **una sola query** grazie ai
  `LEFT JOIN FETCH`. Il `DISTINCT` evita duplicati di riga. È la scelta più
  diretta quando si sa in anticipo quali associazioni servono alla vista.
- **EntityGraph**: ottiene lo stesso risultato del join fetch ma in modo
  **dichiarativo**, mantenendo la query JPQL pulita e riutilizzabile.

## 7. Scelta implementativa adottata

Nella vista reale del calendario ([`PartitaController.getPartite`](../src/main/java/it/uniroma3/siw/torneo/controller/PartitaController.java))
si usa `partitaService.findAllConDettagli()`, cioè la strategia **JOIN FETCH**,
perché per quella pagina servono sempre tutte le associazioni e vogliamo un
numero di query costante (1) indipendente dal numero di partite.

Per operazioni in cui le associazioni non servono, si mantiene invece il
comportamento lazy per non caricare dati inutili. Questo bilanciamento è coerente
con l'obiettivo della Sezione 8: progettare l'accesso ai dati in modo efficiente
e motivare le scelte in base al caso d'uso.
