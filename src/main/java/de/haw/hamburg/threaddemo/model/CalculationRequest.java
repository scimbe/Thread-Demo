package de.haw.hamburg.threaddemo.model;

import lombok.Data;

/**
 * DTO für die Anfrage der Matrix-Multiplikation
 */
@Data
public class CalculationRequest {
    /**
     * Größe der zu multiplizierenden Matrizen
     */
    private int matrixSize;
    
    /**
     * Anzahl parallel auszuführender Aufgaben
     */
    private int parallelTasks;
}
