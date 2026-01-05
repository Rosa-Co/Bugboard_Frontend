package com.unina.bugboardapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.exception.IssueException;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.dto.IssueCreateRequest;

import java.io.IOException;
import java.util.List;
/**
 * Service per la gestione delle issue tramite API REST.
 * <p>
 * Fornisce metodi per:
 * </p>
 * <ul>
 *   <li>recuperare tutte le issue</li>
 *   <li>creare una nuova issue</li>
 * </ul>
 *
 * <h2>Serializzazione JSON</h2>
 * <p>
 * Utilizza Jackson ({@link ObjectMapper}) con {@link JavaTimeModule} e disabilita
 * {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} per gestire correttamente
 * eventuali campi data/ora in formato ISO-8601.
 * </p>
 */
public class IssueService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;
    /**
     * Costruisce il service inizializzando il client API singleton e l'ObjectMapper.
     */
    public IssueService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    /**
     * Recupera tutte le issue dal backend.
     *
     * @return lista di {@link Issue}; lista vuota se la risposta è vuota
     * @throws IssueException in caso di errori di comunicazione o deserializzazione
     */
    public List<Issue> fetchAllIssues() throws IssueException {
        try {
            String responseBody = apiClient.get("/issues");
            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, new TypeReference<List<Issue>>() {
                });
            }
            return List.of();
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new IssueException("Communication error during issue retrieval.", e);
        }catch(Exception e){
            throw new IssueException("Issue retrieval failed: unpredicted error.", e);
        }
    }
    /**
     * Crea una nuova issue sul backend.
     * <p>
     * Converte il modello {@link Issue} in un payload dedicato {@link IssueCreateRequest}
     * per inviare solo i campi necessari alla creazione.
     * </p>
     *
     * @param newIssue issue da creare
     * @return issue creata (come restituita dal server)
     * @throws IssueException se la risposta è vuota o se avvengono errori di comunicazione/parsing
     */
    public Issue createIssue(Issue newIssue) throws IssueException {
        try {
            mapper.registerModule(new JavaTimeModule());
            IssueCreateRequest request = new IssueCreateRequest(
                    newIssue.getTitle(),
                    newIssue.getDescription(),
                    newIssue.getType(),
                    newIssue.getImagePath(),
                    newIssue.getPriority(),
                    newIssue.getState());
            String requestBody = mapper.writeValueAsString(request);
            String responseBody = apiClient.post("/issues", requestBody);
            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, Issue.class);
            }
            throw new IssueException("Issue creation failed: Empty response from server");
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new IssueException("Communication error during issue creation.", e);
        }catch(Exception e){
            throw new IssueException("Issue creation failed: unpredicted error.", e);
        }
    }
}
