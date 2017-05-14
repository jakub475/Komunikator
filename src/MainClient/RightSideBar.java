package MainClient;
import Connection.Client;
import Listeners.ChannelListener;
import Listeners.UserLoggedListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Przemek on 27.11.2016.
 */

/**
 * Rozwijany prawy panel okna.
 */
public class RightSideBar extends JPanel implements UserLoggedListener, ChannelListener {

    /**
     * Kontener zawierający listę użytkowników.
     */
    private Vector<String> loggedInUserList = new Vector<>();

    /**
     * Podwójna hashmapa przechowująca listę kanałów z listą użytkowników poszczególnych kanałów.
     */
    private HashMap<String,HashMap<String, AccountStatusPanel>> channelListWithUsers = new HashMap<>();

    /**
     * Nazwa kanału.
     */
    private String activeChannelName = "general";
    /**
     * Przycisk potrzebny do wysuwania prawego panelu.
     */
    private final JLabel expandButton;
    /**
     * Rozwijana lista, w której znajdują się przyciski funkcyjne oraz lista użytkowników.
     */
    protected JPanel expandedList;
    /**
     * Zmienna przechowująca informację czy panel jest wysunięty czy nie.
     */
    private boolean isExpanded;
    /**
     * Wielkość, którą panel ma osiągnąć przy rozsuwaniu.
     */
    private final Dimension expandedSize;

    /**
     * Przycisk inicjalizujący rozmowę grupową.
     */
    private JButton groupConversationButton;
    /**
     * Przycisk inicjalizujący przesyłanie zrzutu ekranu.
     */
    private JButton screenshotShareButton;

    /**
     * Referencja na panel, w którym znajdują się wiadomości.
     */
    private MessagePanel messages;

    /**
     * Ikona przycisku w momencie gdy przycisk nie jest aktywny.
     */
    ImageIcon normalButton;
    /**
     * Ikona przycisku w momencie gdy przycisk jest aktywny.
     */
    ImageIcon activeButton;

    /**
     * Długość animacji wysuwania panelu.
     */
    private final static int EXPAND_TIME = 250;
    /**
     * Długość kroku animacji wysuwania.
     */
    private final static int EXPAND_STEP_DURATION = 5;

    /**
     * Konstruktor klasy RightSideBar.
     * Wywoływana w trybie online programu.
     * @param messages Referencja na panel, w którym znajdują się wiadomości.
     */
    RightSideBar(MessagePanel messages) {
        this.messages = messages;
        setBackground(Color.white);
        expandButton = new JLabel();
        try {
            normalButton = new ImageIcon(ImageIO.read(getClass().getResource("/resources/images/expandButton.png")));
            activeButton = new ImageIcon(ImageIO.read(getClass().getResource("/resources/images/activeExpandButton.png")));
            this.expandButton.setIcon(normalButton);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        this.expandButton.setPreferredSize(new Dimension(13, 280));
        this.expandButton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(41, 170, 226)));

        this.expandedList = new JPanel();
        setExpandedListSize(new Dimension(0, 0));
        this.expandedList.setLayout(new FlowLayout());
        this.expandedList.setBackground(Color.white);

        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        add(expandButton, BorderLayout.LINE_START);
        add(expandedList, BorderLayout.CENTER);
        setupListeners();
        this.isExpanded = false;
        expandedSize = new Dimension(150, 280);

        initGroupConversationButton();
        initScreenshotShareButton();
    }

    /**
     * Konstruktor klasy RightSideBar.
     * Wywoływana podczas pracy offline.
     */
    public RightSideBar() {
        super();
        setBackground(Color.white);
        expandButton = new JLabel();
        try {
            normalButton = new ImageIcon(ImageIO.read(getClass().getResource("/resources/images/expandButton.png")));
            activeButton = new ImageIcon(ImageIO.read(getClass().getResource("/resources/images/activeExpandButton.png")));
            this.expandButton.setIcon(normalButton);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        this.expandButton.setPreferredSize(new Dimension(13, 280));
        this.expandButton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(41, 170, 226)));

        this.expandedList = new JPanel();
        setExpandedListSize(new Dimension(0, 0));
        this.expandedList.setLayout(new FlowLayout());
        this.expandedList.setBackground(Color.white);

        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        add(expandButton, BorderLayout.LINE_START);
        add(expandedList, BorderLayout.CENTER);
        setupListeners();
        this.isExpanded = false;
        expandedSize = new Dimension(150, 280);
    }

    /**
     * Metoda inicjalizująca przycisk udostępniania zrzutu ekranowego.
     * Po jego naciśnięciu program wykonuje zrzut tego, co jest widoczne pod oknem programu.
     */
    private void initScreenshotShareButton() {
        this.screenshotShareButton = new JButton("Prześlij zrzut ekranu");
        this.expandedList.add(screenshotShareButton);
        this.screenshotShareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ScreenshotShare ss = new ScreenshotShare(expandedList, messages, activeChannelName);
            }
        });
    }

    /**
     * Metoda inicjalizująca przycisk tworzenia konwersacji grupowej.
     */
    private void initGroupConversationButton() {
        this.groupConversationButton = new JButton("Konwersacja grupowa");
        this.expandedList.add(groupConversationButton);
        this.groupConversationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                GroupConversationCreator gc = new GroupConversationCreator(channelListWithUsers.get("general").keySet());
            }
        });
    }

    //UserLoggedListener

    /**
     * Metoda ustawiająca wielkość panelu.
     * @param size Wielkość panelu.
     */
    void setExpandedListSize(Dimension size) {
        this.expandedList.setPreferredSize(size);
        updateUI();
    }

    /**
     * Dodaje nowego uczestniczącego w rozmowie użytkownika.
     * @param userName nazwa użytkownika
     */
    @Override
    synchronized public void userLoggedIn(String userName) {
        loggedInUserList.add(userName);

        for(HashMap<String, AccountStatusPanel> panel :channelListWithUsers.values()){
            AccountStatusPanel status = panel.get(userName);
            if(status!= null){
                status.changeStatus(true);
            }
        }

    }

    /**
     * Przyjmuje informacje o wylogowaniu się użytkownika.
     * @param userName nazwa użytkownika
     */
    @Override
    synchronized public void userLoggedOut(String userName) {
        loggedInUserList.remove(userName);
        if(channelListWithUsers.get(activeChannelName).containsKey(userName)){
            channelListWithUsers.get(activeChannelName).get(userName).changeStatus(false);
        }
    }

    /**
     * Rejetruje nowego użytkownika.
     * @param userName nazwa użytkownika
     */
    @Override
    public void newUserRegister(String userName) {
        AccountStatusPanel user = new AccountStatusPanel(userName);
        channelListWithUsers.get("general").put(userName,user);
        channelListWithUsers.get(activeChannelName).get(userName).changeStatus(true);
        expandedList.add(user);
    }

    /**
     * Aktualizacja interfejsu.
     */
    @Override
    synchronized public void rePaint() {
        super.revalidate();
        revalidate();
        repaint();
    }

    //ChannelListener

    /**
     * Implementacja słuchacza.
     * @param channelName nazwa kanału
     * @param usersName Lista użytkowników
     */
    @Override
    synchronized public void addChannelLater(String channelName, ArrayList<String> usersName) {
        if(channelListWithUsers.containsKey(channelName)){
            return;
        }

        HashMap<String, AccountStatusPanel> userInChannelList = new HashMap<>();

        for(String userName:usersName) {
            if(Client.getClient().getUserName().equals(userName))
                continue;

            AccountStatusPanel newStatus = new AccountStatusPanel(userName);
            newStatus.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);


                }
            });

            userInChannelList.put(userName, newStatus);
            if(channelName.equals(activeChannelName)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        expandedList.add(newStatus);
                        newStatus.changeStatus(loggedInUserList.contains(userName));
                        newStatus.repaint();
                    }
                });
            }
        }
        channelListWithUsers.put(channelName,userInChannelList);
    }

    @Override
    public void removeChannel(String channelName) {

    }

    /**
     * Zmiana aktywnego kanału.
     * @param activeChannelName Nazwa aktywnego kanału
     */
    @Override
    public void changeActiveChannel(String activeChannelName) {
        expandedList.removeAll();
        this.activeChannelName = activeChannelName;
        expandedList.add(groupConversationButton);
        expandedList.add(screenshotShareButton);
        for( AccountStatusPanel onePanel :channelListWithUsers.get(activeChannelName).values()){
            expandedList.add(onePanel);
            rePaint();
        }
    }

//

    /**
     * Metoda inicjalizująca listenery.
     */
    void setupListeners() {
        expandButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                changeIcon(activeButton);
                setBackground(ApplicationWindow.UIMainColor);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent e) {
                changeIcon(normalButton);
                setBackground(Color.white);
                setCursor(Cursor.getDefaultCursor());
            }

            public void mouseClicked(MouseEvent e) {
                if(isExpanded) {
                    HideList();
                }
                else {
                    ExpandList();
                }
            }
        });
    }

    /**
     * Zmienia wygląd przycisku w zależności od potrzeb.
     * @param button
     */
    void changeIcon(ImageIcon button) {
        this.expandButton.setIcon(button);
    }

    /**
     * Metoda animująca rozwijanie się prawego panelu.
     */
    synchronized void ExpandList() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                double widthStep = (double)EXPAND_STEP_DURATION/(double)EXPAND_TIME*150;
                int k = 1;
                while(expandedSize.getWidth() >= expandedList.getWidth()) {
                    setExpandedListSize(new Dimension((int)widthStep*k,280));
                    try {
                        Thread.sleep(EXPAND_STEP_DURATION);
                    } catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    k++;
                }
            }
        });
        t.start();
        isExpanded = true;
    }

    /**
     * Metoda animująca zwijanie się prawego panelu.
     */
    synchronized void HideList() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                double widthStep = (double)EXPAND_STEP_DURATION/(double)EXPAND_TIME*150;
                int k = 1;
                while(expandedList.getWidth() > 0) {
                    setExpandedListSize(new Dimension((int)(expandedSize.getWidth() - widthStep*k),280));
                    try {
                        Thread.sleep(EXPAND_STEP_DURATION);
                    } catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    k++;
                }
            }
        });
        t.start();
        isExpanded = false;
    }

    /**
     * Metoda zwracające nazwę aktualnie aktywnego kanału.
     * @return Nazwa kanału.
     */
    public String getActiveChannelName(){
        return this.activeChannelName;
    }


}
