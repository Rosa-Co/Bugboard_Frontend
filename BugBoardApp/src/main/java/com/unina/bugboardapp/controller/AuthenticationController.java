package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.exception.AuthenticationException;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.service.AuthService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller responsabile della gestione dell'autenticazione utente.
 * <p>
 * Coordina il processo di login/logout delegando le operazioni di autenticazione a {@link AuthService}
 * e aggiornando lo stato applicativo tramite {@link AppState}. In caso di login riuscito, innesca
 * anche un refresh delle issue tramite {@link IssueController} per sincronizzare i dati mostrati
 * nell'interfaccia.
 * </p>
 *
 * <h2>Logging</h2>
 * Registra messaggi informativi e di errore mediante {@link Logger}.
 */
public class AuthenticationController {
    private static final Logger logger = Logger.getLogger(AuthenticationController.class.getName());
    private final AuthService authService;
    private final AppState appState;
    private final IssueController issueController;

    /**
     * Crea un {@code AuthenticationController} associato allo stato applicativo e al controller delle issue.
     *
     * @param appState         stato applicativo da aggiornare con l'utente autenticato
     * @param issueController  controller usato per aggiornare l'elenco delle issue dopo un login riuscito
     */
    public AuthenticationController(AppState appState, IssueController issueController) {
        this.appState = appState;
        this.issueController = issueController;
        this.authService = new AuthService();
    }

    /**
     * Esegue il login con le credenziali fornite.
     * <p>
     * La procedura:
     * <ol>
     *   <li>valida che {@code email} e {@code password} non siano null/vuoti</li>
     *   <li>delega l'autenticazione a {@link AuthService#login(String, String)}</li>
     *   <li>se l'utente è restituito non-null, aggiorna {@link AppState#setLoggedUser(User)}</li>
     *   <li>richiede {@link IssueController#refreshData()} per scaricare le issue post-login</li>
     * </ol>
     * </p>
     *
     * @param email    email/username dell'utente (non {@code null} e non blank)
     * @param password password dell'utente (non {@code null} e non vuota)
     * @return {@code true} se il login va a buon fine, {@code false} in caso di credenziali non valide
     *         o errori gestiti
     */
    public boolean login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            logger.warning("Login failed: Invalid credentials provided");
            return false;
        }

        try {
            User user = authService.login(email, password);

            if (user != null) {
                appState.setLoggedUser(user);
                issueController.refreshData(); // Scarica i dati dopo il login
                logger.info("User logged in: " + user.getUsername() + " (" + user.getType() + ")");
                return true;
            }
        } catch (AuthenticationException e) {
            logger.log(Level.SEVERE, "Errore durante il login", e);
        } catch (Exception e1) {
            logger.log(Level.SEVERE, "Errore inaspettato durante il login", e1);
        }
        logger.warning("Login failed: Invalid email or password");
        return false;
    }

    /**
     * Effettua il logout dell'utente corrente.
     * <p>
     * Se presente un utente nello stato applicativo, viene tracciato un log e lo stato viene aggiornato
     * impostando {@code loggedUser} a {@code null}.
     * </p>
     */
    public void logout() {
        if (appState.getLoggedUser() != null) {
            logger.info("User logged out: " + appState.getLoggedUser().getUsername());
            appState.setLoggedUser(null);
        }
    }
}