package it.uniroma3.siw.torneo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Classe che salva le immagini caricate (per giocatori e squadre) in una
 * cartella fuori dal progetto. L'ho fatto così perché src/main/resources,
 * una volta impacchettato il jar, non è più scrivibile: se salvassi lì le
 * immagini sparirebbero appena riavvio l'app in produzione.
 */
@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String salva(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String originale = file.getOriginalFilename();
            String estensione = "";
            if (originale != null && originale.contains(".")) {
                estensione = originale.substring(originale.lastIndexOf('.'));
            }
            String nomeFile = UUID.randomUUID() + estensione;
            file.transferTo(dir.resolve(nomeFile));
            return nomeFile;
        } catch (IOException e) {
            throw new RuntimeException("Errore nel salvataggio del file caricato", e);
        }
    }
}
