package com.unina.bugboardapp.gui;

import com.unina.bugboardapp.StartApplication;
import com.unina.bugboardapp.controller.AppController;
import com.unina.bugboardapp.model.Issue;
import com.unina.bugboardapp.model.enums.IssueType;
import com.unina.bugboardapp.model.enums.Priority;
import com.unina.bugboardapp.model.enums.IssueState;
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
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));
        colReporter.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReporter().getUsername()));
    }

    /**
     * Inizializza filtri e pipeline dati della tabella (filtered + sorted) e imposta gli handler UI.
     */
    private void setupFiltersAndTable() {
        initializeFilterOptions();
        configureFilterCells();
        setupFilterListeners();
        setupTableRowDoubleClick();
    }

    /**
     * Popola le ComboBox dei filtri con le opzioni disponibili, includendo null per "Tutti".
     */
    private void initializeFilterOptions() {
        ObservableList<IssueType> typeOptions = FXCollections.observableArrayList();
        typeOptions.add(null);
        typeOptions.addAll(IssueType.values());
        typeFilter.setItems(typeOptions);

        ObservableList<IssueState> stateOptions = FXCollections.observableArrayList();
        stateOptions.add(null);
        stateOptions.addAll(IssueState.values());
        stateFilter.setItems(stateOptions);
    }

    /**
     * Configura la visualizzazione personalizzata delle celle dei filtri per mostrare "All" quando null.
     */
    private void configureFilterCells() {
        typeFilter.setButtonCell(createFilterCell("All Types"));
        typeFilter.setCellFactory(lv -> createFilterCell("All Types"));
        stateFilter.setButtonCell(createFilterCell("All States"));
        stateFilter.setCellFactory(lv -> createFilterCell("All States"));
    }

    /**
     * Crea una ListCell personalizzata che mostra un testo specifico quando il valore è null
     * @param nullText testo da mostrare per il valore null
     * @return ListCell configurata
     */
    private <T> ListCell<T> createFilterCell(String nullText) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? nullText : item.toString());
            }
        };
    }

    /**
     * Imposta i listener sui filtri e collega la pipeline FilteredList -> SortedList -> TableView.
     */
    private void setupFilterListeners() {
        FilteredList<Issue> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                updatePredicate(filteredData, newVal, typeFilter.getValue(), stateFilter.getValue()));

        typeFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                updatePredicate(filteredData, searchField.getText(), newVal, stateFilter.getValue()));

        stateFilter.valueProperty().addListener((obs, oldVal, newVal) ->
                updatePredicate(filteredData, searchField.getText(), typeFilter.getValue(), newVal));

        SortedList<Issue> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(issueTable.comparatorProperty());
        issueTable.setItems(sortedData);
    }

    /**
     * Aggiorna il predicato della FilteredList con i valori correnti dei filtri. Evita duplicazione di codice.
     */
    private void updatePredicate(FilteredList<Issue> filteredData, String searchText,
                                 IssueType type, IssueState state) {
        filteredData.setPredicate(issue -> isMatch(issue, searchText, type, state));
    }

    /**
     * Configura il doppio click sulle righe della tabella per aprire la vista dettaglio.
     */
    private void setupTableRowDoubleClick() {
        issueTable.setRowFactory(tv -> {
            TableRow<Issue> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
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
            stage.setTitle("Issue Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Failed to load issue detail view");
        }
    }
}