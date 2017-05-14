package Listeners;

/**
 * Created by Kuba on 18.12.2016.
 */

/**
 * Interfejs słuchacza logowani i wylogowywania użytkowników
 */
public interface UserLoggedListener {
    /**
     * Użytkownik zalogował się
     * @param userName nazwa użytkownika
     */
    public void userLoggedIn(String userName);

    /**
     * Użytkownik wylogował się
     * @param userName nazwa użytkownika
     */
    public void userLoggedOut(String userName);

    /**
     * Ponowne rysowanie interejsu
     */
    public void rePaint();

    /**
     * Nowy użytkownik zalogował się
     * @param userName nazwa użytkownika
     */
    public void newUserRegister(String userName);
}
