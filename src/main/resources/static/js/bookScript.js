// 1. Prendi tutte le stelle
const stars = document.querySelectorAll('.star');

// 2. Prendi il campo hidden dove salvi il voto
const ratingInput = document.getElementById('rating');

// 3. Per ogni stella, aggiungiamo un event listener per il click
stars.forEach(star => {
    star.addEventListener('click', () => {
        // 4. Prendi il valore della stella cliccata (da 1 a 5)
        const value = parseInt(star.getAttribute('data-value'));

        // 5. Salva il valore nell'input hidden (che verrà inviato nel form)
        ratingInput.value = value;

        // 6. Rimuovi le classi "fa-star" e aggiungi "far" per tutte le stelle
        stars.forEach(s => {
            s.classList.remove('fa', 'fa-star', 'checked');
            s.classList.add('far', 'fa-star');
        });

        // 7. Aggiungi "fa-star" e "checked" a tutte le stelle <= del voto selezionato
        stars.forEach(s => {
            if (parseInt(s.getAttribute('data-value')) <= value) {
                s.classList.remove('far');
                s.classList.add('fa', 'checked');
            }
        });
    });
});
