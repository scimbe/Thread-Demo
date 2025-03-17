package de.haw.hamburg.threaddemo.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Service-Klasse, die I/O-intensive Operationen durchführt
 * Diese Operationen sind ideal, um die Stärken von Virtual Threads zu demonstrieren
 * - Bei blockierenden I/O-Operationen wird der Thread pausiert
 * - Virtual Threads können diese Blockierungen effizient handhaben
 * - Platform Threads hingegen bleiben während I/O-Operationen blockiert
 */
@Service
@Slf4j
public class IOIntensiveService {

    private static final Logger log = LoggerFactory.getLogger(IOIntensiveService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${io.test.temp.dir:./io-test-temp}")
    private String tempDirPath;
    
    // Injiziere die executor beans direkt für die CompletableFuture Implementierungen
    private final Executor platformThreadTaskExecutor;
    private final Executor virtualThreadTaskExecutor;
    private final Executor limitedThreadTaskExecutor;
    private final Executor optimizedThreadTaskExecutor;
    
    @Autowired
    public IOIntensiveService(
            Executor platformThreadTaskExecutor,
            Executor virtualThreadTaskExecutor,
            Executor limitedThreadTaskExecutor,
            Executor optimizedThreadTaskExecutor) {
        this.platformThreadTaskExecutor = platformThreadTaskExecutor;
        this.virtualThreadTaskExecutor = virtualThreadTaskExecutor;
        this.limitedThreadTaskExecutor = limitedThreadTaskExecutor;
        this.optimizedThreadTaskExecutor = optimizedThreadTaskExecutor;
    }
    
    /**
     * Simuliert eine Reihe von HTTP-Anfragen, die typischerweise I/O-blockierend sind
     * Verwendet einen simulierten Delay, um nicht von externen Diensten abhängig zu sein
     */
    public List<String> performHttpRequests(int numberOfRequests) {
        List<String> results = new ArrayList<>();
        
        log.info("Starte {} HTTP-Anfragen auf Thread: {}", 
                numberOfRequests, Thread.currentThread().getName());
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfRequests; i++) {
            try {
                // Simuliere Netzwerk-Latenz statt echte HTTP-Anfragen zu machen
                Thread.sleep(300); // 300ms simulierte Netzwerklatenz (erhöht für bessere Unterschiede)
                String result = "HTTP Response #" + i + " - Thread: " + Thread.currentThread().getName();
                results.add(result);
            } catch (Exception e) {
                log.error("Fehler bei HTTP-Anfrage: {}", e.getMessage());
                results.add("Fehler: " + e.getMessage());
            }
        }
        
        long endTime = System.currentTimeMillis();
        log.info("HTTP-Anfragen abgeschlossen in {} ms auf Thread: {}", 
                (endTime - startTime), Thread.currentThread().getName());
        
        return results;
    }
    
    /**
     * Simuliert intensiven Dateizugriff
     * - Dateioperationen sind typische blockierende I/O-Operationen
     * - Zeigt Unterschiede in der Effizienz verschiedener Thread-Modelle
     */
    public List<Path> performFileOperations(int numberOfFiles, int fileSizeKB) {
        List<Path> filePaths = new ArrayList<>();
        
        log.info("Starte Dateioperationen: {} Dateien mit je {} KB auf Thread: {}", 
                numberOfFiles, fileSizeKB, Thread.currentThread().getName());
        
        // Stelle sicher, dass das temporäre Verzeichnis existiert
        Path tempDir = Paths.get(tempDirPath);
        try {
            Files.createDirectories(tempDir);
        } catch (IOException e) {
            log.error("Konnte temporäres Verzeichnis nicht erstellen: {}", e.getMessage());
            return filePaths;
        }
        
        long startTime = System.currentTimeMillis();
        
        // Erstelle zufällige Daten für die Dateien
        byte[] data = new byte[fileSizeKB * 1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (Math.random() * 256);
        }
        
        // Erstelle und lese Dateien
        for (int i = 0; i < numberOfFiles; i++) {
            try {
                // Erstelle Datei mit eindeutigem Namen
                Path filePath = tempDir.resolve("file-" + UUID.randomUUID() + ".dat");
                Files.write(filePath, data, StandardOpenOption.CREATE);
                filePaths.add(filePath);
                
                // Simuliere zusätzliche I/O-Latenz für deutlichere Unterschiede
                Thread.sleep(100);
                
                // Lese die Datei wieder ein
                byte[] readData = Files.readAllBytes(filePath);
                
                // Simuliere Verarbeitung
                Thread.sleep(50);
                
                // Lösche die temporäre Datei
                Files.delete(filePath);
            } catch (Exception e) {
                log.error("Fehler bei Dateioperationen: {}", e.getMessage());
            }
        }
        
        long endTime = System.currentTimeMillis();
        log.info("Dateioperationen abgeschlossen in {} ms auf Thread: {}", 
                (endTime - startTime), Thread.currentThread().getName());
        
        return filePaths;
    }
    
    /**
     * Führt einen gemischten I/O-Test durch, der sowohl Netzwerk- als auch Dateizugriffe enthält
     */
    public void performMixedIOTest(int operations, int fileSizeKB) {
        int httpRequests = operations / 2;
        int fileOperations = operations / 2;
        
        log.info("Starte gemischten I/O-Test mit {} HTTP-Anfragen und {} Dateioperationen", 
                httpRequests, fileOperations);
        
        // Führe synchron im aktuellen Thread aus (der bereits im jeweiligen ThreadPool läuft)
        performHttpRequests(httpRequests);
        performFileOperations(fileOperations, fileSizeKB);
        
        log.info("Gemischter I/O-Test abgeschlossen auf Thread: {}", Thread.currentThread().getName());
    }
    
    /**
     * Asynchrone Ausführung eines gemischten I/O-Tests mit Platform Threads
     * - Verwendet 1:1-Mapping zu OS-Threads (Standard-Java-Threads)
     * - Threads bleiben während blockierender I/O-Operationen aktiv
     */
    public CompletableFuture<Void> performMixedIOTestWithPlatformThreads(int operations, int fileSizeKB) {
        return CompletableFuture.runAsync(() -> performMixedIOTest(operations, fileSizeKB), platformThreadTaskExecutor);
    }
    
    /**
     * Asynchrone Ausführung eines gemischten I/O-Tests mit Virtual Threads
     * - Verwendet leichtgewichtige JVM-Threads, optimiert für I/O
     * - Echte in Java 21+, simuliert in älteren Versionen
     * - Beim Blockieren werden die Threads effizient pausiert
     */
    public CompletableFuture<Void> performMixedIOTestWithVirtualThreads(int operations, int fileSizeKB) {
        return CompletableFuture.runAsync(() -> performMixedIOTest(operations, fileSizeKB), virtualThreadTaskExecutor);
    }
    
    /**
     * Asynchrone Ausführung eines gemischten I/O-Tests mit begrenztem Thread-Pool
     * - Simuliert ressourcenbegrenzte Umgebung
     * - Zeigt Verhalten bei begrenzter Thread-Anzahl
     */
    public CompletableFuture<Void> performMixedIOTestWithLimitedThreads(int operations, int fileSizeKB) {
        return CompletableFuture.runAsync(() -> performMixedIOTest(operations, fileSizeKB), limitedThreadTaskExecutor);
    }
    
    /**
     * Asynchrone Ausführung eines gemischten I/O-Tests mit optimiertem Work-Stealing-Pool
     * - Verwendet optimiertes Scheduling für eine bessere Lastverteilung
     * - Threads können Arbeit von überlasteten Threads "stehlen"
     */
    public CompletableFuture<Void> performMixedIOTestWithOptimizedThreads(int operations, int fileSizeKB) {
        return CompletableFuture.runAsync(() -> performMixedIOTest(operations, fileSizeKB), optimizedThreadTaskExecutor);
    }
}