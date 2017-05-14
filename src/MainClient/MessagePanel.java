package MainClient;
import Connection.Client;
import Connection.ConfigFile;
import Listeners.ChannelListener;
import Listeners.MessageListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import ToSend.ScreenshotInfo;

/**
 * Created by Przemek on 27.11.2016.
 */

/**
 * Klasa zawierająca w sobie wszystkie wiadomości wysyłane przez użytkowników.
 */
public class MessagePanel extends JPanel implements Scrollable, MessageListener,  ChannelListener {

    private String activeUser;
    /**
     * Kanał, w którym obecnie przebywamy.
     */
    private String activeChannel;
    private boolean useChannel;

    /**
     * Lista wiadomości wyświetlanych w kanale.
     */
    private ArrayList<MessagePopUp> MessagesList = new ArrayList<>();

    /**
     * Lista użytkowników na serwerze
     */
    ConcurrentHashMap<String,ArrayList<MessagePopUp>> userMessageList = new ConcurrentHashMap<>();
    /**
     * Mapa kanałów istniejących kanałów z przypisanymi do nich nazwami użytkownika
     */
    ConcurrentHashMap<String,ArrayList<MessagePopUp>> channelMessageList = new ConcurrentHashMap<>();
    /**
     * Nazwa użytkownika
     */
    private String userName;

    /**
     * Konstruktor klasy MessagePanel.
     */
    MessagePanel(String userName) {
        this.userName = userName;
        setBackground(Color.white);
        BoxLayout layout = new BoxLayout(this,BoxLayout.PAGE_AXIS);
        this.setLayout(layout);
        activeChannel = "general";
        useChannel = true;
        addChannel("general", new ArrayList<String>());
    }

    /**
     * Funkcja zwraca nazwę kanału z którym aktualnie prowadzimy rozmowę
     * @return nazwa kanału
     */
    public String getActiveName()
    {
        if(useChannel){
            return activeChannel;
        }
        else {
            return activeUser;
        }
    }

    /**
     * Funkcja sprawdzająca czy rozmowa którą prowadzimy jest w kanale czy bezpośrednio między użytkownikami
     * @return
     */
    public boolean getIsChannel(){
        return useChannel;
    }

    /**
     * Impelemntacja listenera
     * @return
     */
    public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;//set to 16 because that's what you had in your code.
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;//set to 16 because that's what you had set in your code.
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;//track the width, and re-size as needed.
    }

    public boolean getScrollableTracksViewportHeight() {
        return false; //we don't want to track the height, because we want to scroll vertically.
    }


    //MessageListener

    /**
     * Funkcja dodająca wiadomości bezpośrednio od użytkownika
     * @param from  nazwa użytkownika
     * @param message - treść wiadomości
     */
    @Override
    synchronized public void addNewMessage(String from, String message,boolean history) {

        MessagePopUp messagePopUp;
        System.out.println("Dodaje wiadomość");

        if(from.equals(userName)) {
            messagePopUp = new MessageSentPopUp(from, message, history);
        }
        else{
            messagePopUp = new MessageReceivedPopUp(from, message, history);
        }

        userMessageList.get(from).add(messagePopUp);
        if(from.equals(activeUser))
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    messagePopUp.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                    add(messagePopUp,0);
                    rePaint();
                }
            });
        }


    }

    /**
     * Funkcja dodająca wiadomość w kanale
     * @param from nadawca wiadomości
     * @param channelName nazwa kanału
     * @param message treść wiadomości
     * @param history pole oznaczające czy wiadomość została wysłana teraz czy jest archiwalna
     */
    @Override
    public void addNewMessage(String from, String channelName, String message, boolean history) {

        MessagePopUp messagePopUp;

        if(from.equals(userName)) {
            messagePopUp = new MessageSentPopUp(from, message, history);
        }
        else{
            messagePopUp = new MessageReceivedPopUp(from, message, history);
        }

        channelMessageList.get(channelName).add(messagePopUp);
        if(channelName.equals(activeChannel))
        {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    add(messagePopUp);
                    rePaint();
                    goToBottom();
                }
            });
        }

    }

    /**
     * Funkcja dodająca wiadomość ze zdjęciem.
     * @param from nadawca wiadomości
     * @param channelName nazwa kanału
     * @param history pole oznaczające czy wiadomość została wysłana teraz czy jest archiwalna
     */
    @Override
    public void addNewMessage(String from, String channelName, ScreenshotInfo sendPackedScreenshot, boolean history) {
        MessagePopUp messagePopUp =  new ScreenshotMessagePopUp(from, sendPackedScreenshot);

        channelMessageList.get(channelName).add(messagePopUp);
        if(channelName.equals(activeChannel))
        {
            MessagesList.add(messagePopUp);
            add(messagePopUp);
            rePaint();
            goToBottom();
        }
    }

    //MessageListener

    @Override
    synchronized public void rePaint() {
        this.revalidate();
    }

    /**
     * Funkcja służąca do dodawanie wiadomości nadanej przez użytkownika i oznaczająca go jako jego własną
     * @param message treść wiadomości
     * @param channelName nazwa kanału
     */
    public void addNewMyMessageNow(String message, String channelName) {

        MessagePopUp messagePopUp = new MessageSentPopUp(ConfigFile.getConfigFile().getUserName(), message, false);

        channelMessageList.get(activeChannel).add(messagePopUp);

        add(messagePopUp);
        rePaint();
        goToBottom();

    }

    /**
     * Funkcja dodająca powiadomienie o przesłaniu zdjęcia.
     * @param chanelName
     * @param screenshot
     */
    public void addNewMyMessageNow(String chanelName, ScreenshotInfo screenshot) {

        MessagePopUp messagePopUp = new ScreenshotMessagePopUp(ConfigFile.getConfigFile().getUserName(), screenshot);
        channelMessageList.get(chanelName).add(messagePopUp);
        if(chanelName.equals(activeChannel))
            add(messagePopUp);
        rePaint();
        goToBottom();
    }

    /**
     * Funkcja ustawiająca ScrollBar na dole
     */
    public void goToBottom() {
        int height = (int)this.getPreferredSize().getHeight();
        Rectangle rect = new Rectangle(0,height,10,10);
        this.scrollRectToVisible(rect);
    }





    // ChannelListener

    /**
     * Funkcja dodająca kanał do listy aktywnych kanałów
     * @param channelName
     * @param usersName
     */
    public void addChannel(String channelName, ArrayList<String> usersName) {
        channelMessageList.put(channelName,new ArrayList<>());
    }


    @Override
    public void addChannelLater(String channelName, ArrayList<String> usersName) {
        channelMessageList.put(channelName,new ArrayList<>());
    }

    /**
     * Funkcja usuwająca kanał z listy
     * @param channelName
     */
    @Override
    public void removeChannel(String channelName) {

    }

    /**
     * Funkcja zmieniająca aktualnie aktywny kanał
     * @param activeChannelName
     */
    @Override
    public void changeActiveChannel(String activeChannelName) {
        activeChannel = activeChannelName;
//        if(channelMessageList.get(activeChannelName).isEmpty())
//            Client.getClient().getChannelHistory(activeChannelName);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                removeAll();
                for(MessagePopUp messagePopUp : channelMessageList.get(activeChannelName)) {
                    messagePopUp.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                    add(messagePopUp);
                }

                goToBottom();
                rePaint();
            }
        });

    }

    /**
     * Aktualizacja interfejsu.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        if (MessagesList != null) {
            for (MessagePopUp i : MessagesList) {
                i.adjustSize();
                i.invalidate();
            }
        }
    }

}
