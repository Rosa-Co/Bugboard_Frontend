package com.unina.bugboardapp.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.*;
import com.unina.bugboardapp.model.enums.IssueState;
import com.unina.bugboardapp.model.enums.IssueType;
import com.unina.bugboardapp.model.enums.Priority;
/**
 * Modello che rappresenta una Issue (segnalazione/bug/task) dell'applicazione.
 * <p>
 * La classe è annotata per l'integrazione con Jackson, usando nomi di proprietà
 * coerenti con il backend (es. {@code titolo}, {@code descrizione}, {@code priorita}).
 * Gli attributi non riconosciuti vengono ignorati grazie a {@link JsonIgnoreProperties}.
 * </p>
 *
 * <h2>Campi principali</h2>
 * <ul>
 *   <li>{@link #id}: identificativo della issue (incluso nel JSON se diverso da default)</li>
 *   <li>{@link #title}: titolo della issue</li>
 *   <li>{@link #description}: descrizione della issue</li>
 *   <li>{@link #type}: tipologia della issue</li>
 *   <li>{@link #priority}: priorità</li>
 *   <li>{@link #state}: stato corrente</li>
 *   <li>{@link #reporter}: utente che ha creato la issue</li>
 *   <li>{@link #imagePath}: eventuale percorso/URL immagine associata</li>
 *   <li>{@link #comments}: commenti associati (gestiti lato client, ignorati nel JSON)</li>
 * </ul>
 *
 * <h2>Note sulla serializzazione</h2>
 * <p>
 * {@link #comments} è marcato {@link JsonIgnore}: se i commenti vengono restituiti dal backend
 * con una proprietà dedicata, sarà necessario rimuovere l'ignore o introdurre un mapping esplicito.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("titolo")
    private String title;
    @JsonProperty("descrizione")
    private String description;
    @JsonProperty("tipologia")
    private IssueType type;
    @JsonProperty("priorita")
    private Priority priority;
    @JsonProperty("stato")
    private IssueState state;
    @JsonProperty("creataDa")
    private User reporter;
    @JsonProperty("img")
    private String imagePath; // Optional
    @JsonIgnore
    private List<Comment> comments;

    /**
     * Costruttore vuoto (necessario per Jackson).
     * Inizializza la lista commenti a vuota.
     */
    public Issue() {
        this.comments = new ArrayList<>();
    }
    /**
     * Costruisce una issue con i principali campi.
     *
     * @param type tipologia
     * @param title titolo
     * @param description descrizione
     * @param imagePath percorso/URL immagine (opzionale)
     * @param state stato iniziale
     * @param priority priorità
     * @param reporter utente che crea la issue
     */
    public Issue(IssueType type, String title, String description, String imagePath, IssueState state,
            Priority priority, User reporter) {
        this.title = title;
        this.description = description;
        this.reporter = reporter;
        this.comments = new ArrayList<>();
        this.imagePath = imagePath;
        this.type = type;
        this.priority = priority;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IssueType getType() {
        return type;
    }

    public Priority getPriority() {
        return priority;
    }

    public IssueState getState() {
        return state;
    }

    public User getReporter() {
        return reporter;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setState(IssueState state) {
        this.state = state;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    /**
     * Aggiunge un commento alla issue.
     * <p>
     * Se la lista commenti è {@code null}, viene inizializzata.
     * </p>
     *
     * @param comment commento da aggiungere
     */
    public void addComment(Comment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(comment);
    }
}