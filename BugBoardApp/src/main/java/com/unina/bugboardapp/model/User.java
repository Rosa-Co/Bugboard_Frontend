package com.unina.bugboardapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Modello che rappresenta un utente dell'applicazione.
 * <p>
 * La classe è mappata per la serializzazione/deserializzazione JSON tramite Jackson.
 * Eventuali proprietà JSON non riconosciute vengono ignorate.
 * </p>
 *
 * <h2>Mapping JSON</h2>
 * <ul>
 *   <li>{@code email} &rarr; {@link #username}</li>
 *   <li>{@code role} &rarr; {@link #type}</li>
 *   <li>{@code password} &rarr; {@link #password} (in sola scrittura lato JSON)</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private Integer id;
    @JsonProperty("email")
    private String username;
    @JsonProperty("password")
    private final String password;
    @JsonProperty("role")
    private final UserType type;
    /**
     * Costruttore vuoto richiesto/utile per Jackson e per creare un oggetto "placeholder".
     */
    public User() {
        this.username = "";
        this.password = "";
        this.type = UserType.USER;
    }
    /**
     * Costruttore principale usato da Jackson per deserializzare l'utente.
     *
     * @param username email/username
     * @param password password
     * @param type ruolo/tipologia (se {@code null} viene impostato {@link UserType#USER})
     */
    @JsonCreator
    public User(@JsonProperty("email") String username,
            @JsonProperty("password") String password,
            @JsonProperty("role") UserType type) {
        this.username = username;
        this.password = password;
        this.type = type != null ? type : UserType.USER;
    }

    @JsonProperty("email")
    public String getUsername() {
        return username;
    }

    @JsonProperty("role")
    public UserType getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }// ?non so se si può fare...

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
