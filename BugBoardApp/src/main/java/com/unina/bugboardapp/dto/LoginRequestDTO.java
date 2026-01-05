package com.unina.bugboardapp.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * DTO (Data Transfer Object) per una richiesta di autenticazione (login).
 * <p>
 * Questa classe rappresenta tipicamente il payload JSON inviato a un endpoint di login e viene
 * serializzata/deserializzata tramite Jackson. I campi sono mappati su chiavi JSON tramite
 * {@link com.fasterxml.jackson.annotation.JsonProperty}.
 * </p>
 *
 * <p>
 * Contiene le credenziali necessarie: email e password.
 * </p>
 */
public class LoginRequestDTO {

    /**
     * Indirizzo email dell'utente.
     * Mappato sulla proprietà JSON {@code "email"}.
     */
    @JsonProperty("email")
    private String email;

    /**
     * Password dell'utente.
     * Mappato sulla proprietà JSON {@code "password"}.
     */
    @JsonProperty("password")
    private String password;

    /**
     * Costruttore vuoto richiesto/utile per la deserializzazione (es. Jackson).
     */
    public LoginRequestDTO() {
    }

    /**
     * Costruisce una richiesta di login con email e password.
     *
     * @param email    email dell'utente
     * @param password password dell'utente
     */
    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Restituisce l'email associata alla richiesta di login.
     *
     * @return email dell'utente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Imposta l'email associata alla richiesta di login.
     *
     * @param email nuova email dell'utente
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Restituisce la password associata alla richiesta di login.
     *
     * @return password dell'utente
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta la password associata alla richiesta di login.
     *
     * @param password nuova password dell'utente
     */
    public void setPassword(String password) {
        this.password = password;
    }
}