// 1. Prendi solo le stelle all'interno del form di inserimento recensione
const formStars = document.querySelectorAll('#stars .star');

// 2. Prendi il campo hidden del form per salvare il rating
const ratingInput = document.getElementById('rating');

// 3. Per ogni stella del form, aggiungi un event listener
formStars.forEach(star => {
    star.addEventListener('click', () => {
        const value = parseInt(star.getAttribute('data-value')); // Valore della stella cliccata
        ratingInput.value = value; // Salva il valore nel campo nascosto

        // Reset solo per le stelle del form
        formStars.forEach(s => {
            if (parseInt(s.getAttribute('data-value')) <= value) {
                // Stelle piene
                s.className = 'fa fa-star star';
            } else {
                // Stelle vuote
                s.className = 'far fa-star star';
            }
        });
    });
});