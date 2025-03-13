package de.haw.hamburg.threaddemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Konfigurationsklasse für die verschiedenen Thread-Modelle
 */
@Configuration
public class ThreadConfig {

    /**
     * Standard Java Thread-Pool mit fester Größe (Platform Threads)
     */
    @Bean(name = "platformThreadTaskExecutor")
    public Executor platformThreadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("platform-thread-");
        executor.initialize();
        return executor;
    }

    /**
     * Unbegrenzter Thread-Pool mit virtuelle Threads (Java 21 Feature)
     */
    @Bean(name = "virtualThreadTaskExecutor")
    public Executor virtualThreadTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Kleiner Thread-Pool für Kernel-Thread Simulation
     * In Java gibt es keine direkten Kernel-Threads, aber wir können einen
     * sehr kleinen Pool verwenden, um das Verhalten zu simulieren
     */
    @Bean(name = "kernelThreadTaskExecutor")
    public Executor kernelThreadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Stark limitierte Threads
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(500); // Große Warteschlange
        executor.setThreadNamePrefix("kernel-thread-");
        executor.initialize();
        return executor;
    }

    /**
     * Thread-Pool mit vielen Threads für User-Thread Simulation
     */
    @Bean(name = "userThreadTaskExecutor")
    public Executor userThreadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50); // Viele Threads
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(50); // Kleinere Warteschlange
        executor.setThreadNamePrefix("user-thread-");
        executor.initialize();
        return executor;
    }
}
