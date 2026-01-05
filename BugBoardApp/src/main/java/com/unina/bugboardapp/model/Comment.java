package com.unina.bugboardapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
/**
 * Modello che rappresenta un commento associato a una issue.
 * <p>
 * La classe è mappata per la serializzazione/deserializzazione JSON tramite Jackson
 * usando proprietà con nomi coerenti con il backend (es. {@code scrittoDa}, {@code descrizione}).
 * </p>
 *
 * <h2>Campi principali</h2>
 * <ul>
 *   <li>{@link #id}: identificativo del commento</li>
 *   <li>{@link #author}: autore del commento</li>
 *   <li>{@link #issueId}: identificativo della issue a cui il commento appartiene</li>
 *   <li>{@link #content}: testo del commento</li>
 *   <li>{@link #timestamp}: data/ora del commento</li>
 * </ul>
 *
 */
public class Comment {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("scrittoDa")
    private User author;
    @JsonProperty("appartieneId")
    private Integer issueId;
    @JsonProperty("descrizione")
    private String content;
    @JsonProperty("data")
    private LocalDateTime timestamp;

    /**
     * Costruttore vuoto richiesto da Jackson.
     */
    public Comment() {}
    /**
     * Costruisce un commento assegnando autore, contenuto e issue associata.
     * <p>
     * Il {@link #timestamp} viene impostato all'istante corrente.
     * </p>
     *
     * @param author autore del commento
     * @param content contenuto testuale
     * @param issueId id della issue a cui appartiene
     */
    public Comment(User author, String content, Integer issueId) {
        this.author = author;
        this.content = content;
        this.issueId=issueId;
        this.timestamp = LocalDateTime.now();
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("scrittoDa")
    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    @JsonProperty("appartieneId")
    public Integer getIssueId() {
        return issueId;
    }
    public void setIssue(Integer issueId) {
        this.issueId = issueId;
    }

    @JsonProperty("descrizione")
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
