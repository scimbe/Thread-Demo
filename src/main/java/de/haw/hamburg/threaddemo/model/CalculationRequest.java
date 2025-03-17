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
    
    // Explizite Getter und Setter für den Fall, dass Lombok nicht funktioniert
    public int getMatrixSize() {
        return matrixSize;
    }
    
    public void setMatrixSize(int matrixSize) {
        this.matrixSize = matrixSize;
    }
    
    public int getParallelTasks() {
        return parallelTasks;
    }
    
    public void setParallelTasks(int parallelTasks) {
        this.parallelTasks = parallelTasks;
    }
    
    public String getTestType() {
        return testType;
    }
    
    public void setTestType(String testType) {
        this.testType = testType;
    }
    
    public int getFileSizeKB() {
        return fileSizeKB;
    }
    
    public void setFileSizeKB(int fileSizeKB) {
        this.fileSizeKB = fileSizeKB;
    }
}