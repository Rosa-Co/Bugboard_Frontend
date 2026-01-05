package com.unina.bugboardapp.exception;

/**
 * Eccezione checked che rappresenta un errore relativo alla gestione degli utenti.
 * <p>
 * Può essere lanciata in caso di fallimento durante operazioni sugli utenti
 * (ad es. registrazione, aggiornamento profilo, recupero dati) oppure quando
 * i dati utente non rispettano i vincoli previsti.
 * </p>
 */
public class UserException extends Exception {

    /**
     * Crea una nuova {@code UserException} con un messaggio descrittivo.
     *
     * @param message messaggio che descrive l'errore relativo agli utenti
     */
    public UserException(String message) {
        super(message);
    }

    /**
     * Crea una nuova {@code UserException} con messaggio e causa originale.
     *
     * @param message messaggio che descrive l'errore relativo agli utenti
     * @param cause   eccezione originaria che ha causato l'errore
     */
    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}