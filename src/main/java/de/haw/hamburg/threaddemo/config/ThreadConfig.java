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
 */
@Configuration
public class ThreadConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ThreadConfig.class);

    /**
     * Standard Java Thread-Pool mit fester Größe (Platform Threads)
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
     * Virtueller Thread-Pool - verwendet echte Virtual Threads in Java 21+, 
     * sonst einen simulierten unbegrenzten Pool
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
                Class<?> executorsClass = Class.forName("java.util.concurrent.Executors");
                return (Executor) executorsClass.getMethod("newVirtualThreadPerTaskExecutor").invoke(null);
            } catch (Exception e) {
                logger.warn("Konnte keine echten Virtual Threads erstellen, obwohl Java 21+: {}", e.getMessage());
                logger.warn("Verwende stattdessen einen unbegrenzten Thread-Pool...");
                return createSimulatedVirtualThreadPool();
            }
        } else {
            // Java < 21: Simulierten Virtual Thread-Pool verwenden
            logger.info("Java < 21 erkannt, verwende simulierten Virtual Thread-Pool");
            return createSimulatedVirtualThreadPool();
        }
    }
    
    /**
     * Erstellt einen simulierten Virtual Thread-Pool (für Java < 21)
     */
    private Executor createSimulatedVirtualThreadPool() {
        return Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setName("virtual-thread-" + t.getId());
            return t;
        });
    }

    /**
     * Kleiner Thread-Pool für Kernel-Thread Simulation
     * In Java gibt es keine direkten Kernel-Threads, aber wir können einen
     * sehr kleinen Pool verwenden, um das Verhalten zu simulieren
     */
    @Bean(name = "kernelThreadTaskExecutor")
    public Executor kernelThreadTaskExecutor() {
        int coreCount = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(coreCount);
    }

    /**
     * Thread-Pool mit vielen Threads für User-Thread Simulation
     */

    //    1. Work-Stealing-Mechanismus ahmt das Verhalten vieler User-Threads nach
	//        •	Jeder User-Thread in einem System führt in der Regel unterschiedliche Aufgaben aus, die nicht immer gleichmäßig verteilt sind.
    //        •	Der Work-Stealing-Pool sorgt dafür, dass Threads, die nichts zu tun haben, aktive Aufgaben von anderen Threads übernehmen können.
     //       •	Dadurch werden CPU-Kerne optimal ausgelastet, ähnlich wie es in einem System mit vielen User-Threads der Fall wäre.
    @Bean(name = "userThreadTaskExecutor")
    public Executor userThreadTaskExecutor() {
        return Executors.newWorkStealingPool(50);
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
