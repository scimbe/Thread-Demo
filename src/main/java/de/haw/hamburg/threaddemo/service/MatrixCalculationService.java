package de.haw.hamburg.threaddemo.service;

import lombok.extern.slf4j.Slf4j;
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

    private final Random random = new Random();
    
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
     */
    @Async("platformThreadTaskExecutor")
    public CompletableFuture<double[][]> multiplyMatricesWithPlatformThreads(double[][] matrixA, double[][] matrixB) {
        return CompletableFuture.completedFuture(multiplyMatrices(matrixA, matrixB));
    }
    
    /**
     * Asynchrone Matrix-Multiplikation mit Virtual Threads
     */
    @Async("virtualThreadTaskExecutor")
    public CompletableFuture<double[][]> multiplyMatricesWithVirtualThreads(double[][] matrixA, double[][] matrixB) {
        return CompletableFuture.completedFuture(multiplyMatrices(matrixA, matrixB));
    }
    
    /**
     * Asynchrone Matrix-Multiplikation mit simulierten Kernel Threads
     */
    @Async("kernelThreadTaskExecutor")
    public CompletableFuture<double[][]> multiplyMatricesWithKernelThreads(double[][] matrixA, double[][] matrixB) {
        return CompletableFuture.completedFuture(multiplyMatrices(matrixA, matrixB));
    }
    
    /**
     * Asynchrone Matrix-Multiplikation mit simulierten User Threads
     */
    @Async("userThreadTaskExecutor")
    public CompletableFuture<double[][]> multiplyMatricesWithUserThreads(double[][] matrixA, double[][] matrixB) {
        return CompletableFuture.completedFuture(multiplyMatrices(matrixA, matrixB));
    }
}
