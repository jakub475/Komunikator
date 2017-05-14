package Listeners;

import java.util.ArrayList;

/**
 * Created by Kuba on 02.01.2017.
 */

/**
 * Interfejs słuchacza, implementowany dla klas które są informowane o zmianie kanału
 */
public interface ChannelListener {
    /**
     * Dodano nowy kanał
     * @param channelName nazwa kanału
     * @param usersName Lista użytkowników
     */
    public void addChannelLater(String channelName, ArrayList<String> usersName);

    /**
     * Usunięcie kanału
     * @param channelName nazwa kanału
     */
    public void removeChannel(String channelName);
    public void rePaint();

    /**
     * Zmiana aktywnego kanału
     * @param activeChannelName Nazwa aktywnego kanału
     */
    public void changeActiveChannel(String activeChannelName);
}
