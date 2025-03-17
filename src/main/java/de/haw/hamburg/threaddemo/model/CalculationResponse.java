package de.haw.hamburg.threaddemo.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO für die Antwort der Thread-Tests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    // Builder-Methode
    public static CalculationResponseBuilder builder() {
        return new CalculationResponseBuilder();
    }
    
    // Builder-Klasse
    public static class CalculationResponseBuilder {
        private String threadModel;
        private String testType;
        private int matrixSize;
        private int fileSizeKB;
        private int parallelTasks;
        private long totalExecutionTimeMs;
        private double memoryBeforeMB;
        private double memoryPeakMB;
        private double memoryAfterMB;
        
        public CalculationResponseBuilder threadModel(String threadModel) {
            this.threadModel = threadModel;
            return this;
        }
        
        public CalculationResponseBuilder testType(String testType) {
            this.testType = testType;
            return this;
        }
        
        public CalculationResponseBuilder matrixSize(int matrixSize) {
            this.matrixSize = matrixSize;
            return this;
        }
        
        public CalculationResponseBuilder fileSizeKB(int fileSizeKB) {
            this.fileSizeKB = fileSizeKB;
            return this;
        }
        
        public CalculationResponseBuilder parallelTasks(int parallelTasks) {
            this.parallelTasks = parallelTasks;
            return this;
        }
        
        public CalculationResponseBuilder totalExecutionTimeMs(long totalExecutionTimeMs) {
            this.totalExecutionTimeMs = totalExecutionTimeMs;
            return this;
        }
        
        public CalculationResponseBuilder memoryBeforeMB(double memoryBeforeMB) {
            this.memoryBeforeMB = memoryBeforeMB;
            return this;
        }
        
        public CalculationResponseBuilder memoryPeakMB(double memoryPeakMB) {
            this.memoryPeakMB = memoryPeakMB;
            return this;
        }
        
        public CalculationResponseBuilder memoryAfterMB(double memoryAfterMB) {
            this.memoryAfterMB = memoryAfterMB;
            return this;
        }
        
        public CalculationResponse build() {
            CalculationResponse response = new CalculationResponse();
            response.threadModel = this.threadModel;
            response.testType = this.testType;
            response.matrixSize = this.matrixSize;
            response.fileSizeKB = this.fileSizeKB;
            response.parallelTasks = this.parallelTasks;
            response.totalExecutionTimeMs = this.totalExecutionTimeMs;
            response.memoryBeforeMB = this.memoryBeforeMB;
            response.memoryPeakMB = this.memoryPeakMB;
            response.memoryAfterMB = this.memoryAfterMB;
            return response;
        }
    }
    
    // Explizite Getter und Setter für den Fall, dass Lombok nicht funktioniert
    public String getThreadModel() {
        return threadModel;
    }
    
    public void setThreadModel(String threadModel) {
        this.threadModel = threadModel;
    }
    
    public String getTestType() {
        return testType;
    }
    
    public void setTestType(String testType) {
        this.testType = testType;
    }
    
    public int getMatrixSize() {
        return matrixSize;
    }
    
    public void setMatrixSize(int matrixSize) {
        this.matrixSize = matrixSize;
    }
    
    public int getFileSizeKB() {
        return fileSizeKB;
    }
    
    public void setFileSizeKB(int fileSizeKB) {
        this.fileSizeKB = fileSizeKB;
    }
    
    public int getParallelTasks() {
        return parallelTasks;
    }
    
    public void setParallelTasks(int parallelTasks) {
        this.parallelTasks = parallelTasks;
    }
    
    public long getTotalExecutionTimeMs() {
        return totalExecutionTimeMs;
    }
    
    public void setTotalExecutionTimeMs(long totalExecutionTimeMs) {
        this.totalExecutionTimeMs = totalExecutionTimeMs;
    }
    
    public double getMemoryBeforeMB() {
        return memoryBeforeMB;
    }
    
    public void setMemoryBeforeMB(double memoryBeforeMB) {
        this.memoryBeforeMB = memoryBeforeMB;
    }
    
    public double getMemoryPeakMB() {
        return memoryPeakMB;
    }
    
    public void setMemoryPeakMB(double memoryPeakMB) {
        this.memoryPeakMB = memoryPeakMB;
    }
    
    public double getMemoryAfterMB() {
        return memoryAfterMB;
    }
    
    public void setMemoryAfterMB(double memoryAfterMB) {
        this.memoryAfterMB = memoryAfterMB;
    }
}