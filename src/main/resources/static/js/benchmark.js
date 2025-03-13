/**
 * Thread-Demo Helper Functions
 * JavaScript-Funktionen für den Thread-Modell-Vergleich
 */

// UI-Updates für die CPU-Tests
document.getElementById('matrixSize').addEventListener('input', function() {
    document.getElementById('matrixSizeValue').textContent = this.value + ' x ' + this.value;
});

document.getElementById('parallelTasks').addEventListener('input', function() {
    document.getElementById('parallelTasksValue').textContent = this.value;
});

// UI-Updates für die I/O-Tests
document.getElementById('fileSizeKB').addEventListener('input', function() {
    document.getElementById('fileSizeKBValue').textContent = this.value + ' KB';
});

document.getElementById('ioParallelTasks').addEventListener('input', function() {
    document.getElementById('ioParallelTasksValue').textContent = this.value;
});

// Wechsel zwischen CPU- und I/O-Test-Konfiguration
document.getElementById('testTypeCPU').addEventListener('change', function() {
    if (this.checked) {
        document.getElementById('cpuTestConfig').style.display = 'block';
        document.getElementById('ioTestConfig').style.display = 'none';
    }
});

document.getElementById('testTypeIO').addEventListener('change', function() {
    if (this.checked) {
        document.getElementById('cpuTestConfig').style.display = 'none';
        document.getElementById('ioTestConfig').style.display = 'block';
    }
});

// Test-Funktionen
document.getElementById('runAllBtn').addEventListener('click', function() {
    runTest('compare-all');
});

document.getElementById('runPlatformBtn').addEventListener('click', function() {
    runTest('platform-threads');
});

document.getElementById('runVirtualBtn').addEventListener('click', function() {
    runTest('virtual-threads');
});

document.getElementById('runLimitedBtn').addEventListener('click', function() {
    runTest('limited-threads');
});

document.getElementById('runOptimizedBtn').addEventListener('click', function() {
    runTest('optimized-threads');
});

document.getElementById("heavyLoadSwitch").addEventListener("change", (event) => {
    const isChecked = event.target.checked;
    const url = isChecked ? "/api/system/heavy-load/start" : "/api/system/heavy-load/stop";

    fetch(url, { method: 'POST' })
        .then(response => response.text())
        .then(data => {
            console.log(data);
        })
        .catch(err => console.error('Error:', err));
});

// Globale Variablen für die Diagramme
let timeChart = null;
let tasksPerSecondChart = null;
let memoryChart = null;
let testResults = [];

// Funktion zum Ausführen der Tests
function runTest(endpoint) {
    // Test-Typ und Parameter bestimmen
    const testType = document.querySelector('input[name="testType"]:checked').value;
    let parallelTasks, matrixSize, fileSizeKB;
    
    if (testType === 'cpu') {
        parallelTasks = parseInt(document.getElementById('parallelTasks').value);
        matrixSize = parseInt(document.getElementById('matrixSize').value);
        fileSizeKB = 100; // Standardwert
    } else { // io
        parallelTasks = parseInt(document.getElementById('ioParallelTasks').value);
        matrixSize = 100; // Standardwert
        fileSizeKB = parseInt(document.getElementById('fileSizeKB').value);
    }
    
    // Anfragedaten
    const requestData = {
        testType: testType,
        matrixSize: matrixSize,
        parallelTasks: parallelTasks,
        fileSizeKB: fileSizeKB
    };
    
    // Anzeige
    let resultId = Date.now().toString();
    let resultCard = createResultCard(resultId, endpoint, requestData);
    document.getElementById('resultsContainer').prepend(resultCard);
    
    // API-Anfrage
    fetch(`/api/matrix/${endpoint}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(data => {
        updateResultCard(resultId, data);
        
        // Füge Ergebnisse für Diagramm hinzu
        if (Array.isArray(data)) {
            testResults = data;
        } else {
            testResults = [data];
        }
        
        updateCharts();
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById(resultId).querySelector('.loading').style.display = 'none';
        document.getElementById(resultId).querySelector('.card-body').innerHTML += `
            <div class="alert alert-danger mt-3">
                Fehler bei der Anfrage: ${error.message}
            </div>
        `;
    });
}

// Erstellt eine Ergebniskarte mit Ladeanzeige
function createResultCard(id, endpoint, requestData) {
    let title = "";
    let cardClass = "";
    
    switch(endpoint) {
        case 'platform-threads':
            title = "Platform Threads";
            cardClass = "thread-model-platform";
            break;
        case 'virtual-threads':
            title = "Virtual Threads";
            cardClass = "thread-model-virtual";
            break;
        case 'limited-threads':
            title = "Begrenzte Threads";
            cardClass = "thread-model-limited";
            break;
        case 'optimized-threads':
            title = "Optimierte Threads";
            cardClass = "thread-model-optimized";
            break;
        case 'compare-all':
            title = "Vergleich aller Thread-Modelle";
            cardClass = "";
            break;
    }
    
    const testTypeClass = requestData.testType === 'cpu' ? 'test-type-cpu' : 'test-type-io';
    
    const card = document.createElement('div');
    card.className = 'col-md-' + (endpoint === 'compare-all' ? '12' : '6');
    
    let cardContent = `
        <div id="${id}" class="card result-card ${cardClass} ${testTypeClass}">
            <div class="card-header">
                <h5 class="mb-0">${title} (${requestData.testType.toUpperCase()})</h5>
            </div>
            <div class="card-body">`;
            
    if (requestData.testType === 'cpu') {
        cardContent += `<p>Matrix: ${requestData.matrixSize} x ${requestData.matrixSize}</p>`;
    } else {
        cardContent += `<p>Dateigröße: ${requestData.fileSizeKB} KB</p>`;
    }
    
    cardContent += `
                <p>Parallele Aufgaben: ${requestData.parallelTasks}</p>
                <div class="text-center">
                    <div class="loading"></div>
                    <p class="mt-2">Test läuft...</p>
                </div>
            </div>
        </div>
    `;
    
    card.innerHTML = cardContent;
    return card;
}

// Aktualisiert eine Ergebniskarte mit den Testergebnissen
function updateResultCard(id, data) {
    const card = document.getElementById(id);
    if (!card) return;
    
    card.querySelector('.loading').style.display = 'none';
    
    const cardBody = card.querySelector('.card-body');
    if (Array.isArray(data)) {
        // Vergleich aller Thread-Modelle
        let resultsHtml = '<div class="table-responsive"><table class="table table-striped">';
        resultsHtml += '<thead><tr><th>Thread-Modell</th><th>Ausführungszeit (ms)</th><th>Aufgaben/Sekunde</th><th>Speicherverbrauch (MB)</th></tr></thead>';
        resultsHtml += '<tbody>';
        
        data.forEach(result => {
            const tasksPerSecond = (result.parallelTasks / (result.totalExecutionTimeMs / 1000)).toFixed(2);
            resultsHtml += `
                <tr>
                    <td>${result.threadModel}</td>
                    <td>${result.totalExecutionTimeMs}</td>
                    <td>${tasksPerSecond}</td>
                    <td>${result.memoryUsageMB ? result.memoryUsageMB.toFixed(2) : 'N/A'}</td>
                </tr>
            `;
        });
        
        resultsHtml += '</tbody></table></div>';
        cardBody.innerHTML = resultsHtml;
    } else {
        // Einzelnes Thread-Modell
        const tasksPerSecond = (data.parallelTasks / (data.totalExecutionTimeMs / 1000)).toFixed(2);
        let resultsHtml = `
            <h5>Ergebnisse:</h5>
            <p><strong>Thread-Modell:</strong> ${data.threadModel}</p>
            <p><strong>Test-Typ:</strong> ${data.testType ? data.testType.toUpperCase() : 'CPU'}</p>
        `;
        
        if (!data.testType || data.testType === 'cpu') {
            resultsHtml += `<p><strong>Matrix:</strong> ${data.matrixSize} x ${data.matrixSize}</p>`;
        } else {
            resultsHtml += `<p><strong>Dateigröße:</strong> ${data.fileSizeKB} KB</p>`;
        }
        
        resultsHtml += `
            <p><strong>Parallele Aufgaben:</strong> ${data.parallelTasks}</p>
            <p><strong>Gesamtausführungszeit:</strong> ${data.totalExecutionTimeMs} ms</p>
            <p><strong>Durchschnittliche Zeit pro Aufgabe:</strong> ${(data.totalExecutionTimeMs / data.parallelTasks).toFixed(2)} ms</p>
            <p><strong>Aufgaben pro Sekunde:</strong> ${tasksPerSecond}</p>
        `;
        
        // Optional: Speichernutzung anzeigen, wenn verfügbar
        if (data.memoryBeforeMB !== undefined) {
            resultsHtml += `
                <p><strong>Speichernutzung (Start):</strong> ${data.memoryBeforeMB.toFixed(2)} MB</p>
                <p><strong>Speichernutzung (Peak):</strong> ${data.memoryPeakMB ? data.memoryPeakMB.toFixed(2) : 'N/A'} MB</p>
                <p><strong>Speichernutzung (Ende):</strong> ${data.memoryAfterMB ? data.memoryAfterMB.toFixed(2) : 'N/A'} MB</p>
                <p><strong>Speicherverbrauch während Test:</strong> ${data.memoryUsageMB ? data.memoryUsageMB.toFixed(2) : 'N/A'} MB</p>
            `;
        }
        
        cardBody.innerHTML = resultsHtml;
    }
}

// Aktualisiert die Diagramme mit den aktuellen Testergebnissen
function updateCharts() {
    if (testResults.length === 0) return;
    
    document.getElementById('chartContainer').style.display = 'block';
    
    const labels = testResults.map(result => result.threadModel);
    const executionTimes = testResults.map(result => result.totalExecutionTimeMs);
    const tasksPerSecond = testResults.map(result => 
        (result.parallelTasks / (result.totalExecutionTimeMs / 1000)).toFixed(2)
    );
    const memoryUsage = testResults.map(result => 
        result.memoryUsageMB ? result.memoryUsageMB.toFixed(2) : 0
    );
    
    // Farben für die verschiedenen Thread-Modelle
    const backgroundColors = testResults.map(result => {
        if (result.threadModel.includes("Platform")) return 'rgba(40, 167, 69, 0.6)';
        if (result.threadModel.includes("Virtual")) return 'rgba(0, 123, 255, 0.6)';
        if (result.threadModel.includes("Begrenzte")) return 'rgba(255, 193, 7, 0.6)';
        if (result.threadModel.includes("Optimierte")) return 'rgba(220, 53, 69, 0.6)';
        return 'rgba(108, 117, 125, 0.6)';
    });
    
    // Ausführungszeit-Diagramm
    const timeCtx = document.getElementById('timeChart').getContext('2d');
    if (timeChart) timeChart.destroy();
    
    timeChart = new Chart(timeCtx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Ausführungszeit (ms)',
                data: executionTimes,
                backgroundColor: backgroundColors,
                borderColor: backgroundColors.map(color => color.replace('0.6', '1')),
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Zeit in Millisekunden (niedriger ist besser)'
                    }
                }
            },
            plugins: {
                title: {
                    display: true,
                    text: 'Gesamtausführungszeit'
                }
            }
        }
    });
    
    // Aufgaben pro Sekunde Diagramm
    const tasksCtx = document.getElementById('tasksPerSecondChart').getContext('2d');
    if (tasksPerSecondChart) tasksPerSecondChart.destroy();
    
    tasksPerSecondChart = new Chart(tasksCtx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Aufgaben pro Sekunde',
                data: tasksPerSecond,
                backgroundColor: backgroundColors,
                borderColor: backgroundColors.map(color => color.replace('0.6', '1')),
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Aufgaben pro Sekunde (höher ist besser)'
                    }
                }
            },
            plugins: {
                title: {
                    display: true,
                    text: 'Durchsatz (Aufgaben pro Sekunde)'
                }
            }
        }
    });

    // Speichernutzungs-Diagramm
    const memoryCtx = document.getElementById('memoryChart').getContext('2d');
    if (memoryChart) memoryChart.destroy();
    
    memoryChart = new Chart(memoryCtx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Speicherverbrauch (MB)',
                data: memoryUsage,
                backgroundColor: backgroundColors,
                borderColor: backgroundColors.map(color => color.replace('0.6', '1')),
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Speicher in MB (niedriger ist besser)'
                    }
                }
            },
            plugins: {
                title: {
                    display: true,
                    text: 'Speicherverbrauch während Test'
                }
            }
        }
    });
}
