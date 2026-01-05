package com.unina.bugboardapp.manager;

import com.unina.bugboardapp.model.User;
import com.unina.bugboardapp.model.enums.UserType;

/**
 * Gestore della sessione lato client.
 * <p>
 * Mantiene in memoria i dati della sessione corrente:
 * <ul>
 * <li>{@link #token}: token di autenticazione (se presente indica sessione
 * attiva)</li>
 * <li>{@link #user}: utente autenticato</li>
 * </ul>
 * </p>
 *
 * <h2>Pattern</h2>
 * Implementa un Singleton "lazy" (istanza creata al primo
 * {@link #getInstance()}).
 *
 * <h2>Note</h2>
 * Questa implementazione è pensata come storage in-memory: al riavvio
 * dell'applicazione
 * la sessione viene persa. Non gestisce persistenza né rinnovo token.
 */
public class SessionManager {
    private static SessionManager instance = null;
    private String token;
    private User user;

    /**
     * Costruttore privato per impedire istanziazioni esterne.
     */
    private SessionManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Restituisce l'istanza singleton del {@code SessionManager}.
     *
     * @return istanza unica del gestore sessione
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Esegue il logout azzerando token e utente.
     */
    public void logout() {
        this.token = null;
        this.user = null;
    }

    /**
     * Indica se esiste una sessione attiva.
     *
     * @return {@code true} se il token non è {@code null}, {@code false} altrimenti
     */
    public boolean isLoggedIn() {
        return token != null;
    }

    /**
     * Indica se l'utente corrente ha ruolo amministratore.
     *
     * @return {@code true} se l'utente è presente e il suo tipo è
     *         {@link UserType#ADMIN}
     */
    public boolean isAdmin() {
        return user != null && user.getType() == UserType.ADMIN;
    }

    /**
     * Imposta una sessione autenticata con utente e token.
     *
     * @param user  utente autenticato
     * @param token token associato alla sessione
     */
    public void login(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
