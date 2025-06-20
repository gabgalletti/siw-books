/*function toggleFavourite(event) {
            const favouritedInput = document.getElementById("favourited");
            // Inverti il valore corrente
            favouritedInput.value = favouritedInput.value === "true" ? "false" : "true";
        }*/

// 1. Prendi tutte le stelle
const stars = document.querySelectorAll('.star');

// 2. Prendi il campo hidden dove salviamo il voto
const ratingInput = document.getElementById('rating');

// 3. Per ogni stella, aggiungiamo un event listener per il click
stars.forEach(star => {
    star.addEventListener('click', () => {
        // 4. Prendi il valore della stella cliccata (da 1 a 5)
        const value = parseInt(star.getAttribute('data-value'));

        // 5. Salva il valore nell'input hidden (che verrà inviato nel form)
        ratingInput.value = value;

        // 6. Rimuovi la classe "checked" da tutte le stelle
        stars.forEach(s => s.classList.remove('checked'));

        // 7. Aggiungi "checked" a tutte le stelle <= del voto selezionato
        stars.forEach(s => {
            if (parseInt(s.getAttribute('data-value')) <= value) {
                s.classList.add('checked');
            }
        });
    });
});