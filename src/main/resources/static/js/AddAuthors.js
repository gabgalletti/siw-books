document.addEventListener("DOMContentLoaded", function () {
    // Recupera gli elementi dal DOM
    const addAuthorBtn = document.getElementById("add-author-btn");
    const selectContainer = document.getElementById("select-container");
    const authorTemplate = document.getElementById("author");

    // Controlla che l'elemento con ID 'author' esista
    if (!authorTemplate) {
        console.error("Elemento con ID 'author' non trovato! Verifica l'HTML.");
        return;
    }

    // Lista per memorizzare gli ID degli autori selezionati
    const selectedAuthors = [];

    // Cliccando sul pulsante "+"
    addAuthorBtn.addEventListener("click", function () {
        // Crea una nuova riga contenente una select
        const selectRow = document.createElement("div");
        selectRow.classList.add("select-row");
        selectRow.style.display = "flex";
        selectRow.style.alignItems = "center";
        selectRow.style.gap = "8px";

        // Crea il nuovo elemento select
        const newSelect = document.createElement("select");
        newSelect.classList.add("form-select");
        newSelect.name = "authors[]"; // Autori come array
        newSelect.innerHTML = authorTemplate.innerHTML; // Copia tutte le opzioni

        // Memorizza immediatamente il valore selezionato appena la select viene creata
        if (newSelect.value) {
            addAuthorToList(newSelect.value);
        }

        // Aggiungi un evento per aggiornare la lista quando cambia la selezione
        newSelect.addEventListener("change", function () {
            updateAuthorList();
        });

        // Crea il pulsante per rimuovere la riga
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "x";
        removeBtn.type = "button";
        removeBtn.classList.add("btn", "btn-secondary");
        removeBtn.style.padding = "0.2rem 0.5rem";
        removeBtn.style.color = "red";

        // Rimuove la riga (e aggiorna la lista degli autori selezionati)
        removeBtn.addEventListener("click", function () {
            selectContainer.removeChild(selectRow); // Rimuovi il div della riga
            updateAuthorList(); // Aggiorna la lista
        });

        // Aggiungi la select e il pulsante alla riga
        selectRow.appendChild(newSelect);
        selectRow.appendChild(removeBtn);

        // Aggiungi la nuova riga al container
        selectContainer.appendChild(selectRow);
    });

    /**
     * Aggiorna completamente la lista degli autori selezionati
     */
    function updateAuthorList() {
        // Svuota la lista corrente
        selectedAuthors.length = 0;

        // Trova tutte le select nel container e aggiungi i loro valori alla lista
        const allSelects = selectContainer.querySelectorAll("select");
        allSelects.forEach(select => {
            if (select.value) {
                selectedAuthors.push(select.value);
            }
        });

        console.log("Lista aggiornata:", selectedAuthors);
    }

    /**
     * Aggiunge un singolo autore alla lista (senza duplicati)
     */
    function addAuthorToList(authorId) {
        if (!selectedAuthors.includes(authorId) && authorId !== "Select Author") {
            selectedAuthors.push(authorId);
        }
    }

    function addHiddenInputs() {
        const form = document.querySelector("form"); // Recupera il form
        const container = document.querySelector("#select-container");

        // Rimuovi eventuali input nascosti precedenti
        const hiddenInputs = container.querySelectorAll('input[name="authors[]"]');
        hiddenInputs.forEach(input => input.remove());

        // Aggiungi un input hidden per ciascun autore selezionato
        selectedAuthors.forEach(authorId => {
            const hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "authors[]"; // Necessario per inviare una lista
            hiddenInput.value = authorId;  // Valore dell'autore selezionato
            form.appendChild(hiddenInput); // Aggiungi al form
        });
        console.log("Form data prima del submit:");
        const formData = new FormData(form);
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`); // Stampa ogni coppia key-value inclusa nel form
        }

        console.log("Input nascosti aggiunti:", form.querySelectorAll('input[name="authors[]"]'));
    }

    // Collega la funzione `addHiddenInputs` al submit del form
    const form = document.querySelector("form");
    if (form) {
        form.addEventListener("submit", addHiddenInputs);
    }


});