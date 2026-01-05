package com.unina.bugboardapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unina.bugboardapp.model.enums.IssueType;
import com.unina.bugboardapp.model.enums.Priority;
import com.unina.bugboardapp.model.enums.IssueState;

/**
 * DTO (Data Transfer Object) per la creazione di una segnalazione/issue.
 * <p>
 * Questa classe rappresenta il payload tipicamente serializzato/deserializzato in JSON tramite Jackson.
 * I campi sono mappati su chiavi JSON in italiano tramite {@link com.fasterxml.jackson.annotation.JsonProperty}.
 * </p>
 *
 * <p>
 * Contiene le informazioni principali per creare una issue:
 * titolo, descrizione, tipologia, eventuale percorso/identificativo immagine, priorità e stato.
 * </p>
 */
public class IssueCreateRequest {

    /**
     * Titolo della issue.
     * Mappato sulla proprietà JSON {@code "titolo"}.
     */
    @JsonProperty("titolo")
    private String title;

    /**
     * Descrizione della issue.
     * Mappato sulla proprietà JSON {@code "descrizione"}.
     */
    @JsonProperty("descrizione")
    private String description;

    /**
     * Tipologia della issue.
     * Mappato sulla proprietà JSON {@code "tipologia"}.
     */
    @JsonProperty("tipologia")
    private IssueType type;

    /**
     * Percorso o identificativo dell'immagine associata alla issue (se presente).
     * Mappato sulla proprietà JSON {@code "img"}.
     */
    @JsonProperty("img")
    private String imagePath;

    /**
     * Priorità della issue.
     * Mappato sulla proprietà JSON {@code "priorita"}.
     */
    @JsonProperty("priorita")
    private Priority priority;

    /**
     * Stato della issue.
     * Mappato sulla proprietà JSON {@code "stato"}.
     */
    @JsonProperty("stato")
    private IssueState state;

    /**
     * Costruttore vuoto richiesto/utile per la deserializzazione (es. Jackson).
     */
    public IssueCreateRequest() {
    }

    /**
     * Costruisce una richiesta di creazione issue inizializzando tutti i campi.
     *
     * @param title       titolo della issue
     * @param description descrizione della issue
     * @param type        tipologia della issue
     * @param imagePath   percorso/identificativo dell'immagine associata (può essere {@code null})
     * @param priority    priorità della issue
     * @param state       stato della issue
     */
    public IssueCreateRequest(String title, String description, IssueType type, String imagePath, Priority priority,
            IssueState state) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.imagePath = imagePath;
        this.priority = priority;
        this.state = state;
    }

    /**
     * Restituisce il titolo della issue.
     *
     * @return titolo della issue
     */
    public String getTitle() {
        return title;
    }

    /**
     * Imposta il titolo della issue.
     *
     * @param title nuovo titolo della issue
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Restituisce la descrizione della issue.
     *
     * @return descrizione della issue
     */
    public String getDescription() {
        return description;
    }

    /**
     * Imposta la descrizione della issue.
     *
     * @param description nuova descrizione della issue
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Restituisce la tipologia della issue.
     *
     * @return tipologia della issue
     */
    public IssueType getType() {
        return type;
    }

    /**
     * Imposta la tipologia della issue.
     *
     * @param type nuova tipologia della issue
     */
    public void setType(IssueType type) {
        this.type = type;
    }

    /**
     * Restituisce il percorso/identificativo dell'immagine associata alla issue.
     *
     * @return percorso/identificativo immagine, oppure {@code null} se non presente
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Imposta il percorso/identificativo dell'immagine associata alla issue.
     *
     * @param imagePath nuovo percorso/identificativo immagine (può essere {@code null})
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Restituisce la priorità della issue.
     *
     * @return priorità della issue
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Imposta la priorità della issue.
     *
     * @param priority nuova priorità della issue
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Restituisce lo stato della issue.
     *
     * @return stato della issue
     */
    public IssueState getState() {
        return state;
    }

    /**
     * Imposta lo stato della issue.
     *
     * @param state nuovo stato della issue
     */
    public void setState(IssueState state) {
        this.state = state;
    }
}