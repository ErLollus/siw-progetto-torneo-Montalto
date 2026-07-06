package it.uniroma3.siw.torneo;

import it.uniroma3.siw.torneo.model.Arbitro;
import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.model.Squadra;
import it.uniroma3.siw.torneo.model.StatoPartita;
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
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Qui provo a confrontare le diverse strategie di fetch viste a lezione,
 * come chiesto nella sezione 8 del PDF del progetto (quella sulle
 * prestazioni e l'accesso ai dati).
 *
 * Il caso d'uso che ho scelto è il calendario delle partite: per ogni
 * partita bisogna andare a prendere torneo, squadra casa, squadra
 * trasferta e arbitro, quindi è perfetto per far vedere il problema N+1.
 *
 * Provo 3 modi diversi di fare la stessa query:
 *   1) LAZY normale (findAll + poi navigo le relazioni) -> qui nasce il problema N+1
 *   2) una query con JOIN FETCH scritta a mano
 *   3) la stessa cosa ma con @EntityGraph
 *
 * Per confrontarle conto quante query SQL vengono eseguite davvero (uso le
 * Statistics di Hibernate) e quanto tempo ci mette. Il numero di query
 * secondo me è il dato più chiaro: con LAZY aumenta all'aumentare delle
 * partite, con le altre due resta sempre 1.
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
            p.setStato(StatoPartita.TERMINATA);
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

        // mi aspetto che le due strategie ottimizzate facciano molte meno query rispetto a LAZY
        assertTrue(queryJoinFetch < queryLazy, "Il join fetch deve ridurre le query rispetto alla strategia LAZY");
        assertTrue(queryGraph < queryLazy, "L'EntityGraph deve ridurre le query rispetto alla strategia LAZY");
    }

    /**
     * Lancia la strategia passata come parametro e poi legge tutti i campi
     * delle relazioni (così, se sono LAZY, le forzo a caricarsi davvero).
     * Alla fine ritorna quante query SQL sono state eseguite in totale.
     */
    private long misura(String etichetta, Supplier<List<Partita>> strategia) {
        Session session = entityManager.unwrap(Session.class);
        Statistics stats = session.getSessionFactory().getStatistics();
        entityManager.clear();
        stats.clear();

        long inizio = System.nanoTime();
        List<Partita> partite = strategia.get();
        // qui navigo le relazioni: con LAZY è proprio in questo punto che partono le query in più
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
