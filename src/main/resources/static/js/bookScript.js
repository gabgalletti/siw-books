
const formStars = document.querySelectorAll('#stars .star');


const ratingInput = document.getElementById('rating');


formStars.forEach(star => {
    star.addEventListener('click', () => {
        const value = parseInt(star.getAttribute('data-value')); // Valore della stella cliccata
        ratingInput.value = value;

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