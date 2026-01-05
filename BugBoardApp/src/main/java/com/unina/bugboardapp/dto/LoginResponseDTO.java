package com.unina.bugboardapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO (Data Transfer Object) per la risposta a una richiesta di autenticazione (login).
 * <p>
 * Questa classe modella i dati tipicamente restituiti dal backend dopo un login avvenuto con successo,
 * come il token di accesso, l'identificativo utente, il tipo di token, l'email e l'elenco dei ruoli.
 * </p>
 *
 * <p>
 * Nota: il campo {@code userId} è mappato esplicitamente sulla proprietà JSON {@code "id"} tramite
 * {@link com.fasterxml.jackson.annotation.JsonProperty}.
 * </p>
 */
public class LoginResponseDTO {

    /**
     * Token di autenticazione/authorization restituito dal server.
     */
    private String token;

    /**
     * Identificativo numerico dell'utente.
     * Mappato sulla proprietà JSON {@code "id"}.
     */
    @JsonProperty("id")
    private Integer userId;

    /**
     * Tipo del token (es. prefisso o tipologia, a seconda del protocollo adottato).
     */
    private String type;

    /**
     * Email associata all'utente autenticato.
     */
    private String email;

    /**
     * Elenco dei ruoli associati all'utente (es. stringhe come "ROLE_ADMIN", "ROLE_USER", ecc.).
     */
    private List<String> roles;

    /**
     * Costruttore vuoto, utile/richiesto per la deserializzazione (es. Jackson).
     */
    public LoginResponseDTO() {
        /*costruttore vuoto per la costruzione del messaggio JSON*/
    }

    /**
     * Restituisce il token di autenticazione.
     *
     * @return token restituito dal server
     */
    public String getToken() {
        return token;
    }

    /**
     * Imposta il token di autenticazione.
     *
     * @param token nuovo token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Restituisce l'identificativo dell'utente.
     *
     * @return id utente, oppure {@code null} se non presente
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Imposta l'identificativo dell'utente.
     *
     * @param userId nuovo id utente
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Restituisce il tipo del token.
     *
     * @return tipo del token
     */
    public String getType() {
        return type;
    }

    /**
     * Imposta il tipo del token.
     *
     * @param type nuovo tipo del token
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Restituisce l'email dell'utente autenticato.
     *
     * @return email dell'utente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Imposta l'email dell'utente autenticato.
     *
     * @param email nuova email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Restituisce l'elenco dei ruoli associati all'utente.
     *
     * @return lista dei ruoli, oppure {@code null} se non presente
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Imposta l'elenco dei ruoli associati all'utente.
     *
     * @param roles nuova lista di ruoli
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Restituisce un ruolo "semplificato" derivato dalla lista {@link #getRoles()}.
     * <p>
     * La logica applicata è:
     * <ul>
     *   <li>se {@code roles} non è {@code null}, non è vuota e il primo elemento contiene la stringa {@code "ADMIN"},
     *       allora ritorna {@code "ADMIN"}</li>
     *   <li>in tutti gli altri casi ritorna {@code "USER"}</li>
     * </ul>
     * </p>
     *
     * @return {@code "ADMIN"} oppure {@code "USER"} in base al contenuto di {@code roles}
     */
    public String getRole() {
        if (roles != null && !roles.isEmpty() && roles.getFirst().contains("ADMIN")) {
            return "ADMIN";
        }
        return "USER";
    }
}