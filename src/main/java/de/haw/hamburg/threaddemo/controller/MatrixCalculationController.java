package de.haw.hamburg.threaddemo.controller;

import de.haw.hamburg.threaddemo.model.CalculationRequest;
import de.haw.hamburg.threaddemo.model.CalculationResponse;
import de.haw.hamburg.threaddemo.service.MatrixCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/matrix")
@RequiredArgsConstructor
@Slf4j
public class MatrixCalculationController {

    private final MatrixCalculationService calculationService;

    /**
     * Führt Matrix-Multiplikation mit Standard-Plattform-Threads durch
     */
    @PostMapping("/platform-threads")
    public CalculationResponse calculateWithPlatformThreads(@RequestBody CalculationRequest request) {
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<double[][]>> futures = new ArrayList<>();
        for (int i = 0; i < request.getParallelTasks(); i++) {
            double[][] matrixA = calculationService.createRandomMatrix(request.getMatrixSize());
            double[][] matrixB = calculationService.createRandomMatrix(request.getMatrixSize());
            futures.add(calculationService.multiplyMatricesWithPlatformThreads(matrixA, matrixB));
        }
        
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during platform thread calculation", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        return CalculationResponse.builder()
                .threadModel("Platform Threads")
                .matrixSize(request.getMatrixSize())
                .parallelTasks(request.getParallelTasks())
                .totalExecutionTimeMs(endTime - startTime)
                .build();
    }

    /**
     * Führt Matrix-Multiplikation mit virtuellen Threads durch
     */
    @PostMapping("/virtual-threads")
    public CalculationResponse calculateWithVirtualThreads(@RequestBody CalculationRequest request) {
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<double[][]>> futures = new ArrayList<>();
        for (int i = 0; i < request.getParallelTasks(); i++) {
            double[][] matrixA = calculationService.createRandomMatrix(request.getMatrixSize());
            double[][] matrixB = calculationService.createRandomMatrix(request.getMatrixSize());
            futures.add(calculationService.multiplyMatricesWithVirtualThreads(matrixA, matrixB));
        }
        
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during virtual thread calculation", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        return CalculationResponse.builder()
                .threadModel("Virtual Threads (Loom)")
                .matrixSize(request.getMatrixSize())
                .parallelTasks(request.getParallelTasks())
                .totalExecutionTimeMs(endTime - startTime)
                .build();
    }

    /**
     * Führt Matrix-Multiplikation mit simulierten Kernel-Threads durch
     */
    @PostMapping("/kernel-threads")
    public CalculationResponse calculateWithKernelThreads(@RequestBody CalculationRequest request) {
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<double[][]>> futures = new ArrayList<>();
        for (int i = 0; i < request.getParallelTasks(); i++) {
            double[][] matrixA = calculationService.createRandomMatrix(request.getMatrixSize());
            double[][] matrixB = calculationService.createRandomMatrix(request.getMatrixSize());
            futures.add(calculationService.multiplyMatricesWithKernelThreads(matrixA, matrixB));
        }
        
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during kernel thread calculation", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        return CalculationResponse.builder()
                .threadModel("Kernel Threads (Simulation)")
                .matrixSize(request.getMatrixSize())
                .parallelTasks(request.getParallelTasks())
                .totalExecutionTimeMs(endTime - startTime)
                .build();
    }

    /**
     * Führt Matrix-Multiplikation mit simulierten User-Threads durch
     */
    @PostMapping("/user-threads")
    public CalculationResponse calculateWithUserThreads(@RequestBody CalculationRequest request) {
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<double[][]>> futures = new ArrayList<>();
        for (int i = 0; i < request.getParallelTasks(); i++) {
            double[][] matrixA = calculationService.createRandomMatrix(request.getMatrixSize());
            double[][] matrixB = calculationService.createRandomMatrix(request.getMatrixSize());
            futures.add(calculationService.multiplyMatricesWithUserThreads(matrixA, matrixB));
        }
        
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during user thread calculation", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        return CalculationResponse.builder()
                .threadModel("User Threads (Simulation)")
                .matrixSize(request.getMatrixSize())
                .parallelTasks(request.getParallelTasks())
                .totalExecutionTimeMs(endTime - startTime)
                .build();
    }
    
    /**
     * Endpoint zum Testen aller Thread-Modelle gleichzeitig
     */
    @PostMapping("/compare-all")
    public List<CalculationResponse> compareAllThreadModels(@RequestBody CalculationRequest request) {
        List<CalculationResponse> results = new ArrayList<>();
        
        // Teste alle Thread-Modelle nacheinander
        results.add(calculateWithPlatformThreads(request));
        results.add(calculateWithVirtualThreads(request));
        results.add(calculateWithKernelThreads(request));
        results.add(calculateWithUserThreads(request));
        
        return results;
    }
}
