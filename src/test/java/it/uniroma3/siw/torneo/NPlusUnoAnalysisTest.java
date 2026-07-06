package it.uniroma3.siw.torneo;

import it.uniroma3.siw.torneo.model.Arbitro;
import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.model.Squadra;
import it.uniroma3.siw.torneo.model.Torneo;
import it.uniroma3.siw.torneo.repository.ArbitroRepository;
import it.uniroma3.siw.torneo.repository.PartitaRepository;
import it.uniroma3.siw.torneo.repository.SquadraRepository;
import it.uniroma3.siw.torneo.repository.TorneoRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ANALISI SPERIMENTALE SULLE STRATEGIE DI ACCESSO AI DATI (Sezione 8 del PDF).
 *
 * Caso d'uso: "visualizzazione del calendario delle partite". Per ogni partita
 * la vista naviga verso torneo, squadra di casa, squadra in trasferta e arbitro.
 *
 * Confrontiamo tre strategie:
 *   1) LAZY   : findAll() + navigazione delle associazioni -> problema N+1
 *   2) JOIN FETCH : un'unica query con LEFT JOIN FETCH
 *   3) ENTITY GRAPH : equivalente dichiarativo
 *
 * Misuriamo il NUMERO di statement SQL eseguiti (via Hibernate Statistics) e il
 * tempo di esecuzione. Il numero di query è la metrica più significativa per il
 * problema N+1: con la strategia LAZY cresce col numero di partite, con le altre
 * due resta costante (1 sola query).
 */
@DataJpaTest
class NPlusUnoAnalysisTest {

    private static final int NUMERO_PARTITE = 20;

    @Autowired private PartitaRepository partitaRepository;
    @Autowired private SquadraRepository squadraRepository;
    @Autowired private ArbitroRepository arbitroRepository;
    @Autowired private TorneoRepository torneoRepository;
    @Autowired private EntityManager entityManager;

    @BeforeEach
    void seed() {
        Torneo torneo = new Torneo();
        torneo.setNome("Torneo Test");
        torneo.setAnno(2026);
        torneoRepository.save(torneo);

        List<Squadra> squadre = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Squadra s = new Squadra();
            s.setNome("Squadra " + i);
            s.setCitta("Roma");
            s.setAnnoFondazione(2000 + i);
            squadraRepository.save(s);
            squadre.add(s);
        }

        List<Arbitro> arbitri = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Arbitro a = new Arbitro();
            a.setNome("Arbitro");
            a.setCognome("Numero " + i);
            a.setCodiceArbitrale("ARB-" + i);
            arbitroRepository.save(a);
            arbitri.add(a);
        }

        for (int i = 0; i < NUMERO_PARTITE; i++) {
            Partita p = new Partita();
            p.setTorneo(torneo);
            p.setSquadraCasa(squadre.get(i % squadre.size()));
            p.setSquadraTrasferta(squadre.get((i + 1) % squadre.size()));
            p.setArbitro(arbitri.get(i % arbitri.size()));
            p.setDataOra(LocalDateTime.now().plusDays(i));
            p.setLuogo("Campo " + i);
            p.setGoalsHome(i % 4);
            p.setGoalsAway(i % 3);
            p.setStato("TERMINATA");
            partitaRepository.save(p);
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void confrontaStrategieDiAccesso() {
        long queryLazy      = misura("1) LAZY (findAll + navigazione)", () -> {
            List<Partita> partite = new ArrayList<>();
            partitaRepository.findAll().forEach(partite::add);
            return partite;
        });
        long queryJoinFetch = misura("2) JOIN FETCH", () -> partitaRepository.findAllWithDettagli());
        long queryGraph     = misura("3) ENTITY GRAPH", () -> partitaRepository.findAllWithEntityGraph());

        System.out.println("\n================ RISULTATI ANALISI N+1 ================");
        System.out.println("Partite create: " + NUMERO_PARTITE);
        System.out.println("Query LAZY        : " + queryLazy);
        System.out.println("Query JOIN FETCH  : " + queryJoinFetch);
        System.out.println("Query ENTITY GRAPH: " + queryGraph);
        System.out.println("=======================================================\n");

        // Le strategie ottimizzate devono eseguire molte meno query della LAZY.
        assertTrue(queryJoinFetch < queryLazy, "Il join fetch deve ridurre le query rispetto alla strategia LAZY");
        assertTrue(queryGraph < queryLazy, "L'EntityGraph deve ridurre le query rispetto alla strategia LAZY");
    }

    /**
     * Esegue la strategia fornita, naviga TUTTE le associazioni (per forzare il
     * caricamento lazy) e restituisce il numero di statement SQL eseguiti.
     */
    private long misura(String etichetta, Supplier<List<Partita>> strategia) {
        Session session = entityManager.unwrap(Session.class);
        Statistics stats = session.getSessionFactory().getStatistics();
        entityManager.clear();
        stats.clear();

        long inizio = System.nanoTime();
        List<Partita> partite = strategia.get();
        // Navigazione delle associazioni: è qui che la strategia LAZY genera le query extra
        for (Partita p : partite) {
            if (p.getTorneo() != null) p.getTorneo().getNome();
            if (p.getSquadraCasa() != null) p.getSquadraCasa().getNome();
            if (p.getSquadraTrasferta() != null) p.getSquadraTrasferta().getNome();
            if (p.getArbitro() != null) p.getArbitro().getCognome();
        }
        long fineMs = (System.nanoTime() - inizio) / 1_000_000;
        long query = stats.getPrepareStatementCount();
        System.out.printf("%-35s -> %3d query, %4d ms%n", etichetta, query, fineMs);
        return query;
    }
}
