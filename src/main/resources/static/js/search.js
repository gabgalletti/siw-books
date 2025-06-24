

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.querySelector('.search-bar');

    if (!searchInput) return;


    searchInput.addEventListener('input', function() {
        // valore accettato in minuscolo
        const searchTerm = searchInput.value.toLowerCase().trim();

        // container degli elementi (i div che contengono i link)
        const itemContainers = document.querySelectorAll('.content-list-container > div');

        // Per ogni container
        itemContainers.forEach(function(container) {
            // testo del titolo
            const titleElement = container.querySelector('.content-title-text');
            const title = titleElement ? titleElement.textContent.toLowerCase() : '';

            // testo del sottotitolo (autore per i libri)
            const subtitleElement = container.querySelector('.content-subtitle-text');
            const subtitle = subtitleElement ? subtitleElement.textContent.toLowerCase() : '';

            // Mostra o nascondi l'elemento in base alla corrispondenza
            if (searchTerm === '' || title.includes(searchTerm) || subtitle.includes(searchTerm)) {
                container.style.display = ''; // Mostra l'elemento
            } else {
                container.style.display = 'none'; // Nascondi l'elemento
            }
        });

        const visibleItems = document.querySelectorAll('.content-list-container > div[style=""]');
        const noResultsMessage = document.getElementById('no-results-message');

        if (visibleItems.length === 0 && searchTerm !== '') {
            // Se non esiste un messaggio "nessun risultato", crealo
            if (!noResultsMessage) {
                const message = document.createElement('div');
                message.id = 'no-results-message';
                message.textContent = 'Nessun risultato trovato';
                message.style.textAlign = 'center';
                message.style.padding = '20px';
                message.style.color = '#666';

                const container = document.querySelector('.content-list-container');
                if (container) {
                    container.appendChild(message);
                }
            } else {
                noResultsMessage.style.display = '';
            }
        } else if (noResultsMessage) {
            noResultsMessage.style.display = 'none';
        }
    });
});