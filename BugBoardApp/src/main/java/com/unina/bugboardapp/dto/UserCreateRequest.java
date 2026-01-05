package com.unina.bugboardapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unina.bugboardapp.model.UserType;

/**
 * DTO (Data Transfer Object) per la creazione di un utente.
 * <p>
 * Questa classe rappresenta il payload tipicamente serializzato/deserializzato in JSON tramite Jackson.
 * I campi sono mappati su specifiche proprietà JSON mediante {@link JsonProperty}.
 * </p>
 *
 * <p>
 * Contiene le informazioni necessarie alla creazione di un utente:
 * email, password e ruolo/tipologia ({@link UserType}).
 * </p>
 */
public class UserCreateRequest {

    /**
     * Email dell'utente da creare.
     * Mappata sulla proprietà JSON {@code "email"}.
     */
    @JsonProperty("email")
    private String email;

    /**
     * Password dell'utente da creare.
     * Mappata sulla proprietà JSON {@code "password"}.
     */
    @JsonProperty("password")
    private String password;

    /**
     * Ruolo/tipologia dell'utente da creare.
     * Mappata sulla proprietà JSON {@code "role"}.
     */
    @JsonProperty("role")
    private UserType role;

    /**
     * Costruttore vuoto, utile/richiesto per la deserializzazione (es. Jackson).
     */
    public UserCreateRequest() {
    }

    /**
     * Costruisce una richiesta di creazione utente inizializzando tutti i campi.
     *
     * @param email    email dell'utente
     * @param password password dell'utente
     * @param role     ruolo/tipologia dell'utente
     */
    public UserCreateRequest(String email, String password, UserType role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Restituisce l'email dell'utente da creare.
     *
     * @return email dell'utente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Imposta l'email dell'utente da creare.
     *
     * @param email nuova email dell'utente
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Restituisce la password dell'utente da creare.
     *
     * @return password dell'utente
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta la password dell'utente da creare.
     *
     * @param password nuova password dell'utente
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce il ruolo/tipologia dell'utente da creare.
     *
     * @return ruolo/tipologia dell'utente
     */
    public UserType getRole() {
        return role;
    }

    /**
     * Imposta il ruolo/tipologia dell'utente da creare.
     *
     * @param role nuovo ruolo/tipologia dell'utente
     */
    public void setRole(UserType role) {
        this.role = role;
    }
}