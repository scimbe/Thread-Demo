package de.haw.hamburg.threaddemo.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller zum Abfragen von Thread-Informationen
 * Nützlich für die Visualisierung und Überwachung der verschiedenen Thread-Typen
 */
@RestController
@RequestMapping("/api/threads")
@RequiredArgsConstructor
public class ThreadInfoController {

    /**
     * Gibt aktuelle Thread-Informationen zurück
     */
    @GetMapping("/info")
    public ThreadSystemInfo getThreadInfo() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadSystemInfo info = new ThreadSystemInfo();
        
        info.setTotalStartedThreadCount(threadMXBean.getTotalStartedThreadCount());
        info.setCurrentThreadCount(threadMXBean.getThreadCount());
        info.setPeakThreadCount(threadMXBean.getPeakThreadCount());
        info.setDaemonThreadCount(threadMXBean.getDaemonThreadCount());
        
        // Aktuelle Thread-Details
        long[] threadIds = threadMXBean.getAllThreadIds();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadIds, 3);
        
        List<ThreadDetail> threadDetails = new ArrayList<>();
        for (ThreadInfo t : threadInfos) {
            if (t != null) {
                ThreadDetail detail = new ThreadDetail();
                detail.setId(t.getThreadId());
                detail.setName(t.getThreadName());
                detail.setState(t.getThreadState().name());
                detail.setBlocked(t.getThreadState() == Thread.State.WAITING || 
                               t.getThreadState() == Thread.State.TIMED_WAITING || 
                               t.getThreadState() == Thread.State.BLOCKED);
                detail.setThreadGroup(getThreadGroupName(t.getThreadName()));
                threadDetails.add(detail);
            }
        }
        
        info.setThreadDetails(threadDetails);
        
        // Thread-Gruppen-Statistik
        info.setThreadGroupStats(
            info.getThreadDetails().stream()
                .collect(Collectors.groupingBy(
                    ThreadDetail::getThreadGroup,
                    Collectors.counting()
                ))
        );
        
        return info;
    }
    
    /**
     * Ermittelt die Thread-Gruppe anhand des Namens
     */
    private String getThreadGroupName(String threadName) {
        if (threadName.startsWith("platform-thread-")) {
            return "Platform Threads";
        } else if (threadName.startsWith("VirtualThread") || threadName.startsWith("virtual-thread-")) {
            // Unterstützt sowohl echte Virtual Threads (Java 21) als auch unsere simulierten
            return "Virtual Threads";
        } else if (threadName.startsWith("kernel-thread-")) {
            return "Kernel Threads";
        } else if (threadName.startsWith("user-thread-")) {
            return "User Threads";
        } else if (threadName.startsWith("http-nio")) {
            return "Tomcat Threads";
        } else if (threadName.startsWith("ForkJoinPool")) {
            return "ForkJoin Pool";
        } else if (threadName.startsWith("pool-")) {
            // Dies könnte ein Thread aus unserem cached Thread-Pool sein (simulierte Virtual Threads)
            return "Virtual Threads";
        } else {
            return "Other";
        }
    }
    
    /**
     * DTO für Thread-System-Informationen
     */
    @Data
    public static class ThreadSystemInfo {
        private long totalStartedThreadCount;
        private int currentThreadCount;
        private int peakThreadCount;
        private int daemonThreadCount;
        private List<ThreadDetail> threadDetails = new ArrayList<>();
        private java.util.Map<String, Long> threadGroupStats;
        
        // Explizite Getter und Setter für den Fall, dass Lombok nicht funktioniert
        public long getTotalStartedThreadCount() {
            return totalStartedThreadCount;
        }
        
        public void setTotalStartedThreadCount(long totalStartedThreadCount) {
            this.totalStartedThreadCount = totalStartedThreadCount;
        }
        
        public int getCurrentThreadCount() {
            return currentThreadCount;
        }
        
        public void setCurrentThreadCount(int currentThreadCount) {
            this.currentThreadCount = currentThreadCount;
        }
        
        public int getPeakThreadCount() {
            return peakThreadCount;
        }
        
        public void setPeakThreadCount(int peakThreadCount) {
            this.peakThreadCount = peakThreadCount;
        }
        
        public int getDaemonThreadCount() {
            return daemonThreadCount;
        }
        
        public void setDaemonThreadCount(int daemonThreadCount) {
            this.daemonThreadCount = daemonThreadCount;
        }
        
        public List<ThreadDetail> getThreadDetails() {
            return threadDetails;
        }
        
        public void setThreadDetails(List<ThreadDetail> threadDetails) {
            this.threadDetails = threadDetails;
        }
        
        public java.util.Map<String, Long> getThreadGroupStats() {
            return threadGroupStats;
        }
        
        public void setThreadGroupStats(java.util.Map<String, Long> threadGroupStats) {
            this.threadGroupStats = threadGroupStats;
        }
    }
    
    /**
     * DTO für Thread-Details
     */
    @Data
    public static class ThreadDetail {
        private long id;
        private String name;
        private String state;
        private boolean blocked;
        private String threadGroup;
        
        // Explizite Getter für den Fall, dass Lombok nicht funktioniert
        public long getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getState() {
            return state;
        }
        
        public boolean isBlocked() {
            return blocked;
        }
        
        public String getThreadGroup() {
            return threadGroup;
        }
        
        // Explizite Setter für Lombok-Unabhängigkeit
        public void setId(long id) {
            this.id = id;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void setState(String state) {
            this.state = state;
        }
        
        public void setBlocked(boolean blocked) {
            this.blocked = blocked;
        }
        
        public void setThreadGroup(String threadGroup) {
            this.threadGroup = threadGroup;
        }
    }
}