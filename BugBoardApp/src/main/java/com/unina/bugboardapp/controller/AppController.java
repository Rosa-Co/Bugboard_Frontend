package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.model.*;
import com.unina.bugboardapp.model.enums.IssueState;
import com.unina.bugboardapp.model.enums.IssueType;
import com.unina.bugboardapp.model.enums.Priority;
import com.unina.bugboardapp.model.enums.UserType;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

/**
 * Facciata (Facade) e punto di accesso centrale ai controller dell'applicazione.
 * <p>
 * Questa classe incapsula lo stato applicativo ({@link AppState}) e delega le operazioni
 * ai controller specializzati:
 * <ul>
 *   <li>{@link AuthenticationController} per autenticazione e logout</li>
 *   <li>{@link IssueController} per gestione e recupero delle issue</li>
 *   <li>{@link UserController} per creazione e verifica utenti</li>
 *   <li>{@link CommentController} per gestione commenti</li>
 * </ul>
 * </p>
 *
 * <h2>Pattern</h2>
 * Implementa il pattern Singleton tramite {@link #getInstance()} per garantire una sola istanza
 * condivisa nell'applicazione.
 *
 * <h2>Note sul threading</h2>
 * Alcune operazioni delegate (es. refresh, creazione issue/utente/commenti) possono essere eseguite
 * asincronamente dai controller sottostanti (tipicamente con aggiornamenti UI tramite JavaFX).
 */
public class AppController {

    private static AppController instance;
    private final AppState appState;
    private final AuthenticationController authController;
    private final IssueController issueController;
    private final UserController userController;
    private final CommentController commentController;

    private AppController() {
        this.appState = new AppState();
        this.issueController = new IssueController(appState);
        this.authController = new AuthenticationController(appState, issueController); // Auth triggera refresh issue
        this.userController = new UserController(appState);
        this.commentController = new CommentController(appState);
    }

    /**
     * Restituisce l'istanza Singleton di {@code AppController}.
     * <p>
     * Il metodo è sincronizzato per evitare race condition in fase di prima inizializzazione
     * in contesti multi-thread.
     * </p>
     *
     * @return istanza unica di {@code AppController}
     */
    public static synchronized AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }

    /**
     * Esegue il login dell'utente tramite {@link AuthenticationController}.
     * <p>
     * In caso di autenticazione riuscita, lo stato applicativo viene aggiornato con l'utente loggato
     * e può essere innescato un refresh dei dati (delegato al controller di autenticazione).
     * </p>
     *
     * @param email    email/username dell'utente
     * @param password password dell'utente
     * @return {@code true} se l'autenticazione va a buon fine, {@code false} altrimenti
     */
    public boolean login(String email, String password) {
        return authController.login(email, password);
    }

    /**
     * Effettua il logout dell'utente corrente.
     * <p>
     * Delegato a {@link AuthenticationController}, che azzera l'utente nello {@link AppState}.
     * </p>
     */
    public void logout() {
        authController.logout();
    }

    /**
     * Restituisce l'utente attualmente loggato.
     *
     * @return utente loggato, oppure {@code null} se nessun utente è autenticato
     */
    public User getLoggedUser() {
        return appState.getLoggedUser();
    }

    /**
     * Verifica se esiste un utente loggato nello stato applicativo.
     *
     * @return {@code true} se un utente è autenticato, {@code false} altrimenti
     */
    public boolean isLoggedIn() {
        return appState.isLoggedIn();
    }

    /**
     * Verifica se l'utente corrente è un amministratore.
     *
     * @return {@code true} se l'utente loggato esiste ed ha tipo {@link UserType#ADMIN},
     * {@code false} altrimenti
     */
    public boolean isCurrentUserAdmin() {
        return appState.isCurrentUserAdmin();
    }

    /**
     * Richiede l'aggiornamento dei dati delle issue dal backend.
     * <p>
     * Delegato a {@link IssueController#refreshData()}.
     * </p>
     */
    public void refreshData() {
        issueController.refreshData();
    }

    /**
     * Crea una nuova issue delegando la richiesta a {@link IssueController}.
     * <p>
     * Le validazioni (es. utente loggato, campi non null/blank) sono gestite dal controller delegato.
     * </p>
     *
     * @param title       titolo della issue
     * @param description descrizione della issue
     * @param type        tipologia della issue
     * @param priority    priorità della issue
     * @param imagePath   percorso immagine associata (opzionale, può essere {@code null} o vuoto)
     * @param state       stato iniziale della issue
     * @throws IllegalStateException    se l'utente non è loggato (in base alle regole del controller delegato)
     * @throws IllegalArgumentException se i parametri non rispettano i vincoli di validazione
     */
    public void createIssue(String title, String description, IssueType type,
                            Priority priority, String imagePath, IssueState state) {
        issueController.createIssue(title, description, type, priority, imagePath, state);
    }

    /**
     * Restituisce la lista osservabile di tutte le issue presenti nello stato applicativo.
     * <p>
     * La lista è quella mantenuta da {@link AppState}; eventuali aggiornamenti (es. refresh)
     * si rifletteranno su questa struttura.
     * </p>
     *
     * @return lista osservabile delle issue
     */
    public ObservableList<Issue> getAllIssues() {
        return appState.getIssues();
    }

    /**
     * Restituisce una lista osservabile di issue filtrate per tipo.
     *
     * @param type tipo di issue su cui filtrare; se {@code null} viene restituita una copia
     *             contenente tutte le issue
     * @return lista osservabile filtrata per {@code type}
     */
    public ObservableList<Issue> getIssuesFiltered(IssueType type) {
        return issueController.getIssuesFiltered(type);
    }

    /**
     * Restituisce una lista osservabile di issue filtrate per priorità.
     *
     * @param priority priorità su cui filtrare; se {@code null} viene restituita una copia
     *                 contenente tutte le issue
     * @return lista osservabile filtrata per {@code priority}
     */
    public ObservableList<Issue> getIssuesByPriority(Priority priority) {
        return issueController.getIssuesByPriority(priority);
    }

    /**
     * Restituisce una lista osservabile di issue filtrate per stato.
     *
     * @param state stato su cui filtrare; se {@code null} viene restituita una copia
     *              contenente tutte le issue
     * @return lista osservabile filtrata per {@code state}
     */
    public ObservableList<Issue> getIssuesByState(IssueState state) {
        return issueController.getIssuesByState(state);
    }

    /**
     * Crea un nuovo utente delegando a {@link UserController}.
     * <p>
     * Tipicamente richiede privilegi amministrativi; le verifiche e validazioni
     * sono gestite dal controller delegato.
     * </p>
     *
     * @param email    email/username del nuovo utente
     * @param password password del nuovo utente
     * @param type     tipo utente
     * @throws IllegalStateException    se l'utente corrente non è amministratore (in base alle regole del controller delegato)
     * @throws IllegalArgumentException se i parametri non sono validi o l'utente risulta già presente nello stato
     */
    public void createUser(String email, String password, UserType type) {
        userController.createUser(email, password, type);
    }

    /**
     * Verifica se esiste un utente con la specifica email.
     * <p>
     * La logica (incluse eventuali scelte "fail-safe" in caso di errori) è delegata a
     * {@link UserController#existsUser(String)}.
     * </p>
     *
     * @param email email dell'utente da cercare
     * @return {@code true} se l'utente risulta esistente, {@code false} altrimenti
     */
    public boolean existsUser(String email) {
        return userController.existsUser(email);
    }

    /**
     * Restituisce la lista osservabile di tutti gli utenti presenti nello stato applicativo.
     *
     * @return lista osservabile degli utenti
     */
    public ObservableList<User> getAllUsers() {
        return appState.getUsers();
    }

    /**
     * Aggiunge un commento ad una issue delegando a {@link CommentController}.
     * <p>
     * L'operazione può essere asincrona; in caso di successo, il commento creato può essere
     * inserito nella issue e la callback {@code onSuccess} (se non {@code null}) viene invocata.
     * </p>
     *
     * @param issue     issue a cui associare il commento (non {@code null})
     * @param content   contenuto del commento (non {@code null} e non vuoto)
     * @param onSuccess callback opzionale invocata con il {@link Comment} creato con successo
     * @throws IllegalStateException    se l'utente non è loggato (in base alle regole del controller delegato)
     * @throws IllegalArgumentException se {@code issue} è {@code null} o {@code content} non è valido
     */
    public void addComment(Issue issue, String content, Consumer<Comment> onSuccess) {
        commentController.addComment(issue, content, onSuccess);
    }

    /**
     * Carica i commenti associati ad una issue delegando a {@link CommentController}.
     * <p>
     * L'operazione è tipicamente asincrona; al completamento, i commenti ottenuti vengono impostati
     * sulla issue.
     * </p>
     *
     * @param issue issue per cui caricare i commenti
     */
    public void loadCommentsForIssue(Issue issue) {
        commentController.loadCommentsForIssue(issue);
    }
}