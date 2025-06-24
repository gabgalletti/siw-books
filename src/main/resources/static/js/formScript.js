document.addEventListener("DOMContentLoaded", function () {
    const fileInput = document.getElementById('image');
    const label = document.querySelector('.attach-image label');
    const checkmark = document.querySelector('.checkmark');

    if (fileInput && label) {
        fileInput.addEventListener('change', function () {
            if (this.files && this.files.length > 0) {
                label.textContent = 'Image Selected';

                // Mostra il checkmark se esiste
                if (checkmark) {
                    checkmark.style.display = 'inline';
                }
            } else {

                label.textContent = 'Select An Image';


                if (checkmark) {
                    checkmark.style.display = 'none';
                }
            }
        });
    } else {
        console.error('Elemento con id="image" o label non trovato.');
    }
});