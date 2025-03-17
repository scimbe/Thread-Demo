package de.haw.hamburg.threaddemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Konfigurationsklasse für die verschiedenen Thread-Modelle
 * 
 * Diese Klasse definiert vier verschiedene Thread-Implementierungen, wobei eine klare
 * Unterscheidung zwischen JVM-Konzepten und Betriebssystemkonzepten gemacht wird:
 * 
 * 1. Platform Threads: JVM-Threads mit 1:1-Mapping zu Betriebssystem-Threads
 * 2. Virtual Threads: Leichtgewichtige, von der JVM verwaltete Threads (ab Java 21)
 * 3. Begrenzte Threads: Simulation der Ressourcenbeschränkungen ähnlich wie bei OS-Kernel-Threads
 * 4. Optimierte Threads: Work-Stealing-Pool für optimiertes Scheduling ähnlich zu User-Space-Threading
 */
@Configuration
public class ThreadConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ThreadConfig.class);

    /**
     * Standard Java Platform Thread-Pool mit fester Größe
     * - 1:1-Mapping zu Betriebssystem-Threads
     * - Schwere Threads mit eigenem Stack im Speicher
     * - Feste Poolgröße beschränkt die maximale Parallelität
     */
    @Bean(name = "platformThreadTaskExecutor")
    public Executor platformThreadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("platform-thread-");
        executor.initialize();
        return executor;
    }

    /**
     * Virtual Thread Executor - verwendet echte Virtual Threads in Java 21+, 
     * sonst einen simulierten unbegrenzten Pool
     * - Sehr leichtgewichtige Threads (nur in Java 21+)
     * - Carrier-Thread-Multiplexing (in Java 21+)
     * - Optimiert für blockierende Operationen
     * - Millionen von Threads möglich
     */
    @Bean(name = "virtualThreadTaskExecutor")
    public Executor virtualThreadTaskExecutor() {
        // Überprüfe, ob wir auf Java 21 oder höher laufen
        int majorVersion = getMajorJavaVersion();
        
        if (majorVersion >= 21) {
            // Java 21+: Echte Virtual Threads verwenden
            try {
                // Reflection, um Java 21 Features ohne direkten Import zu nutzen
                // Das verhindert Kompilierungsfehler in älteren Java-Versionen
                logger.info("Java 21+ erkannt: Verwende echte Virtual Threads");
                Class<?> executorsClass = Class.forName("java.util.concurrent.Executors");
                return (Executor) executorsClass.getMethod("newVirtualThreadPerTaskExecutor").invoke(null);
            } catch (Exception e) {
                logger.warn("Konnte keine echten Virtual Threads erstellen, obwohl Java 21+: {}", e.getMessage());
                logger.warn("Verwende stattdessen einen unbegrenzten Platform Thread-Pool zur Simulation...");
                return createSimulatedVirtualThreadPool();
            }
        } else {
            // Java < 21: Simulierten Virtual Thread-Pool verwenden
            logger.info("Java < 21 erkannt: Verwende simulierten Virtual Thread-Pool");
            return createSimulatedVirtualThreadPool();
        }
    }
    
    /**
     * Erstellt einen simulierten Virtual Thread-Pool (für Java < 21)
     * - Verwendet einen CachedThreadPool für dynamische Skalierung
     * - Echte Virtual Threads würden weniger Ressourcen verbrauchen
     * - Diese Simulation hat immer noch 1:1-Mapping zu OS-Threads
     */
    private Executor createSimulatedVirtualThreadPool() {
        return Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(false);  // Nicht-Daemon-Threads für konsistenteres Verhalten
            t.setName("virtual-thread-" + t.getId());
            return t;
        });
    }

    /**
     * Stark begrenzter Thread-Pool zur Simulation von Ressourcenbeschränkungen
     * 
     * HINWEIS: Dies simuliert nur die Ressourcenbeschränkung ähnlich zu Kernel-Threads,
     * ist aber technisch gesehen ein Pool aus Platform-Threads. In Java/JVM gibt es keine
     * direkten "Kernel-Threads" - alle Threads sind letztendlich OS-Threads.
     * 
     * - Begrenzt auf die Anzahl der CPU-Kerne
     * - Zeigt Verhalten bei starker Thread-Limitierung
     */
    @Bean(name = "limitedThreadTaskExecutor")
    public Executor limitedThreadTaskExecutor() {
        int coreCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreCount);
        executor.setMaxPoolSize(coreCount);
        executor.setQueueCapacity(50);  // Kleinere Queue für realistischere Limitierung
        executor.setThreadNamePrefix("limited-thread-");
        executor.setRejectedExecutionHandler((r, e) -> 
            logger.warn("Task abgelehnt aufgrund von Ressourcenbeschränkung in begrenztem Thread-Pool"));
        executor.initialize();
        logger.info("Begrenzter Thread-Pool erstellt mit {} Threads", coreCount);
        return executor;
    }

    /**
     * Optimierter Work-Stealing-Pool für effizientes Task-Scheduling
     * 
     * HINWEIS: Dies simuliert optimiertes Threading ähnlich zu User-Space-Threading-Ansätzen,
     * verwendet aber immer noch Platform-Threads (keine echten "User-Threads" im traditionellen Sinne).
     * 
     * - Verwendet einen ForkJoinPool mit Work-Stealing-Algorithmus
     * - Threads können Arbeit von überlasteten Threads "stehlen"
     * - Bessere Lastverteilung bei ungleichmäßigen Aufgaben
     */
    @Bean(name = "optimizedThreadTaskExecutor")
    public Executor optimizedThreadTaskExecutor() {
        int parallelism = Math.max(8, Runtime.getRuntime().availableProcessors() * 4);  // Höherer Parallelitätsgrad
        logger.info("Optimierter Thread-Pool erstellt mit Parallelitätsgrad {}", parallelism);
        return Executors.newWorkStealingPool(parallelism);
    }
    
    /**
     * Ermittelt die Java-Hauptversionsnummer
     */
    private int getMajorJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            // Alte Versionierung (1.6, 1.7, 1.8)
            return Integer.parseInt(version.substring(2, 3));
        } else {
            // Neue Versionierung (9, 10, 11, ...)
            int dot = version.indexOf('.');
            if (dot != -1) {
                return Integer.parseInt(version.substring(0, dot));
            }
            return Integer.parseInt(version);
        }
    }
}