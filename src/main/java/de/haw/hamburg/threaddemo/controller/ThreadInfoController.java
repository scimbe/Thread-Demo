package de.haw.hamburg.threaddemo.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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
        info.setThreadDetails(
            Arrays.stream(threadInfos)
                .filter(t -> t != null)
                .map(t -> new ThreadDetail(
                    t.getThreadId(),
                    t.getThreadName(),
                    t.getThreadState().name(),
                    t.getThreadState() == Thread.State.WAITING || 
                    t.getThreadState() == Thread.State.TIMED_WAITING || 
                    t.getThreadState() == Thread.State.BLOCKED,
                    getThreadGroupName(t.getThreadName())
                ))
                .collect(Collectors.toList())
        );
        
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
    }
    
    /**
     * DTO für Thread-Details
     */
    @Data
    public static class ThreadDetail {
        private final long id;
        private final String name;
        private final String state;
        private final boolean blocked;
        private final String threadGroup;
    }
}
