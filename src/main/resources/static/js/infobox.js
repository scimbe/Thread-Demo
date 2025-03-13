/**
 * Thread-Demo Info-Box JavaScript
 * Funktionalität für die ausblendbare Info-Box mit Thread-Konzepten und Experimentvorschlägen
 */

// Diese Funktion wird aufgerufen, wenn die Info-Box geladen wurde
function initializeInfoBox() {
    console.log("Initialisiere InfoBox");
    // Toggle-Funktionalität für die Info-Box hinzufügen
    const toggleInfoButton = document.getElementById('toggleInfoButton');
    if (toggleInfoButton) {
        console.log("Toggle-Button gefunden");
        toggleInfoButton.addEventListener('click', function() {
            const infoBox = document.getElementById('infoBox');
            if (infoBox) {
                if (infoBox.classList.contains('show')) {
                    infoBox.classList.remove('show');
                    this.textContent = 'Anzeigen';
                } else {
                    infoBox.classList.add('show');
                    this.textContent = 'Ausblenden';
                }
            } else {
                console.error("InfoBox-Element nicht gefunden");
            }
        });
    } else {
        console.error("Toggle-Button nicht gefunden");
    }
}

// Ursprünglicher Event-Listener für den Fall, dass die InfoBox direkt im HTML enthalten ist
document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM geladen, prüfe auf direkten Toggle-Button");
    // Wenn die Info-Box direkt im HTML ist (nicht per fetch geladen)
    const toggleInfoButton = document.getElementById('toggleInfoButton');
    if (toggleInfoButton) {
        console.log("Toggle-Button direkt im DOM gefunden");
        initializeInfoBox();
    }
});
