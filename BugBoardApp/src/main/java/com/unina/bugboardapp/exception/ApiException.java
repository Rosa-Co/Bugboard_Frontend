package com.unina.bugboardapp.exception;

/**
 * Eccezione runtime che rappresenta un errore proveniente da una chiamata API/HTTP.
 * <p>
 * Incapsula un codice di stato (ad es. HTTP) insieme a un messaggio descrittivo,
 * rendendo disponibile il codice tramite {@link #getStatusCode()}.
 * </p>
 */
public class ApiException extends RuntimeException {

    /**
     * Codice di stato associato all'errore (tipicamente un codice HTTP).
     */
    private final int statusCode;

    /**
     * Crea una nuova {@code ApiException} con codice di stato e messaggio.
     *
     * @param statusCode codice di stato dell'errore (es. HTTP)
     * @param message    messaggio descrittivo dell'errore
     */
    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Restituisce il codice di stato associato all'errore.
     *
     * @return codice di stato (tipicamente HTTP)
     */
    public int getStatusCode() {
        return statusCode;
    }
}