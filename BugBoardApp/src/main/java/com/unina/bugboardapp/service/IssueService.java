package com.unina.bugboardapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.exception.IssueException;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.dto.IssueCreateRequest;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;
/**
 * Service per la gestione delle issue tramite API REST.
 * <p>
 * Fornisce metodi per:
 * </p>
 * <ul>
 *   <li>Recuperare tutte le issue</li>
 *   <li>Creare una nuova issue</li>
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
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IssueException("Communication error during issue retrieval.", e);
        } catch (Exception e) {
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
            String localImagePath = newIssue.getImagePath();

            IssueCreateRequest request = new IssueCreateRequest(
                    newIssue.getTitle(),
                    newIssue.getDescription(),
                    newIssue.getType(),
                    null,
                    newIssue.getPriority(),
                    newIssue.getState());
            String requestBody = mapper.writeValueAsString(request);
            String responseBody = apiClient.post("/issues", requestBody);

            Issue createdIssue;
            if (responseBody != null && !responseBody.isEmpty()) {
                createdIssue = mapper.readValue(responseBody, Issue.class);
            } else {
                throw new IssueException("Issue creation failed: Empty response from server");
            }

            if (localImagePath != null && !localImagePath.isEmpty()) {
                Path path = Path.of(localImagePath);
                if (Files.exists(path)) {
                    String serverPath = apiClient.postMultipart("/images/upload/" + createdIssue.getId(), path);
                    createdIssue.setImagePath(serverPath);
                }
            }
            return createdIssue;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IssueException("Communication error during issue creation.", e);
        } catch (Exception e) {
            throw new IssueException("Issue creation failed: unpredicted error.", e);
        }
    }
    /**
     * Scarica un'immagine dal backend e restituisce lo stream dei bytes.
     * <p>
     * Il parametro {@code filename} può contenere anche un path (con {@code /} o {@code \});
     * in tal caso viene estratto solo il nome del file per evitare di inviare percorsi
     * al server e ridurre il rischio di path traversal lato client.
     * </p>
     *
     * <p><strong>Nota:</strong> lo stream restituito va chiuso dal chiamante.</p>
     *
     * @param filename nome file (o path) dell'immagine da scaricare
     * @return {@link java.io.InputStream} con il contenuto dell'immagine
     * @throws IssueException in caso di errore di comunicazione durante il download
     */
    public InputStream downloadImage(String filename) throws IssueException {
        try {
            String actualFilename = filename;
            if (filename.contains("/") || filename.contains("\\")) {
                actualFilename = new File(filename).getName();
            }
            return apiClient.getStream("/images/" + actualFilename);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IssueException("Error downloading image", e);
        }
    }
}
