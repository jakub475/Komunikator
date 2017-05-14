package MainClient;
import Listeners.ChannelListener;
import Listeners.MessageListener;

import Connection.ConfigFile;
import ToSend.ScreenshotInfo;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Przemek on 27.11.2016.
 */

/**
 * Panel znaldujący się po lewej stronie interfejsu graficznego. Znajduje się w nim logo, nazwa użytkownika, oraz lista kanałów.
 */
public class LeftSideBar extends JPanel implements ChannelListener, MessageListener {

    /**
     * Hashmapa ze wszystkimi kanałami w których znajduje się użytkownik.
     */
    private HashMap<String, AccountStatusPanel> channelList = new HashMap<>();
    /**
     * Lista ze wszytkimi słuchaczami kanałów.
     */
    private ArrayList<ChannelListener> channelListener = new ArrayList<>();
    /**
     * Nazwa aktualnie aktywnego kanału. Początkowo jest to kanał 'general'
     */
    private String activeChannelName = "general";

    /**
     * Logo programu.
     */
    private JLabel latteLogo;
    /**
     * Panel wyświetlający podstawowe informacje o użytkowniku: nazwę oraz status połączenia z serwerem.
     */
    private UserStatusPanel userStatusPanel;

    /**
     * Konsturktor klasy LeftSideBar.
     * @param username Nazwa użytkownika.
     * @param connectionStatus Status połączenia z serwerem.
     */
    LeftSideBar(String username,boolean connectionStatus) {
        super();
        setBackground(new Color(41, 170, 226));
        setPreferredSize(new Dimension(150, 400));
        FlowLayout layout = new FlowLayout();
        latteLogo = new JLabel(new ImageIcon("resources/images/logoApplication.png"));
        userStatusPanel =  new UserStatusPanel(username,connectionStatus);
        this.add(latteLogo);
        this.add(userStatusPanel);
        this.addChannelLater("general" , new ArrayList<>());
        setLayout(layout);
    }

    /**
     * Dodaje słuchacza kanału.
     * @param oneListener Słuchacz kanału.
     */
    public void addChannelListener(ChannelListener oneListener){
        channelListener.add(oneListener);
    }


    //ChannelListener

    /**
     * Dodaje nowy kanał - jego nazwa wyświetli się w lewym panelu.
     * @param channelName nazwa kanału
     * @param usersName Lista użytkowników
     */
    @Override
    public void addChannelLater(String channelName, ArrayList<String> usersName) {
        if(channelList.get(channelName) == null) {
            AccountStatusPanel newStatus = new AccountStatusPanel(channelName);
            if(channelName.equals("general"))
                newStatus.changeBackground(true);
            channelList.put(channelName, newStatus);
            newStatus.addMouseListener(new MouseAdapter() {
                Color tempColor = newStatus.getStatusColor();
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    System.out.println("Hey "+channelName);
                    changeActiveChannel(channelName);

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseEntered(e);
                    setCursor(Cursor.getDefaultCursor());
                }
            });
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    add(newStatus);
                    rePaint();
                }
            });
        }

    }

    /**
     * Usuwa kanał
     * @param channelName nazwa kanału
     */
    @Override
    public void removeChannel(String channelName) {
        remove(channelList.get(channelName));
        channelList.remove(channelName);
    }

    /**
     * Update interfejsu.
     */
    @Override
    public void rePaint() {
        super.revalidate();
        revalidate();
        repaint();
    }

    /**
     * Po kliknięciu w nazwę kanału, czat zaczyna wyświetlać wiadomości na nim zgromadzone.
     * @param activeChannelName Nazwa aktywnego kanału
     */
    @Override
    public void changeActiveChannel(String activeChannelName) {
        channelList.get(activeChannelName).changeStatus(false);
        channelList.get(this.activeChannelName).changeBackground(false);
        channelList.get(this.activeChannelName).repaint();
        this.activeChannelName = activeChannelName;
        channelList.get(activeChannelName).changeBackground(true);
        channelList.get(activeChannelName).repaint();

        //Powiadom słuchaczy o zdarzeniu zmiany kanału
        for(ChannelListener one: channelListener){
            one.changeActiveChannel(activeChannelName);
        }


    }

    // MessageListener


    @Override
    public void addNewMessage(String from, String message, boolean history) {

    }

    /**
     * Dodaje nową wiadomość do kanału.
     * @param from nadawca wiadomości
     * @param channelName nazwa kanału
     * @param message treść wiadomości
     * @param history pole informujące czy wiadomośc jest historyczna
     */
    @Override
    public void addNewMessage(String from, String channelName, String message, boolean history) {
        if(!activeChannelName.equals(channelName) && !history){
            channelList.get(channelName).changeStatus(true);
            rePaint();
        }
    }

    @Override
    public void addNewMessage(String from, String channelName, ScreenshotInfo sendPackedScreenshot, boolean history) {

    }
}
