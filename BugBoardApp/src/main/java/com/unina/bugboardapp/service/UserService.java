package com.unina.bugboardapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.exception.UserException;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.dto.UserCreateRequest;

import java.io.IOException;

/**
 * Service layer per le operazioni utente lato client.
 * <p>
 * Questa classe incapsula le chiamate HTTP verso il backend per:
 * <ul>
 *   <li>creazione di un utente</li>
 *   <li>verifica esistenza utente tramite email</li>
 * </ul>
 * </p>
 *
 * <h2>Serializzazione</h2>
 * Utilizza {@link ObjectMapper} di Jackson configurato con {@link JavaTimeModule} e con
 * {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} disabilitata, in modo da serializzare
 * eventuali campi data/ora in formato leggibile (ISO-8601) anziché timestamp.
 *
 * <h2>Trasporto</h2>
 * Le richieste sono eseguite tramite {@link ApiClient} (ottenuto come Singleton).
 */
public class UserService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;

    /**
     * Costruisce un {@code UserService} inizializzando:
     * <ul>
     *   <li>il client HTTP ({@link ApiClient})</li>
     *   <li>il mapper Jackson ({@link ObjectMapper}) con supporto alle date Java Time</li>
     * </ul>
     */
    public UserService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Crea un nuovo utente sul backend.
     * <p>
     * Il {@link User} ricevuto in input viene trasformato in un {@link UserCreateRequest} e
     * serializzato in JSON; la richiesta viene inviata con una POST a {@code /users}.
     * </p>
     *
     * @param newUser utente da creare (dati sorgente per username/email, password e tipo)
     * @return l'utente creato deserializzato dalla risposta del server
     * @throws UserException se la risposta è vuota/null, se avvengono errori di comunicazione
     *                       o errori imprevisti durante serializzazione/deserializzazione
     */
    public User createUser(User newUser) throws UserException {
        try {
            UserCreateRequest request = new UserCreateRequest(newUser.getUsername(), newUser.getPassword(),
                    newUser.getType());
            String requestBody = mapper.writeValueAsString(request);
            String responseBody = apiClient.post("/users", requestBody);

            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, User.class);
            }
            throw new UserException("User creation failed: Empty response from server");
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UserException("Communication error during user creation.", e);
        } catch (Exception e) {
            throw new UserException("User creation failed: unpredicted error.", e);
        }
    }

    /**
     * Verifica se esiste un utente con la specifica email sul backend.
     * <p>
     * Esegue una GET a {@code /users/email/{email}} e considera l'utente esistente se la risposta
     * è non nulla e non vuota.
     * </p>
     *
     * @param email email da verificare (inserita nell'URL)
     * @return {@code true} se il backend restituisce un body non vuoto, {@code false} altrimenti
     * @throws IOException          se si verifica un errore I/O durante la comunicazione
     * @throws InterruptedException se il thread viene interrotto durante l'operazione HTTP
     */
    public boolean existsUser(String email) throws IOException, InterruptedException {
        String responseBody = apiClient.get("/users/email/" + email);
        return (responseBody != null && !responseBody.isEmpty());
    }
}