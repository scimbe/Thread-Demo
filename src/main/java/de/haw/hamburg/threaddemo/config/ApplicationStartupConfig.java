package de.haw.hamburg.threaddemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurationsklasse für die Anwendungsinitialisierung
 * - Prüft Java-Version
 * - Gibt Warnungen aus, wenn nicht Java 21 oder höher verwendet wird
 */
@Configuration
public class ApplicationStartupConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupConfig.class);
    
    @Bean
    public CommandLineRunner checkJavaVersion() {
        return args -> {
            String javaVersion = System.getProperty("java.version");
            logger.info("Anwendung wird mit Java {} ausgeführt", javaVersion);
            
            int majorVersion = getMajorJavaVersion();
            
            if (majorVersion < 17) {
                logger.error("");
                logger.error("***************************************************************");
                logger.error("*                        FEHLER                               *");
                logger.error("*                                                             *");
                logger.error("* Die Anwendung benötigt mindestens Java 17!                 *");
                logger.error("* Aktuelle Java-Version: {}                                  *", javaVersion);
                logger.error("*                                                             *");
                logger.error("***************************************************************");
                logger.error("");
                
                // In einer produktiven Anwendung könnte hier die Anwendung beendet werden
                // System.exit(1);
            } else if (majorVersion < 21) {
                logger.warn("");
                logger.warn("***************************************************************");
                logger.warn("*                        WARNUNG                              *");
                logger.warn("*                                                             *");
                logger.warn("* Die Anwendung läuft mit Java {}, aber für optimale         *", javaVersion);
                logger.warn("* Leistung wird Java 21 oder höher empfohlen.                 *");
                logger.warn("* Mit Java 21 können echte Virtual Threads verwendet werden!  *");
                logger.warn("*                                                             *");
                logger.warn("* Aktuell werden simulierte Virtual Threads verwendet.        *");
                logger.warn("*                                                             *");
                logger.warn("***************************************************************");
                logger.warn("");
            } else {
                logger.info("");
                logger.info("***************************************************************");
                logger.info("*                                                             *");
                logger.info("* Java 21 oder höher erkannt! Echte Virtual Threads werden    *");
                logger.info("* für optimale Leistung verwendet.                            *");
                logger.info("*                                                             *");
                logger.info("***************************************************************");
                logger.info("");
            }
        };
    }
    
    /**
     * Ermittelt die Java-Hauptversionsnummer
     */
    private int getMajorJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            // Alte Versionierung (1.6, 1.7, 1.8)
            return Integer.parseInt(version.substring(2, 3));
        } else {
            // Neue Versionierung (9, 10, 11, ...)
            int dot = version.indexOf('.');
            if (dot != -1) {
                return Integer.parseInt(version.substring(0, dot));
            }
            return Integer.parseInt(version);
        }
    }
}
