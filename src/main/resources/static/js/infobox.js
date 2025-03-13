/**
 * Thread-Demo Info-Box JavaScript
 * Funktionalität für die ausblendbare Info-Box mit Thread-Konzepten und Experimentvorschlägen
 */

document.addEventListener('DOMContentLoaded', function() {
    // Toggle-Funktionalität für die Info-Box hinzufügen
    const toggleInfoButton = document.getElementById('toggleInfoButton');
    if (toggleInfoButton) {
        toggleInfoButton.addEventListener('click', function() {
            const infoBox = document.getElementById('infoBox');
            
            if (infoBox.classList.contains('show')) {
                infoBox.classList.remove('show');
                this.textContent = 'Anzeigen';
            } else {
                infoBox.classList.add('show');
                this.textContent = 'Ausblenden';
            }
        });
    }
});
