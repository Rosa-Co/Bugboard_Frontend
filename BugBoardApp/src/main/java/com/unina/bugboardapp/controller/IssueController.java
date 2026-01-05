package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.exception.IssueException;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.model.enums.IssueState;
import com.unina.bugboardapp.model.enums.IssueType;
import com.unina.bugboardapp.model.enums.Priority;
import com.unina.bugboardapp.service.IssueService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller responsabile della gestione delle {@link Issue} lato client.
 * <p>
 * Incapsula la logica di:
 * <ul>
 *   <li>sincronizzazione delle issue dal backend nello stato applicativo ({@link AppState})</li>
 *   <li>creazione di nuove issue (con validazione input e aggiornamento della lista UI-bound)</li>
 *   <li>ottenimento di viste filtrate delle issue (per tipo, priorità e stato)</li>
 * </ul>
 * </p>
 *
 * <h2>Threading e UI</h2>
 * Le operazioni di rete verso il backend vengono eseguite in background tramite {@link Thread}
 * per evitare il blocco della UI. Le modifiche a strutture osservabili usate dalla UI
 * vengono effettuate nel JavaFX Application Thread mediante {@link Platform#runLater(Runnable)}.
 *
 * <h2>Stato applicativo</h2>
 * Le issue sono memorizzate in {@link AppState#getIssues()} come {@link ObservableList}, così
 * da consentire binding con componenti JavaFX (es. {@code TableView}).
 */
public class IssueController {
    private static final Logger logger = Logger.getLogger(IssueController.class.getName());
    private final IssueService issueService;
    private final AppState appState;

    /**
     * Crea un {@code IssueController} associato allo stato applicativo fornito.
     *
     * @param appState stato applicativo che contiene la lista osservabile delle issue e l'utente loggato
     */
    public IssueController(AppState appState) {
        this.appState = appState;
        this.issueService = new IssueService();
    }

    /**
     * Aggiorna l'elenco delle issue scaricandole dal backend e sincronizzandole nello stato applicativo.
     * <p>
     * L'operazione:
     * <ol>
     *   <li>avvia un thread in background</li>
     *   <li>recupera le issue tramite {@link IssueService#fetchAllIssues()}</li>
     *   <li>nel thread JavaFX svuota {@link AppState#getIssues()} e aggiunge le issue scaricate</li>
     * </ol>
     * </p>
     *
     * <p>
     * In caso di errore, l'eccezione viene registrata nel {@link Logger}. Il metodo non lancia
     * eccezioni verso il chiamante.
     * </p>
     */
    public void refreshData() {
        new Thread(() -> {
            try {
                List<Issue> realIssues = issueService.fetchAllIssues();
                Platform.runLater(() -> {
                    appState.getIssues().clear();
                    if (realIssues != null) {
                        appState.getIssues().addAll(realIssues);
                    }
                    logger.info("Dati aggiornati dal backend!");
                });
            } catch (IssueException e) {
                logger.warning("Impossibile scaricare le issue: " + e.getMessage());
            } catch (Exception e1) {
                logger.warning("Errore inaspettato durante il download dei dati: " + e1.getMessage());
            }
        }).start();
    }

    /**
     * Crea una nuova {@link Issue} e la invia al backend.
     * <p>
     * Prima della creazione vengono eseguite validazioni tramite {@link #validateIssueInput(String, String, IssueType, Priority, IssueState)}.
     * Se {@code imagePath} è valorizzato, viene normalizzato con {@code trim()} e impostato sulla issue.
     * </p>
     *
     * <p>
     * La richiesta di creazione viene eseguita in background tramite {@link IssueService#createIssue(Issue)}.
     * In caso di successo, nel JavaFX Application Thread l'issue creata viene aggiunta a
     * {@link AppState#getIssues()}.
     * </p>
     *
     * @param title       titolo della issue (non {@code null} e non blank)
     * @param description descrizione della issue (non {@code null} e non blank)
     * @param type        tipo della issue (non {@code null})
     * @param priority    priorità della issue (non {@code null})
     * @param imagePath   percorso immagine associata (opzionale; se {@code null} o blank viene ignorato)
     * @param state       stato iniziale della issue (non {@code null})
     * @throws IllegalStateException    se l'utente non è loggato
     * @throws IllegalArgumentException se i parametri non rispettano i vincoli di validazione
     */
    public void createIssue(String title, String description, IssueType type,
                            Priority priority, String imagePath, IssueState state) {
        validateIssueInput(title, description, type, priority, state);

        Issue newIssue = new Issue(type, title, description, null, state, priority, appState.getLoggedUser());
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            newIssue.setImagePath(imagePath.trim());
        }

        new Thread(() -> {
            try {
                Issue createdIssue = issueService.createIssue(newIssue);
                Platform.runLater(() -> {
                    if (createdIssue != null) {
                        appState.getIssues().add(createdIssue);
                        logger.info("Issue creata su server e UI");
                    }
                });
            } catch (IssueException e) {
                logger.log(Level.SEVERE, "Errore durante la creazione della Issue", e);
            } catch (Exception e1) {
                logger.log(Level.SEVERE, "Errore inaspettato durante la creazione della Issue", e1);
            }
        }).start();
    }

    /**
     * Valida i parametri necessari alla creazione di una issue.
     * <p>
     * Regole applicate:
     * <ul>
     *   <li>l'utente deve essere loggato ({@link AppState#isLoggedIn()})</li>
     *   <li>{@code title} non deve essere {@code null} né blank</li>
     *   <li>{@code description} non deve essere {@code null} né blank</li>
     *   <li>{@code type}, {@code priority} e {@code state} non devono essere {@code null}</li>
     * </ul>
     * </p>
     *
     * @param title       titolo della issue
     * @param description descrizione della issue
     * @param type        tipo della issue
     * @param priority    priorità della issue
     * @param state       stato della issue
     * @throws IllegalStateException    se l'utente non è loggato
     * @throws IllegalArgumentException se uno o più parametri non sono validi
     */
    private void validateIssueInput(String title, String description, IssueType type, Priority priority, IssueState state) {
        if (!appState.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to create issues");
        }
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Issue title cannot be empty");
        if (description == null || description.trim().isEmpty()) throw new IllegalArgumentException("Issue description cannot be empty");
        if (type == null || priority == null || state == null) throw new IllegalArgumentException("Issue type, state and priority cannot be null");
    }

    /**
     * Restituisce una lista osservabile di issue filtrate per {@link IssueType}.
     * <p>
     * Se {@code type} è {@code null}, viene restituita una nuova lista contenente tutte le issue
     * attualmente presenti nello stato (copia).
     * </p>
     *
     * @param type tipo di issue per il filtro; se {@code null} non viene applicato alcun filtro
     * @return lista osservabile con le issue filtrate per tipo
     */
    public ObservableList<Issue> getIssuesFiltered(IssueType type) {
        if (type == null) return FXCollections.observableArrayList(appState.getIssues());
        return appState.getIssues().stream()
                .filter(i -> i.getType() == type)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * Restituisce una lista osservabile di issue filtrate per {@link Priority}.
     * <p>
     * Se {@code priority} è {@code null}, viene restituita una nuova lista contenente tutte le issue
     * attualmente presenti nello stato (copia).
     * </p>
     *
     * @param priority priorità per il filtro; se {@code null} non viene applicato alcun filtro
     * @return lista osservabile con le issue filtrate per priorità
     */
    public ObservableList<Issue> getIssuesByPriority(Priority priority) {
        if (priority == null) return FXCollections.observableArrayList(appState.getIssues());
        return appState.getIssues().stream()
                .filter(i -> i.getPriority() == priority)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * Restituisce una lista osservabile di issue filtrate per {@link IssueState}.
     * <p>
     * Se {@code state} è {@code null}, viene restituita una nuova lista contenente tutte le issue
     * attualmente presenti nello stato (copia).
     * </p>
     *
     * @param state stato per il filtro; se {@code null} non viene applicato alcun filtro
     * @return lista osservabile con le issue filtrate per stato
     */
    public ObservableList<Issue> getIssuesByState(IssueState state) {
        if (state == null) return FXCollections.observableArrayList(appState.getIssues());
        return appState.getIssues().stream()
                .filter(i -> i.getState() == state)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
}