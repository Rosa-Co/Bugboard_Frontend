package com.unina.bugboardapp.model.enums;

/**
 * Rappresenta lo stato di avanzamento di una {@code Issue}.
 * <p>
 * Ogni valore dell'enum espone una label “user-friendly” (es. per UI) accessibile tramite
 * {@link #getLabel()} e usata anche da {@link #toString()}.
 * </p>
 */
public enum IssueState {

    /**
     * Issue da svolgere / non ancora iniziata.
     */
    TODO("To Do"),

    /**
     * Issue attualmente in lavorazione.
     */
    IN_PROGRESS("In Progress"),

    /**
     * Issue completata.
     */
    DONE("Done");

    private final String label;

    /**
     * Costruttore dell'enum che associa una rappresentazione testuale allo stato.
     *
     * @param label etichetta leggibile (tipicamente mostrata in UI)
     */
    IssueState(String label) {
        this.label = label;
    }

    /**
     * Restituisce l'etichetta leggibile associata allo stato.
     *
     * @return label dello stato
     */
    public String getLabel() {
        return label;
    }

    /**
     * Restituisce la rappresentazione testuale dello stato.
     * <p>
     * Per convenzione, coincide con la label ritornata da {@link #getLabel()}.
     * </p>
     *
     * @return label dello stato
     */
    @Override
    public String toString() {
        return label;
    }
}