package de.haw.hamburg.threaddemo.model;

import lombok.Builder;
import lombok.Data;

/**
 * DTO für die Antwort der Thread-Tests
 */
@Data
@Builder
public class CalculationResponse {
    /**
     * Name des verwendeten Thread-Modells
     */
    private String threadModel;
    
    /**
     * Art des Tests: "cpu" für Matrix-Multiplikation, "io" für I/O-intensive Tests
     */
    private String testType;
    
    /**
     * Größe der Matrizen (für CPU-Tests)
     */
    private int matrixSize;
    
    /**
     * Dateigröße in KB (für I/O-Tests)
     */
    private int fileSizeKB;
    
    /**
     * Anzahl parallel ausgeführter Aufgaben
     */
    private int parallelTasks;
    
    /**
     * Gesamtzeit für die Ausführung in Millisekunden
     */
    private long totalExecutionTimeMs;
    
    /**
     * Speichernutzung vor dem Test (MB)
     */
    private double memoryBeforeMB;
    
    /**
     * Speichernutzung während des Tests (Peak) (MB)
     */
    private double memoryPeakMB;
    
    /**
     * Speichernutzung nach dem Test (MB)
     */
    private double memoryAfterMB;
    
    /**
     * Durchschnittliche Zeit pro Aufgabe
     */
    public double getAverageTimePerTask() {
        return (double) totalExecutionTimeMs / parallelTasks;
    }
    
    /**
     * Speicherverbrauch während des Tests (MB)
     */
    public double getMemoryUsageMB() {
        return memoryPeakMB - memoryBeforeMB;
    }
}