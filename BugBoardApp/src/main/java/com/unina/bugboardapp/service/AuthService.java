package com.unina.bugboardapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.dto.LoginRequestDTO;
import com.unina.bugboardapp.dto.LoginResponseDTO;
import com.unina.bugboardapp.exception.AuthenticationException;
import com.unina.bugboardapp.manager.SessionManager;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.UserType;
import java.io.IOException;
/**
 * Servizio applicativo responsabile delle operazioni di autenticazione.
 * <p>
 * Attualmente supporta il login tramite chiamata HTTP all'endpoint {@code /auth/login}
 * e, in caso di successo, inizializza la sessione applicativa tramite {@link SessionManager}.
 * </p>
 *
 * <h2>Serializzazione JSON</h2>
 * <p>
 * Usa Jackson ({@link ObjectMapper}) con {@link JavaTimeModule} e con
 * {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} disabilitata per gestire correttamente
 * eventuali campi data/ora in formato ISO-8601.
 * </p>
 */
public class AuthService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;
    /**
     * Crea un nuovo {@link AuthService} inizializzando il client API singleton
     * e configurando l'ObjectMapper per la (de)serializzazione JSON.
     */
    public AuthService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    /**
     * Esegue il login verso il backend usando email e password.
     * <p>
     * Flusso:
     * </p>
     * <ol>
     *   <li>Costruisce un {@link LoginRequestDTO} e lo serializza in JSON</li>
     *   <li>Invia una POST a {@code /auth/login}</li>
     *   <li>Deserializza la risposta in {@link LoginResponseDTO}</li>
     *   <li>Costruisce un {@link User}, assegna l'id e salva token+utente in sessione</li>
     * </ol>
     *
     * @param email    email dell'utente
     * @param password password dell'utente
     * @return {@link User} autenticato (con id valorizzato)
     * @throws AuthenticationException se la risposta è vuota o se si verificano errori di comunicazione o parsing
     */
    public User login(String email, String password) throws AuthenticationException {
        try{
            LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);
            String requestBody = mapper.writeValueAsString(loginRequest);

            String responseBody = apiClient.post("/auth/login", requestBody);

            if (responseBody != null && !responseBody.isEmpty()) {
                LoginResponseDTO response = mapper.readValue(responseBody, LoginResponseDTO.class);
                User user = new User(email, password,
                        "ADMIN".equalsIgnoreCase(response.getRole()) ? UserType.ADMIN : UserType.USER);
                user.setId(response.getUserId());
                SessionManager.getInstance().login(user, response.getToken());
                return user;
            }
            throw new AuthenticationException("Login failed: Empty response from server.");
        }catch(IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new AuthenticationException("Communication error during login.", e);
        }catch(Exception e){
            throw new AuthenticationException("Login failed: unpredicted error.", e);
        }
    }
}
