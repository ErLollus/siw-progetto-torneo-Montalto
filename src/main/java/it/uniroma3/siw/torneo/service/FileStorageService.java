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
 * Salva i file caricati (immagini di giocatori e squadre) su una cartella
 * esterna al classpath, in modo che restino disponibili anche a runtime
 * (a differenza di src/main/resources, non scrivibile una volta impacchettata l'app).
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
