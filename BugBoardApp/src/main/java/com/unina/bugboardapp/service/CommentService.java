package com.unina.bugboardapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unina.bugboardapp.exception.CommentException;
import com.unina.bugboardapp.model.Comment;

import java.io.IOException;
import java.util.List;
/**
 * Service per la gestione dei commenti tramite API REST.
 * <p>
 * Incapsula le chiamate HTTP verso gli endpoint dei commenti e la conversione
 * JSON &harr; oggetti tramite Jackson.
 * </p>
 *
 * <h2>Serializzazione JSON</h2>
 * <p>
 * Configura {@link ObjectMapper} con {@link JavaTimeModule} e disabilita
 * {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} per gestire correttamente
 * date/ore in formato ISO-8601.
 * </p>
 */
public class CommentService {

    private final ApiClient apiClient;
    private final ObjectMapper mapper;
    /**
     * Costruisce il service inizializzando il client API singleton e l'ObjectMapper.
     */
    public CommentService() {
        this.apiClient = ApiClient.getInstance();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    /**
     * Crea un nuovo commento sul backend.
     *
     * @param newComment commento da creare
     * @return commento creato (come restituito dal server)
     * @throws CommentException se la risposta è vuota o se avvengono errori di comunicazione/parsing
     */
    public Comment createComment(Comment newComment) throws CommentException {
        try{
            String requestBody = mapper.writeValueAsString(newComment);
            String responseBody = apiClient.post("/comments", requestBody);
            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, Comment.class);
            }
            throw new CommentException("Comment creation failed: Empty response from server");
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new CommentException("Communication error during comment creation.", e);
        }catch(Exception e){
            throw new CommentException("Comment creation failed: unpredicted error.", e);
        }
    }
    /**
     * Recupera l'elenco dei commenti associati a una specifica issue.
     *
     * @param issueId id della issue
     * @return lista commenti; lista vuota se la risposta è vuota
     * @throws CommentException in caso di errori di comunicazione/parsing
     */
    public List<Comment> getCommentsByIssueId(Integer issueId) throws CommentException {
        try {
            String responseBody = apiClient.get("/comments/issue/" + issueId);
            if (responseBody != null && !responseBody.isEmpty()) {
                return mapper.readValue(responseBody, new TypeReference<List<Comment>>() {
                });
            }
            return List.of();
        }catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            throw new CommentException("Communication error during comment retrieval.", e);
        }catch(Exception e){
            throw new CommentException("Comment retrieval failed: unpredicted error.", e);
        }
    }
}
