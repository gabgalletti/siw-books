function showCheckmark(fileInput) {
    if (fileInput.files && fileInput.files.length > 0) {
        const attachImageDiv = fileInput.parentElement;
        const checkmark = attachImageDiv.querySelector('.checkmark');
        console.log("File selezionato:", fileInput.files[0]); // Debug
        console.log("Elemento checkmark trovato?", !!checkmark); // Debug
        if (checkmark) {
            checkmark.style.display = 'inline'; // Mostra il checkmark
        }
    }
}
