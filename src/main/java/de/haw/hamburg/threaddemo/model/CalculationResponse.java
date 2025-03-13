package de.haw.hamburg.threaddemo.model;

import lombok.Builder;
import lombok.Data;

/**
 * DTO für die Antwort der Matrix-Multiplikation
 */
@Data
@Builder
public class CalculationResponse {
    /**
     * Name des verwendeten Thread-Modells
     */
    private String threadModel;
    
    /**
     * Größe der Matrizen
     */
    private int matrixSize;
    
    /**
     * Anzahl parallel ausgeführter Aufgaben
     */
    private int parallelTasks;
    
    /**
     * Gesamtzeit für die Ausführung in Millisekunden
     */
    private long totalExecutionTimeMs;
    
    /**
     * Durchschnittliche Zeit pro Aufgabe
     */
    public double getAverageTimePerTask() {
        return (double) totalExecutionTimeMs / parallelTasks;
    }
}
