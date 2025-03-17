package de.haw.hamburg.threaddemo.controller;

import de.haw.hamburg.threaddemo.service.MatrixCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller zur Verwaltung der System-Auslastung
 */
@RestController
@RequestMapping("/api/system")
public class SystemLoadController {
    
    private final MatrixCalculationService calculationService;
    
    @Autowired
    public SystemLoadController(MatrixCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    /**
     * Aktiviert zusätzliche Belastung
     */
    @PostMapping("/heavy-load/start")
    public String startHeavyLoad() {
        calculationService.setHeavyLoadEnabled(true);
        calculationService.performHeavyLoadTask();
        return "Heavy load started.";
    }

    /**
     * Deaktiviert zusätzliche Belastung
     */
    @PostMapping("/heavy-load/stop")
    public String stopHeavyLoad() {
        calculationService.setHeavyLoadEnabled(false);
        return "Heavy load stopped.";
    }
}