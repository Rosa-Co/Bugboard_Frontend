package com.unina.bugboardapp.exception;

/**
 * Eccezione checked che rappresenta un errore relativo alla gestione dei commenti.
 * <p>
 * Può essere lanciata in caso di fallimento durante operazioni come creazione,
 * modifica, eliminazione o recupero di commenti, oppure in presenza di vincoli
 * non rispettati (ad es. dati non validi).
 * </p>
 */
public class CommentException extends Exception {

    /**
     * Crea una nuova {@code CommentException} con un messaggio descrittivo.
     *
     * @param message messaggio che descrive l'errore relativo ai commenti
     */
    public CommentException(String message) {
        super(message);
    }

    /**
     * Crea una nuova {@code CommentException} con messaggio e causa originale.
     *
     * @param message messaggio che descrive l'errore relativo ai commenti
     * @param cause   eccezione originaria che ha causato l'errore
     */
    public CommentException(String message, Throwable cause) {
        super(message, cause);
    }
}