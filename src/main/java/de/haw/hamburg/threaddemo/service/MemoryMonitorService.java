package de.haw.hamburg.threaddemo.service;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service zur Überwachung des Speicherverbrauchs
 */
@Service
public class MemoryMonitorService {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private AtomicReference<Double> peakMemoryUsage = new AtomicReference<>(0.0);
    
    /**
     * Startet eine neue Speicherüberwachung
     */
    public MemorySnapshot startMonitoring() {
        // Speicherbereinigung erzwingen für konsistentere Messungen
        System.gc();
        
        MemorySnapshot snapshot = new MemorySnapshot();
        snapshot.setHeapMemoryUsageMB(getHeapMemoryUsageMB());
        snapshot.setNonHeapMemoryUsageMB(getNonHeapMemoryUsageMB());
        snapshot.setTotalMemoryUsageMB(getTotalMemoryUsageMB());
        
        // Peak zurücksetzen
        peakMemoryUsage.set(snapshot.getTotalMemoryUsageMB());
        
        return snapshot;
    }
    
    /**
     * Aktualisiert den Peak-Wert der Speichernutzung
     */
    public void updatePeakMemory() {
        double currentMemory = getTotalMemoryUsageMB();
        peakMemoryUsage.getAndUpdate(peak -> Math.max(peak, currentMemory));
    }
    
    /**
     * Beendet die Speicherüberwachung und gibt den finalen Snapshot zurück
     */
    public MemorySnapshot stopMonitoring() {
        MemorySnapshot snapshot = new MemorySnapshot();
        snapshot.setHeapMemoryUsageMB(getHeapMemoryUsageMB());
        snapshot.setNonHeapMemoryUsageMB(getNonHeapMemoryUsageMB());
        snapshot.setTotalMemoryUsageMB(getTotalMemoryUsageMB());
        snapshot.setPeakMemoryUsageMB(peakMemoryUsage.get());
        
        return snapshot;
    }
    
    /**
     * Gibt die aktuelle Heap-Speichernutzung in MB zurück
     */
    private double getHeapMemoryUsageMB() {
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        return bytesToMB(heapMemoryUsage.getUsed());
    }
    
    /**
     * Gibt die aktuelle Non-Heap-Speichernutzung in MB zurück
     */
    private double getNonHeapMemoryUsageMB() {
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        return bytesToMB(nonHeapMemoryUsage.getUsed());
    }
    
    /**
     * Gibt die gesamte Speichernutzung (Heap + Non-Heap) in MB zurück
     */
    private double getTotalMemoryUsageMB() {
        return getHeapMemoryUsageMB() + getNonHeapMemoryUsageMB();
    }
    
    /**
     * Konvertiert Bytes in Megabytes
     */
    private double bytesToMB(long bytes) {
        return (double) bytes / (1024 * 1024);
    }
    
    /**
     * DTO für Speicher-Snapshots
     */
    @Data
    public static class MemorySnapshot {
        private double heapMemoryUsageMB;
        private double nonHeapMemoryUsageMB;
        private double totalMemoryUsageMB;
        private double peakMemoryUsageMB;
    }
}