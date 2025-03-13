# Thread-Demo: Vergleich von Thread-Modellen für REST-Anfragen

## Übersicht

Dieses Projekt demonstriert die Unterschiede zwischen verschiedenen Thread-Implementierungen bei der Verarbeitung von REST-Anfragen in Java. Es zeigt die Performanceunterschiede und Eigenschaften von Platform Threads, Virtual Threads, Kernel Threads und User Threads (Falls unterstützt, sonst nach besten Wissen simuliert) anhand einer rechenintensiven Matrix-Multiplikation.

## Java-Kompatibilität

Das Projekt wurde angepasst, um mit Java 17 oder höher zu arbeiten:
- Für **Java 21** nutzt es echte Virtual Threads über `Executors.newVirtualThreadPerTaskExecutor()`
- Für **Java 17-20** verwendet es einen unbegrenzten Thread-Pool, der Virtual Threads simuliert

## Thread-Modelle im Vergleich

Das Projekt implementiert vier Thread-Modelle:

### 1. Platform Threads (1:1 Mapping)
- Traditionelle Java-Threads mit 1:1-Mapping zu Betriebssystem-Threads
- Jeder Thread benötigt eigenen Stack (typischerweise ~1MB Speicher)
- Skalierungsprobleme bei vielen parallel laufenden Anfragen
- Implementiert durch Standard-Thread-Pool mit fester Größe

### 2. Virtual Threads (simuliert oder echt, je nach Java-Version)
- In Java 21: Echte Virtual Threads (Project Loom)
- In Java 17-20: Simuliert durch einen unbegrenzten Thread-Pool
- Leichtgewichtig mit besserer Skalierbarkeit als klassische Threads
- Ideal für I/O-intensive Anwendungen

### 3. Kernel Threads (Simulation)
- Simulation durch stark begrenzten Thread-Pool (2-4 Threads)
- Im echten Betriebssystem direkt vom Kernel verwaltet
- Ressourcenintensiv, aber direkt vom Betriebssystem-Scheduler verwaltet
- Begrenzte Anzahl verfügbar
- Implementiert durch Thread-Pool mit sehr kleiner Größe zur Simulation der Begrenzung

### 4. User Threads (Simulation)
- Simulation durch großen Thread-Pool (50-100 Threads)
- Im echten Betriebssystem im User-Space verwaltet
- Leichtgewichtig, aber meist kooperatives Multitasking
- Implementiert durch Thread-Pool mit vielen Threads und kleiner Warteschlange

## Demonstrierter Anwendungsfall

Der Demonstrationsfall ist eine rechenintensive Matrix-Multiplikation:

1. Der Benutzer kann Matrixgröße und Anzahl paralleler Aufgaben konfigurieren
2. Das System startet die angegebene Anzahl paralleler Matrix-Berechnungen
3. Jede Thread-Implementierung verarbeitet die Anfragen unterschiedlich
4. Die Anwendung misst Gesamtausführungszeit und Durchsatz
5. Ergebnisse werden grafisch visualisiert

## Technische Details

### Implementierung

- Die verschiedenen Thread-Modelle werden durch unterschiedliche `Executor`-Implementierungen umgesetzt
- Jedes Thread-Modell hat seinen eigenen REST-Endpunkt
- Metriken werden gesammelt und über eine interaktive Benutzeroberfläche präsentiert
- Ein Monitor zeigt Live-Informationen über Thread-Zustände und Gruppenverteilung

### Komponenten

- **ThreadConfig**: Konfiguriert die verschiedenen Thread-Pools
- **MatrixCalculationService**: Implementiert die rechenintensive Matrix-Multiplikation
- **MatrixCalculationController**: Stellt REST-Endpoints für die verschiedenen Thread-Modelle bereit
- **ThreadInfoController**: Liefert Informationen über aktuelle Thread-Zustände
- **Web-Interface**: Visuelle Darstellung der Ergebnisse und Thread-Monitor

## Benutzeroberfläche

Die Anwendung bietet zwei Hauptseiten:

1. **Hauptseite** (index.html):
   - Konfiguration der Matrix-Größe und paralleler Aufgaben
   - Buttons zum Testen einzelner oder aller Thread-Modelle
   - Ergebnisdarstellung mit Ausführungszeiten
   - Grafischer Vergleich mit Balkendiagrammen

2. **Thread-Monitor** (thread-monitor.html):
   - Echtzeit-Anzeige aller aktiven Threads
   - Verteilung nach Thread-Gruppen
   - Verteilung nach Thread-Status
   - Filterung nach Thread-Typ und Status

## Erwartete Ergebnisse

Je nach Testkonfiguration werden unterschiedliche Thread-Modelle Vorteile zeigen:

- Bei **wenigen parallelen Aufgaben** sollten alle Modelle ähnlich performen
- Bei **vielen parallelen Aufgaben**:
  - Platform Threads werden durch den Pool begrenzt
  - Virtual Threads sollten am besten skalieren (besonders mit echten Virtual Threads in Java 21)
  - Kernel Threads (simuliert) werden stark durch die Pool-Größe begrenzt
  - User Threads (simuliert) sollten besser als Kernel Threads, aber schlechter als Virtual Threads abschneiden

- Bei **größeren Matrizen** (CPU-intensiv) kann der Overhead von Virtual Threads sichtbar werden
- Bei **sehr kleinen Matrizen** spielen Thread-Wechsel-Kosten eine größere Rolle

## Ausführung

1. Sicherstellen, dass mindestens Java 17 installiert ist (Java 21 für echte Virtual Threads)
2. Projekt starten: `mvn spring-boot:run`
3. Im Browser öffnen:
   - Hauptseite: http://localhost:8080/
   - Thread-Monitor: http://localhost:8080/thread-monitor.html

## Bildungsaspekte

Dieses Projekt verdeutlicht:

- Die Auswirkungen verschiedener Thread-Modelle auf die Leistung
- Skalierungsverhalten unter Last
- Ressourcennutzung durch verschiedene Thread-Implementierungen
- Visualisierung von Thread-Zuständen und Verteilungen
- Vorteile von modernen Thread-Modellen gegenüber traditionellen Ansätzen
