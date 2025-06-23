function confirmDeleteBook(bookId, bookTitle) {
    if (confirm('Sei sicuro di voler eliminare il libro "' + bookTitle + '"?')) {
        window.location.href = '/book/delete/' + bookId;
        return true;
    }
    return false;
}
function confirmDeleteAuthor(authorId, authorName) {
    if (confirm('Sei sicuro di voler eliminare l\'autore "' + authorName + '"?')) {
        window.location.href = '/author/delete/' + authorId;
        return true;
    }
    return false;
}