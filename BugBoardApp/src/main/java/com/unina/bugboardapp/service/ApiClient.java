package com.unina.bugboardapp.service;

import com.unina.bugboardapp.exception.ApiException;
import com.unina.bugboardapp.manager.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Client HTTP centralizzato per l'accesso alle API REST dell'applicazione.
 * <p>
 * Incapsula la creazione ed esecuzione di richieste {@code GET}/{@code POST} usando
 * {@link java.net.http.HttpClient}, includendo automaticamente:
 * </p>
 * <ul>
 *   <li>Base URL comune ({@link #BASE_URL})</li>
 *   <li>Header {@code Content-Type: application/json}</li>
 *   <li>Header {@code Authorization: Bearer ...} quando un token è presente in sessione</li>
 * </ul>
 *
 * <h2>Gestione errori</h2>
 * <p>
 * Per risposte con status code &gt;= 400 viene sollevata una {@link ApiException}
 * con codice e payload della risposta, e viene scritto un log a livello {@link Level#WARNING}.
 * </p>
 *
 * <h2>Note di utilizzo</h2>
 * <p>
 * La classe è implementata come singleton tramite {@link #getInstance()}.
 * </p>
 */
public class ApiClient {
    private static final Logger logger = Logger.getLogger(ApiClient.class.getName());
    private static final String BASE_URL = "http://localhost:8080/api";
    private static ApiClient instance;
    private final HttpClient client;

    private ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    /**
     * Restituisce l'istanza singleton del client.
     *
     * @return istanza di {@link ApiClient}
     */
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }
    /**
     * Esegue una richiesta GET verso l'endpoint specificato.
     *
     * @param endpoint path relativo (es. {@code "/issues"})
     * @return body della risposta
     * @throws IOException          in caso di errore I/O durante l'invio/ricezione
     * @throws InterruptedException se il thread viene interrotto durante l'attesa della risposta
     * @throws ApiException         se lo status code è &gt;= 400
     */
    public String get(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = createGetRequest(endpoint);
        return executeRequest(request);
    }
    /**
     * Esegue una richiesta POST verso l'endpoint specificato, con payload JSON.
     *
     * @param endpoint path relativo (es. {@code "/issues"})
     * @param jsonBody corpo della richiesta in formato JSON (stringa)
     * @return body della risposta
     * @throws IOException          in caso di errore I/O durante l'invio/ricezione
     * @throws InterruptedException se il thread viene interrotto durante l'attesa della risposta
     * @throws ApiException         se lo status code è &gt;= 400
     */
    public String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = createPostRequest(endpoint, jsonBody);
        return executeRequest(request);
    }



    private HttpRequest createGetRequest(String endpoint) {
        return getBaseRequestBuilder(endpoint)
                .GET()
                .build();
    }

    private HttpRequest createPostRequest(String endpoint, String jsonBody) {
        return getBaseRequestBuilder(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    /**
     * Crea un builder con URI e header comuni (Authorization e Content-Type).
     */
    private HttpRequest.Builder getBaseRequestBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint));

        String token = SessionManager.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        builder.header("Content-Type", "application/json");
        return builder;
    }
    /**
     * Invia la richiesta e gestisce l'errore in base allo status code.
     */
    private String executeRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        handleError(response);
        return response.body();
    }
    /**
     * Solleva {@link ApiException} per risposte HTTP con status code &gt;= 400.
     */
    private void handleError(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            logger.log(Level.WARNING, () -> "API Error " + response.statusCode() + ": " + response.body());
            throw new ApiException(response.statusCode(),"API call failed: "  + response.body());
        }
    }
}
