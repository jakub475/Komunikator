package Connection;

import CustomClasses.MessagePair;
import Listeners.ChannelListener;
import Listeners.MessageListener;
import ToSend.ScreenshotInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Created by Przemek on 29.01.2017.
 */

/**
 * Ekwiwalent klasy Client. Jego obiekt tworzony jest podczas pracy offline.
 */
public class ClientOffline implements Runnable{

    /**
     * Nazwa zalogowanego użytkownika.
     */
    private String userName = null;
    /**
     * Hasło zalogowanego użytkownika.
     */
    private String userPassword = null;
    /**
     * Instancja klasy ClientOffline.
     */
    private static ClientOffline client = null;
    /**
     * Lista słuchaczy wiadomości.
     */
    private Vector<MessageListener> messageListeners = new Vector<>();
    /**
     * Lista słuchaczy kanałów
     */
    private Vector<ChannelListener> channelListeners = new Vector<>();
    /**
     * Lista nazw kanałów.
     */
    private ArrayList<String> channelNames = null;

    /**
     * Instancja bazy danych lokalnej historii.
     */
    private LocalDatabase localHistory = null;

    public ClientOffline() {
        localHistory = new LocalDatabase();
    }

    /**
     * Zwraca instancje klasy singleton.
     * @return jedyna instancja klasy ClientOffline
     */
    public static ClientOffline getClientOffline()
    {
        if(client == null)
        {
            client = new ClientOffline();
            return client;
        }
        else
            return client;
    }

    /**
     * Metoda run wątku obsługującego pracę offline programu.
     */
    @Override
    public void run() {
        channelNames = localHistory.getAllChannelsFromHistory();
        String currentChannel;
        ArrayList<MessagePair> messageList = new ArrayList<>();
        for(ListIterator<String> it = localHistory.getAllChannelsFromHistory().listIterator(); it.hasNext();) {
            currentChannel = it.next();
            addNewChannel(currentChannel,new ArrayList<>());
            channelNames.add(currentChannel);
            messageList = localHistory.getMessagesFromChannel(currentChannel);
            for(MessagePair currentMessage : messageList) {
                newMessageShow(currentMessage.getKey(),currentChannel,currentMessage.getValue(),true);
            }
        }
    }

    /**
     * Metoda zwracająca nazwę aktualnie zalogowanego użytkownika.
     * @return nazwa użytkownika
     */
    public String getUserName(){
        return localHistory.getLastUsername();
    }


    /**
     * Funkcja dodaje słuchacza nowych wiadomości
     * @param newListener nowy słuchacz
     */
    public void addMessageListener(MessageListener newListener)
    {
        messageListeners.add(newListener);
    }

    /**
     * Funkcja dodaje słuchaczy utworzenia nowego kanału
     * @param channelListener obiekt słuchacza
     */
    public void addChannelListeners(ChannelListener channelListener) {
        this.channelListeners.add(channelListener);
    }

    /**
     * Funkcja służąca do dodawania nowego kanału
     * @param channelName nazwa nowego kanału
     * @param usersName Lista użytkowników znajdujących się w kanale
     */
    private void addNewChannel(String channelName, ArrayList<String> usersName){
        for(ChannelListener one :channelListeners)
        {
            one.addChannelLater(channelName,usersName);
        }
    }

    /**
     * Funkcja informująca wszystkie obiekty na liście messageListener o przyjściu nowej wiadomości
     * @param from - nazwa użytkownika od którego przyszła wiadomość
     * @param channelName nazwa kanału na który wiadomość została wysłana
     * @param newMessage treść wiadomości
     * @param history pole wyboru czy wiadomość została już odczytana
     */
    private void newMessageShow(String from, String channelName, String newMessage, boolean history){
        for(MessageListener oneListener: messageListeners)
        {
            oneListener.addNewMessage(from, channelName, newMessage, history);
        }
    }

    /**
     * Służy do podania nazwy użytkownika podawanej przy logowani do serwera
     * @param userName nazwa użytkownika
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Służy do podania hasła użytkownika podawanego przy logowaniu do serwera
     * @param userPassword hasło lub hash hasła
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Pobiera hasło użytkownika wcześniej przez niego wpisane.
     * @return hasło użytkownika
     */
    public String getUserPassword() {
        return this.userPassword;
    }

    /**
     * Funkcja zwracająca lokalną bazę danych.
     * @return Lokalna historia.
     */
    public LocalDatabase getLocalHistory() {
        return localHistory;
    }

}
