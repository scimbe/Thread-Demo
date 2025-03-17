package de.haw.hamburg.threaddemo.controller;

import de.haw.hamburg.threaddemo.model.CalculationRequest;
import de.haw.hamburg.threaddemo.model.CalculationResponse;
import de.haw.hamburg.threaddemo.service.IOIntensiveService;
import de.haw.hamburg.threaddemo.service.MatrixCalculationService;
import de.haw.hamburg.threaddemo.service.MemoryMonitorService;
import de.haw.hamburg.threaddemo.service.MemoryMonitorService.MemorySnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Controller für die Durchführung von Thread-Modell-Vergleichstests
 * - Stellt Endpunkte für verschiedene Thread-Implementierungen bereit
 * - Unterstützt sowohl CPU-intensive als auch I/O-intensive Tests
 * - Erfasst Ausführungszeiten und Speicherverbrauch
 */
@RestController
@RequestMapping("/api/matrix")
public class MatrixCalculationController {

    private static final Logger log = LoggerFactory.getLogger(MatrixCalculationController.class);
    private final MatrixCalculationService calculationService;
    private final IOIntensiveService ioService;
    private final MemoryMonitorService memoryMonitorService;
    
    @Autowired
    public MatrixCalculationController(
            MatrixCalculationService calculationService,
            IOIntensiveService ioService,
            MemoryMonitorService memoryMonitorService) {
        this.calculationService = calculationService;
        this.ioService = ioService;
        this.memoryMonitorService = memoryMonitorService;
    }
    
    /**
     * Führt Tests mit Standard Java Platform Threads durch
     * - 1:1-Mapping zu Betriebssystem-Threads
     * - Begrenzte Anzahl (durch Pool-Größe)
     * - Vergleichsweise höherer Speicherverbrauch
     */
    @PostMapping("/platform-threads")
    public CalculationResponse calculateWithPlatformThreads(@RequestBody CalculationRequest request) {
        // Starte Speicherüberwachung
        MemorySnapshot memoryBefore = memoryMonitorService.startMonitoring();
        ScheduledExecutorService memoryMonitor = startMemoryMonitoring();
        
        long startTime = System.currentTimeMillis();
        
        if ("cpu".equalsIgnoreCase(request.getTestType())) {
            // CPU-intensiver Test (Matrix-Multiplikation)
            executeCPUTest("platformThreadTaskExecutor", request);
        } else {
            // I/O-intensiver Test
            executeIOTest("platformThreadTaskExecutor", request);
        }
        
        long endTime = System.currentTimeMillis();
        
        // Beende Speicherüberwachung
        memoryMonitor.shutdown();
        MemorySnapshot memoryAfter = memoryMonitorService.stopMonitoring();
        
        return buildResponse(request, "Platform Threads (1:1 OS-Mapping)", startTime, endTime, 
                            memoryBefore, memoryAfter);
    }

    /**
     * Führt Tests mit Virtual Threads durch
     * - In Java 21+: Echte leichtgewichtige Virtual Threads 
     * - In Java <21: Simulierte unbegrenzte Threads
     * - Bessere Skalierbarkeit, besonders bei I/O-intensiven Aufgaben
     */
    @PostMapping("/virtual-threads")
    public CalculationResponse calculateWithVirtualThreads(@RequestBody CalculationRequest request) {
        // Starte Speicherüberwachung
        MemorySnapshot memoryBefore = memoryMonitorService.startMonitoring();
        ScheduledExecutorService memoryMonitor = startMemoryMonitoring();
        
        long startTime = System.currentTimeMillis();
        
        if ("cpu".equalsIgnoreCase(request.getTestType())) {
            // CPU-intensiver Test (Matrix-Multiplikation)
            executeCPUTest("virtualThreadTaskExecutor", request);
        } else {
            // I/O-intensiver Test
            executeIOTest("virtualThreadTaskExecutor", request);
        }
        
        long endTime = System.currentTimeMillis();
        
        // Beende Speicherüberwachung
        memoryMonitor.shutdown();
        MemorySnapshot memoryAfter = memoryMonitorService.stopMonitoring();
        
        return buildResponse(request, "Virtual Threads (JVM-optimiert)", startTime, endTime, 
                            memoryBefore, memoryAfter);
    }

    /**
     * Führt Tests mit stark begrenztem Thread-Pool durch
     * - Simuliert Ressourcenbegrenzungen ähnlich zu OS-Kernel-Limits
     * - Limitiert auf Anzahl der CPU-Kerne
     * - Zeigt Verhalten bei Thread-Knappheit
     */
    @PostMapping("/limited-threads")
    public CalculationResponse calculateWithLimitedThreads(@RequestBody CalculationRequest request) {
        // Starte Speicherüberwachung
        MemorySnapshot memoryBefore = memoryMonitorService.startMonitoring();
        ScheduledExecutorService memoryMonitor = startMemoryMonitoring();
        
        long startTime = System.currentTimeMillis();
        
        if ("cpu".equalsIgnoreCase(request.getTestType())) {
            // CPU-intensiver Test (Matrix-Multiplikation)
            executeCPUTest("limitedThreadTaskExecutor", request);
        } else {
            // I/O-intensiver Test
            executeIOTest("limitedThreadTaskExecutor", request);
        }
        
        long endTime = System.currentTimeMillis();
        
        // Beende Speicherüberwachung
        memoryMonitor.shutdown();
        MemorySnapshot memoryAfter = memoryMonitorService.stopMonitoring();
        
        return buildResponse(request, "Begrenzte Threads (CPU-Core-limitiert)", startTime, endTime, 
                            memoryBefore, memoryAfter);
    }

    /**
     * Führt Tests mit optimiertem Work-Stealing-Pool durch
     * - Verwendet ForkJoinPool für besseres Scheduling
     * - Optimiert für ungleichmäßige Aufgabenverteilung
     * - Ähnlich zu modernen User-Space-Threading-Konzepten
     */
    @PostMapping("/optimized-threads")
    public CalculationResponse calculateWithOptimizedThreads(@RequestBody CalculationRequest request) {
        // Starte Speicherüberwachung
        MemorySnapshot memoryBefore = memoryMonitorService.startMonitoring();
        ScheduledExecutorService memoryMonitor = startMemoryMonitoring();
        
        long startTime = System.currentTimeMillis();
        
        if ("cpu".equalsIgnoreCase(request.getTestType())) {
            // CPU-intensiver Test (Matrix-Multiplikation)
            executeCPUTest("optimizedThreadTaskExecutor", request);
        } else {
            // I/O-intensiver Test
            executeIOTest("optimizedThreadTaskExecutor", request);
        }
        
        long endTime = System.currentTimeMillis();
        
        // Beende Speicherüberwachung
        memoryMonitor.shutdown();
        MemorySnapshot memoryAfter = memoryMonitorService.stopMonitoring();
        
        return buildResponse(request, "Optimierte Threads (Work-Stealing)", startTime, endTime, 
                            memoryBefore, memoryAfter);
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
        results.add(calculateWithLimitedThreads(request));
        results.add(calculateWithOptimizedThreads(request));
        
        return results;
    }
    
    /**
     * Führt einen CPU-intensiven Test durch
     */
    private void executeCPUTest(String executorName, CalculationRequest request) {
        List<CompletableFuture<double[][]>> futures = new ArrayList<>();
        
        for (int i = 0; i < request.getParallelTasks(); i++) {
            double[][] matrixA = calculationService.createRandomMatrix(request.getMatrixSize());
            double[][] matrixB = calculationService.createRandomMatrix(request.getMatrixSize());
            
            CompletableFuture<double[][]> future = null;
            switch (executorName) {
                case "platformThreadTaskExecutor":
                    future = calculationService.multiplyMatricesWithPlatformThreads(matrixA, matrixB);
                    break;
                case "virtualThreadTaskExecutor":
                    future = calculationService.multiplyMatricesWithVirtualThreads(matrixA, matrixB);
                    break;
                case "limitedThreadTaskExecutor":
                    future = calculationService.multiplyMatricesWithLimitedThreads(matrixA, matrixB);
                    break;
                case "optimizedThreadTaskExecutor":
                    future = calculationService.multiplyMatricesWithOptimizedThreads(matrixA, matrixB);
                    break;
            }
            
            if (future != null) {
                futures.add(future);
            }
        }
        
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during CPU test execution", e);
        }
    }
    
    /**
     * Führt einen I/O-intensiven Test durch
     */
    private void executeIOTest(String executorName, CalculationRequest request) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < request.getParallelTasks(); i++) {
            CompletableFuture<Void> future = null;
            switch (executorName) {
                case "platformThreadTaskExecutor":
                    future = ioService.performMixedIOTestWithPlatformThreads(5, request.getFileSizeKB());
                    break;
                case "virtualThreadTaskExecutor":
                    future = ioService.performMixedIOTestWithVirtualThreads(5, request.getFileSizeKB());
                    break;
                case "limitedThreadTaskExecutor":
                    future = ioService.performMixedIOTestWithLimitedThreads(5, request.getFileSizeKB());
                    break;
                case "optimizedThreadTaskExecutor":
                    future = ioService.performMixedIOTestWithOptimizedThreads(5, request.getFileSizeKB());
                    break;
            }
            
            if (future != null) {
                futures.add(future);
            }
        }
        
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during I/O test execution", e);
        }
    }
    
    /**
     * Startet die Speicherüberwachung in einem separaten Thread
     */
    private ScheduledExecutorService startMemoryMonitoring() {
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(
            () -> memoryMonitorService.updatePeakMemory(),
            100, 100, TimeUnit.MILLISECONDS
        );
        return scheduler;
    }
    
    /**
     * Erstellt eine Response mit den Ergebnissen des Tests
     */
    private CalculationResponse buildResponse(
            CalculationRequest request, 
            String threadModel, 
            long startTime, 
            long endTime, 
            MemorySnapshot memoryBefore, 
            MemorySnapshot memoryAfter) {
        
        return CalculationResponse.builder()
                .threadModel(threadModel)
                .testType(request.getTestType())
                .matrixSize(request.getMatrixSize())
                .fileSizeKB(request.getFileSizeKB())
                .parallelTasks(request.getParallelTasks())
                .totalExecutionTimeMs(endTime - startTime)
                .memoryBeforeMB(memoryBefore.getTotalMemoryUsageMB())
                .memoryPeakMB(memoryAfter.getPeakMemoryUsageMB())
                .memoryAfterMB(memoryAfter.getTotalMemoryUsageMB())
                .build();
    }
}