package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.enums.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Contenitore dello stato applicativo condiviso (in-memory) per il client.
 * <p>
 * Mantiene:
 * <ul>
 *   <li>la lista osservabile degli utenti caricati/creati ({@link #getUsers()})</li>
 *   <li>la lista osservabile delle issue disponibili ({@link #getIssues()})</li>
 *   <li>l'utente attualmente autenticato ({@link #getLoggedUser()})</li>
 * </ul>
 * </p>
 *
 * <p>
 * Le collezioni sono {@link ObservableList} (JavaFX) per facilitare il data-binding con la UI:
 * le modifiche alle liste possono riflettersi automaticamente nelle viste.
 * </p>
 */
public class AppState {
    private final ObservableList<User> users;
    private final ObservableList<Issue> issues;
    private User loggedUser;

    /**
     * Inizializza lo stato applicativo creando le liste osservabili di utenti e issue.
     */
    public AppState() {
        this.users = FXCollections.observableArrayList();
        this.issues = FXCollections.observableArrayList();
    }

    /**
     * Restituisce la lista osservabile degli utenti presenti nello stato.
     *
     * @return lista osservabile di {@link User}
     */
    public ObservableList<User> getUsers() {
        return users;
    }

    /**
     * Restituisce la lista osservabile delle issue presenti nello stato.
     *
     * @return lista osservabile di {@link Issue}
     */
    public ObservableList<Issue> getIssues() {
        return issues;
    }

    /**
     * Restituisce l'utente attualmente loggato.
     *
     * @return utente autenticato, oppure {@code null} se nessun utente ha effettuato il login
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Imposta l'utente attualmente loggato.
     * <p>
     * Passare {@code null} equivale a effettuare il logout a livello di stato applicativo.
     * </p>
     *
     * @param loggedUser utente autenticato da impostare, oppure {@code null} per azzerare la sessione
     */
    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    /**
     * Indica se esiste un utente autenticato nello stato applicativo.
     *
     * @return {@code true} se {@link #getLoggedUser()} è diverso da {@code null}, {@code false} altrimenti
     */
    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    /**
     * Indica se l'utente corrente è un amministratore.
     *
     * @return {@code true} se esiste un utente loggato e il suo tipo è {@link UserType#ADMIN},
     *         {@code false} altrimenti
     */
    public boolean isCurrentUserAdmin() {
        return loggedUser != null && loggedUser.getType() == UserType.ADMIN;
    }
}