// InfoBox-Inhalt laden mit verbesserter Fehlerbehandlung
function loadInfoBox() {
    const infoBoxContainer = document.getElementById('infoBoxContainer');
    if (!infoBoxContainer) {
        console.error("InfoBox Container nicht gefunden");
        return;
    }

    // Ladeindikator anzeigen
    infoBoxContainer.innerHTML = '<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div><p>Lade Projektinformationen...</p></div>';

    fetch('infobox.html')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP Fehler: ${response.status}`);
            }
            return response.text();
        })
        .then(data => {
            infoBoxContainer.innerHTML = data;
            console.log("InfoBox HTML geladen, starte Initialisierung");
            // Prüfen ob die notwendigen Elemente existieren
            if (document.getElementById('toggleInfoButton') && document.getElementById('infoBox')) {
                // Initialisierung der InfoBox-Funktionalität nach dem Laden des Inhalts
                setTimeout(() => initializeInfoBox(), 100);
            } else {
                console.error("Notwendige InfoBox-Elemente nicht gefunden");
                infoBoxContainer.innerHTML = `
                    <div class="alert alert-warning">
                        <h4 class="alert-heading">Hinweis</h4>
                        <p>Die Projektinformationen konnten nicht vollständig geladen werden.</p>
                        <hr>
                        <p class="mb-0">Die Demo-Funktionalität bleibt davon unberührt.</p>
                    </div>
                `;
            }
        })
        .catch(error => {
            console.error('Error loading infobox:', error);
            infoBoxContainer.innerHTML = `
                <div class="alert alert-danger">
                    <h4 class="alert-heading">Fehler beim Laden der Info-Box</h4>
                    <p>${error.message}</p>
                    <hr>
                    <p class="mb-0">Die Demo-Funktionalität bleibt davon unberührt.</p>
                </div>
            `;
        });
}

// Lade InfoBox, wenn das DOM bereit ist
document.addEventListener('DOMContentLoaded', loadInfoBox);
