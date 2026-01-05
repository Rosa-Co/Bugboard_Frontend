package com.unina.bugboardapp.exception;

/**
 * Eccezione checked che rappresenta un errore relativo alla gestione delle issue.
 * <p>
 * Può essere lanciata quando un'operazione sulle issue (ad es. creazione, aggiornamento,
 * chiusura o recupero) fallisce, oppure quando i dati dell'issue non rispettano
 * i vincoli previsti.
 * </p>
 */
public class IssueException extends Exception {

    /**
     * Crea una nuova {@code IssueException} con un messaggio descrittivo.
     *
     * @param message messaggio che descrive l'errore relativo alle issue
     */
    public IssueException(String message) {
        super(message);
    }

    /**
     * Crea una nuova {@code IssueException} con messaggio e causa originale.
     *
     * @param message messaggio che descrive l'errore relativo alle issue
     * @param cause   eccezione originaria che ha causato l'errore
     */
    public IssueException(String message, Throwable cause) {
        super(message, cause);
    }
}