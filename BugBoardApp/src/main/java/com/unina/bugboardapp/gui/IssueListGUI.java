package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.controller.AppController;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.model.IssueType;
import com.unina.bugboardapp.model.Priority;
import com.unina.bugboardapp.model.IssueState;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.logging.Logger;

import java.io.IOException;

/**
 * Controller JavaFX della vista di elenco delle {@link Issue}.
 * <p>
 * Fornisce:
 * <ul>
 *   <li>Visualizzazione tabellare con colonne (id, titolo, tipo, priorità, stato, reporter);</li>
 *   <li>Filtri per testo libero (titolo/descrizione), tipo e stato;</li>
 *   <li>Ordinamento tramite {@link TableView} (supportato da {@link SortedList});</li>
 *   <li>Apertura della finestra di creazione issue;</li>
 *   <li>Apertura della vista di dettaglio con doppio click su una riga.</li>
 * </ul>
 * </p>
 *
 * <h2>Dati</h2>
 * La tabella si basa su una lista locale {@link #masterData} sincronizzata (binding) con la lista
 * restituita da {@link AppController#getAllIssues()}. I filtri sono applicati tramite
 * {@link FilteredList} e l'ordinamento tramite {@link SortedList}.
 */
public class IssueListGUI {

    /**
     * Campo di ricerca testuale (filtra su titolo e descrizione).
     */
    @FXML
    private TextField searchField;

    /**
     * Filtro per tipo issue.
     */
    @FXML
    private ComboBox<IssueType> typeFilter;

    /**
     * Filtro per stato issue.
     */
    @FXML
    private ComboBox<IssueState> stateFilter;

    /**
     * Tabella di visualizzazione delle issue.
     */
    @FXML
    private TableView<Issue> issueTable;

    @FXML
    private TableColumn<Issue, Integer> colId;
    @FXML
    private TableColumn<Issue, String> colTitle;
    @FXML
    private TableColumn<Issue, IssueType> colType;
    @FXML
    private TableColumn<Issue, Priority> colPriority;
    @FXML
    private TableColumn<Issue, IssueState> colState;
    @FXML
    private TableColumn<Issue, String> colReporter;

    /**
     * Lista "master" locale a cui vengono applicati filtri e ordinamento.
     * <p>
     * Viene mantenuta sincronizzata con la lista sorgente esposta dall'applicazione.
     * </p>
     */
    private final ObservableList<Issue> masterData = FXCollections.observableArrayList();

    /**
     * Logger della classe, usato per registrare errori nel caricamento delle viste.
     */
    private static final Logger logger = Logger.getLogger(IssueListGUI.class.getName());

    /**
     * Inizializza la vista dopo l'iniezione dei campi FXML.
     * <p>
     * Esegue:
     * <ol>
     *   <li>Configurazione delle colonne della tabella;</li>
     *   <li>Configurazione dei filtri e del comportamento della tabella (doppio click);</li>
     *   <li>Binding dei dati locali {@link #masterData} con la lista restituita da
     *       {@link AppController#getAllIssues()}.</li>
     * </ol>
     * </p>
     */
    @FXML
    public void initialize() {
        setupColumns();
        setupFiltersAndTable();
        ObservableList<Issue> sourceList = AppController.getInstance().getAllIssues();
        javafx.beans.binding.Bindings.bindContent(masterData, sourceList);
    }

    /**
     * Configura il mapping tra proprietà del modello {@link Issue} e colonne della {@link #issueTable}.
     * <p>
     * La colonna reporter è calcolata a partire da {@code issue.getReporter().getUsername()}.
     * </p>
     */
    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));
        colReporter.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReporter().getUsername()));
    }

    /**
     * Inizializza filtri e pipeline dati della tabella (filtered + sorted) e imposta gli handler UI.
     * <p>
     * Imposta:
     * <ul>
     *   <li>Valori disponibili nei combo filtro (tutti i valori delle enum);</li>
     *   <li>Una {@link FilteredList} basata su {@link #masterData} con predicato aggiornato dai listener;</li>
     *   <li>Una {@link SortedList} con comparatore agganciato a quello della {@link #issueTable};</li>
     *   <li>Una row factory che apre il dettaglio con doppio click.</li>
     * </ul>
     * </p>
     */
    private void setupFiltersAndTable() {
        typeFilter.setItems(FXCollections.observableArrayList(IssueType.values()));
        stateFilter.setItems(FXCollections.observableArrayList(IssueState.values()));

        FilteredList<Issue> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filteredData
                .setPredicate(issue -> isMatch(issue, newVal, typeFilter.getValue(), stateFilter.getValue())));

        typeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filteredData
                .setPredicate(issue -> isMatch(issue, searchField.getText(), newVal, stateFilter.getValue())));

        stateFilter.valueProperty().addListener((obs, oldVal, newVal) -> filteredData
                .setPredicate(issue -> isMatch(issue, searchField.getText(), typeFilter.getValue(), newVal)));

        SortedList<Issue> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(issueTable.comparatorProperty());
        issueTable.setItems(sortedData);

        issueTable.setRowFactory(tv -> {
            TableRow<Issue> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openDetailView(row.getItem());
                }
            });
            return row;
        });
    }

    /**
     * Verifica se una {@link Issue} soddisfa i criteri di filtro correnti.
     * <p>
     * Il match testuale viene effettuato (case-insensitive) su titolo e descrizione.
     * I filtri di tipo e stato, se non nulli, richiedono uguaglianza esatta con i valori della issue.
     * </p>
     *
     * @param issue      issue da valutare
     * @param searchText testo cercato; se {@code null} o vuoto non viene applicato filtro testuale
     * @param type       tipo richiesto; se {@code null} non viene applicato filtro per tipo
     * @param state      stato richiesto; se {@code null} non viene applicato filtro per stato
     * @return {@code true} se la issue passa tutti i filtri, {@code false} altrimenti
     */
    private boolean isMatch(Issue issue, String searchText, IssueType type, IssueState state) {
        boolean matchText = true;
        if (searchText != null && !searchText.isEmpty()) {
            String lowerCaseFilter = searchText.toLowerCase();
            matchText = issue.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                    issue.getDescription().toLowerCase().contains(lowerCaseFilter);
        }

        boolean matchType = true;
        if (type != null) {
            matchType = issue.getType() == type;
        }

        boolean matchState = true;
        if (state != null) {
            matchState = issue.getState() == state;
        }

        return matchText && matchType && matchState;
    }

    /**
     * Handler del click su "New Issue".
     * <p>
     * Apre una finestra modale con la vista di creazione issue ({@code issue-create-view.fxml}).
     * </p>
     * <p>
     * In caso di errore di caricamento (I/O), registra un messaggio nel logger.
     * </p>
     *
     * @param event evento JavaFX associato all'azione; può essere {@code null}
     */
    @FXML
    void onNewIssue(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("issue-create-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Issue");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.severe("Failed to load issue create view");
        }
    }

    /**
     * Apre la vista di dettaglio per una specifica {@link Issue}.
     * <p>
     * Carica {@code issue-detail-view.fxml}, imposta la issue nel relativo controller
     * {@link IssueDetailGUI} tramite {@link IssueDetailGUI#setIssue(Issue)} e mostra una nuova finestra
     * (non bloccante) in modalità {@link Modality#APPLICATION_MODAL}.
     * </p>
     *
     * @param issue issue di cui mostrare i dettagli; non dovrebbe essere {@code null}
     */
    private void openDetailView(Issue issue) {
        try {
            FXMLLoader loader = new FXMLLoader(StartApplication.class.getResource("issue-detail-view.fxml"));
            Parent root = loader.load();

            IssueDetailGUI controller = loader.getController();
            controller.setIssue(issue);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Issue #" + issue.getId());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Failed to load issue detail view");
        }
    }
}