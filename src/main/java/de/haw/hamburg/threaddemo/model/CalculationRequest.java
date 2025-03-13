package de.haw.hamburg.threaddemo.model;

import lombok.Data;

/**
 * DTO für die Anfrage der Thread-Tests
 */
@Data
public class CalculationRequest {
    /**
     * Größe der zu multiplizierenden Matrizen (für CPU-Tests)
     */
    private int matrixSize;
    
    /**
     * Anzahl parallel auszuführender Aufgaben
     */
    private int parallelTasks;
    
    /**
     * Art des Tests: "cpu" für Matrix-Multiplikation, "io" für I/O-intensive Tests
     */
    private String testType = "cpu";
    
    /**
     * Dateigröße in KB für I/O-Tests
     */
    private int fileSizeKB = 100;
}