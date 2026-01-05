package com.unina.bugboardapp.controller;

import com.unina.bugboardapp.exception.ApiException;
import com.unina.bugboardapp.exception.UserException;
import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.enums.UserType;
import com.unina.bugboardapp.service.UserService;
import javafx.application.Platform;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller responsabile delle operazioni sugli utenti lato client.
 * <p>
 * Fornisce funzionalità per:
 * <ul>
 *   <li>creare un nuovo utente (tipicamente riservato agli amministratori)</li>
 *   <li>verificare l'esistenza di un utente sul backend dato un indirizzo email</li>
 * </ul>
 * </p>
 *
 * <h2>Autorizzazioni</h2>
 * La creazione di utenti è consentita solo se l'utente corrente è amministratore
 * (verifica tramite {@link AppState#isCurrentUserAdmin()}).
 *
 * <h2>Threading e UI</h2>
 * Le operazioni di creazione utente che coinvolgono chiamate di rete sono eseguite in background
 * tramite {@link Thread}. Gli aggiornamenti alla lista osservabile di utenti, usata tipicamente
 * dalla UI, vengono effettuati sul JavaFX Application Thread tramite {@link Platform#runLater(Runnable)}.
 *
 * <h2>Gestione errori</h2>
 * Gli errori vengono registrati tramite {@link Logger}. Per la verifica di esistenza utente,
 * in caso di problemi di comunicazione viene adottato un comportamento “fail-safe” restituendo
 * {@code true} (considerando l'utente come esistente) per evitare duplicazioni o stati incoerenti.
 */
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserService userService;
    private final AppState appState;

    /**
     * Crea un {@code UserController} associato allo stato applicativo fornito.
     *
     * @param appState stato applicativo da cui leggere l'utente corrente e la lista utenti
     */
    public UserController(AppState appState) {
        this.appState = appState;
        this.userService = new UserService();
    }

    /**
     * Crea un nuovo utente sul backend e aggiorna lo stato applicativo.
     * <p>
     * Flusso operativo:
     * <ol>
     *   <li>verifica che l'utente corrente sia amministratore</li>
     *   <li>valida i parametri tramite {@link #validateUserInput(String, String, UserType)}</li>
     *   <li>normalizza l'email ({@code trim + lowercase})</li>
     *   <li>verifica che l'utente non sia già presente nello stato locale ({@link AppState#getUsers()})</li>
     *   <li>crea l'utente sul backend in background tramite {@link UserService#createUser(User)}</li>
     *   <li>in caso di successo, aggiunge l'utente creato a {@link AppState#getUsers()} nel thread JavaFX</li>
     * </ol>
     * </p>
     *
     * @param email    email/username del nuovo utente (non {@code null} e non blank)
     * @param password password del nuovo utente (non {@code null} e non vuota)
     * @param type     tipo del nuovo utente (non {@code null})
     * @throws IllegalStateException    se l'utente corrente non è un amministratore
     * @throws IllegalArgumentException se i parametri non sono validi o l'utente è già presente nello stato locale
     */
    public void createUser(String email, String password, UserType type) {
        if (!appState.isCurrentUserAdmin()) {
            throw new IllegalStateException("Only administrators can create users");
        }
        validateUserInput(email, password, type);

        String normalizedEmail = email.trim().toLowerCase();
        if (appState.getUsers().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(normalizedEmail))) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User newUser = new User(normalizedEmail, password, type);
        new Thread(() -> {
            try {
                User createdUser = userService.createUser(newUser);
                Platform.runLater(() -> {
                    if (createdUser != null) {
                        appState.getUsers().add(createdUser);
                        logger.info("User creato su server e UI");
                    }
                });
            } catch (UserException e) {
                logger.warning("Errore durante la creazione dell'utente: " + e.getMessage());
            } catch (Exception e1) {
                logger.warning("Errore inaspettato durante la creazione dell'utente: " + e1.getMessage());
            }
        }).start();
    }

    /**
     * Valida i parametri necessari per creare un utente.
     * <p>
     * Regole applicate:
     * <ul>
     *   <li>{@code email} non deve essere {@code null} né blank</li>
     *   <li>{@code password} non deve essere {@code null} né vuota</li>
     *   <li>{@code type} non deve essere {@code null}</li>
     * </ul>
     * </p>
     *
     * @param email    email/username da validare
     * @param password password da validare
     * @param type     tipo utente da validare
     * @throws IllegalArgumentException se uno o più parametri non rispettano i vincoli
     */
    private void validateUserInput(String email, String password, UserType type) {
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email cannot be empty");
        if (password == null || password.isEmpty()) throw new IllegalArgumentException("Password cannot be empty");
        if (type == null) throw new IllegalArgumentException("User type cannot be null");
    }

    /**
     * Verifica se esiste un utente con la specifica email sul backend.
     * <p>
     * Delega la chiamata a {@link UserService#existsUser(String)} e gestisce gli errori come segue:
     * <ul>
     *   <li>{@link ApiException}: se lo status code è 404, ritorna {@code false}; altrimenti rilancia l'eccezione</li>
     *   <li>{@link IOException}: log e ritorna {@code true} (fail-safe)</li>
     *   <li>{@link InterruptedException}: re-imposta il flag di interruzione, log e ritorna {@code true} (fail-safe)</li>
     *   <li>altre {@link Exception}: log e ritorna {@code true} (fail-safe)</li>
     * </ul>
     * </p>
     *
     * @param email email dell'utente da cercare
     * @return {@code true} se l'utente risulta esistente (o in caso di errore “fail-safe”),
     *         {@code false} se il backend risponde con 404 (utente non trovato)
     * @throws ApiException se il backend risponde con un errore diverso da 404 e l'eccezione viene rilanciata
     */
    public boolean existsUser(String email) {
        try {
            return userService.existsUser(email);
        } catch (ApiException e) {
            if (e.getStatusCode() == 404) return false;
            throw e;
        } catch (IOException e1) {
            logger.log(Level.SEVERE, "Errore durante la verifica dell'esistenza dell'utente: il server non ha risposto", e1);
            return true; // Fail-safe
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Errore durante la verifica dell'esistenza dell'utente: errore nella GET", e2);
            return true;
        } catch (Exception e3) {
            logger.log(Level.SEVERE, "Errore durante la verifica dell'esistenza dell'utente: errore generico", e3);
            return true;
        }
    }
}