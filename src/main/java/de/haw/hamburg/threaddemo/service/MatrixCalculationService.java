package de.haw.hamburg.threaddemo.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Service-Klasse, die eine rechenintensive Matrix-Multiplikation durchführt
 * Diese Operation eignet sich gut, um die Unterschiede zwischen Thread-Modellen zu zeigen
 */
@Service
@Slf4j
public class MatrixCalculationService {

    private static final Logger log = LoggerFactory.getLogger(MatrixCalculationService.class);
    private final Random random = new Random();
    private volatile boolean isHeavyLoadEnabled = false;

    /**
     * Aktiviert oder deaktiviert die Zusatzbelastung
     */
    public void setHeavyLoadEnabled(boolean isEnabled) {
        this.isHeavyLoadEnabled = isEnabled;
    }

    /**
     * Simuliert eine schwere Berechnungslast (z. B. Primzahlenberechnung)
     */
    @Async("platformThreadTaskExecutor") // Kann auch andere Executoren verwenden
    public void performHeavyLoadTask() {
        while (isHeavyLoadEnabled) {
            calculateHeavyTask();
        }
    }

    /**
     * Führt beispielsweise eine Primzahlenberechnung aus
     */
    private void calculateHeavyTask() {
        long primeCount = 0;
        for (long i = 1; i < 1_000_000; i++) {
            if (isPrime(i)) {
                primeCount++;
            }
        }
        log.info("Prime numbers calculated: {}", primeCount);
    }

    /**
     * Prüft, ob eine Zahl eine Primzahl ist
     */
    private boolean isPrime(long number) {
        return calculate(number);
    }

    public static boolean calculate(long number) {
        if (number <= 1) {
            return false;
        }
        for (long i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Erstellt eine zufällige Matrix mit der angegebenen Größe
     */
    public double[][] createRandomMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }
        return matrix;
    }
    
    /**
     * Führt eine rechenintensive Matrix-Multiplikation durch
     */
    public double[][] multiplyMatrices(double[][] matrixA, double[][] matrixB) {
        int size = matrixA.length;
        double[][] result = new double[size][size];
        
        log.info("Starting matrix multiplication with size {} on thread: {}", 
                size, Thread.currentThread().getName());
        
        long startTime = System.currentTimeMillis();
        
        // Führe Matrix-Multiplikation durch
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[i][j] = 0;
                for (int k = 0; k < size; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        
        long endTime = System.currentTimeMillis();
        log.info("Matrix multiplication completed in {} ms on thread: {}", 
                (endTime - startTime), Thread.currentThread().getName());
        
        return result;
    }
    
    /**
     * Asynchrone Matrix-Multiplikation mit Platform Threads
     * Verwendet 1:1-Mapping zu OS-Threads (Standard-Java-Threads)
     */
    @Async("platformThreadTaskExecutor")
    public CompletableFuture<double[][]> multiplyMatricesWithPlatformThreads(double[][] matrixA, double[][] matrixB) {
        return CompletableFuture.completedFuture(multiplyMatrices(matrixA, matrixB));
    }
    
    /**
     * Asynchrone Matrix-Multiplikation mit Virtual Threads
     * Verwendet leichtgewichtige JVM-Threads (echte in Java 21+, simuliert in älteren Versionen)
     */
    @Async("virtualThreadTaskExecutor")
    public CompletableFuture<double[][]> multiplyMatricesWithVirtualThreads(double[][] matrixA, double[][] matrixB) {
        return CompletableFuture.completedFuture(multiplyMatrices(matrixA, matrixB));
    }
    
    /**
     * Asynchrone Matrix-Multiplikation mit begrenztem Thread-Pool
     * Simuliert ressourcenbegrenzte Umgebung, ähnlich zu OS-Kernel-Thread-Limitierungen
     */
    @Async("limitedThreadTaskExecutor")
    public CompletableFuture<double[][]> multiplyMatricesWithLimitedThreads(double[][] matrixA, double[][] matrixB) {
        return CompletableFuture.completedFuture(multiplyMatrices(matrixA, matrixB));
    }
    
    /**
     * Asynchrone Matrix-Multiplikation mit optimiertem Work-Stealing-Pool
     * Verwendet optimiertes Scheduling für eine bessere Lastverteilung
     */
    @Async("optimizedThreadTaskExecutor")
    public CompletableFuture<double[][]> multiplyMatricesWithOptimizedThreads(double[][] matrixA, double[][] matrixB) {
        return CompletableFuture.completedFuture(multiplyMatrices(matrixA, matrixB));
    }
}