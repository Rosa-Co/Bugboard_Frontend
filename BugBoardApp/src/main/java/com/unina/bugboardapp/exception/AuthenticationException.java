package com.unina.bugboardapp.exception;

/**
 * Eccezione checked che rappresenta un errore di autenticazione.
 * <p>
 * Può essere utilizzata per segnalare condizioni di fallimento durante la verifica
 * delle credenziali o l'esecuzione di operazioni che richiedono autenticazione.
 * </p>
 */
public class AuthenticationException extends Exception {

    /**
     * Crea una nuova {@code AuthenticationException} con un messaggio descrittivo.
     *
     * @param message messaggio che descrive la causa dell'errore di autenticazione
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Crea una nuova {@code AuthenticationException} con messaggio e causa originale.
     *
     * @param message messaggio che descrive la causa dell'errore di autenticazione
     * @param cause   eccezione originaria che ha causato l'errore
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}