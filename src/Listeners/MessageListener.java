package Listeners;

import ToSend.ScreenshotInfo;

/**
 * Created by Kuba on 18.12.2016.
 */

/**
 * Interfejs słuchacza przychodzących wiadomości
 */
public interface MessageListener {

    /**
     * Dodanie nowej wiadomości bezpośrednio do użytkownika
     * @param from nadawca wiadomości
     * @param message treść wiadomości
     * @param history pole informujące czy wiadomośc jest historyczna
     */
    public void addNewMessage(String from, String message, boolean history);

    /**
     * Dodanie nowej wiadomości do kanału
     * @param from nadawca wiadomości
     * @param channelName nazwa kanału
     * @param message treść wiadomości
     * @param history pole informujące czy wiadomośc jest historyczna
     */
    public void addNewMessage(String from, String channelName, String message, boolean history);

    /**
     *
     * Dodanie nowej wiadomości do kanału
     * @param from nadawca wiadomości
     * @param channelName nazwa kanału
     * @param sendPackedScreenshot zrzut ekranu
     * @param history pole informujące czy wiadomośc jest historyczna
     */
    public void addNewMessage(String from, String channelName, ScreenshotInfo sendPackedScreenshot, boolean history);

    /**
     * Odrysuj elementy interfejsu
     */
    public void rePaint();
}
