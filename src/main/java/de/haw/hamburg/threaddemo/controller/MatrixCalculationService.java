

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service("matrixCalculationService")
@Slf4j
public class MatrixCalculationService {

    private final Random random = new Random();
    private volatile boolean isHeavyLoadEnabled = false;

    // Existierende Methoden bleiben unverändert

    /**
     * Aktiviert oder deaktiviert die Zusatzbelastung
     */
    public void setHeavyLoadEnabled(boolean isEnabled) {
        this.isHeavyLoadEnabled = isEnabled;
    }

    /**
     * Simuliert eine schwere Berechnungslast (z. B. Primzahlenberechnung)
     */
    @Async("platformThreadTaskExecutor") // Kann auch andere Executoren verwenden
    public void performHeavyLoadTask() {
        while (isHeavyLoadEnabled) {
            calculateHeavyTask();
        }
    }

    /**
     * Führt beispielsweise eine Primzahlenberechnung aus
     */
    private void calculateHeavyTask() {
        long primeCount = 0;
        for (long i = 1; i < 1_000_000; i++) {
            if (isPrime(i)) {
                primeCount++;
            }
        }
        log.info("Prime numbers calculated: {}", primeCount);
    }

    /**
     * Prüft, ob eine Zahl eine Primzahl ist
     */
    private boolean isPrime(long number) {
        if (number <= 1) {
            return false;
        }
        for (long i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}