package MainClient;
import Connection.Client;
import Connection.ClientOffline;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * Created by Przemek on 27.11.2016.
 */

/**
 * Podstawowa klasa UI programu. Okno, które zawiera w sobie wszystkie elementy interfejsu graficznego głównego okna czatu.
 */
public class ApplicationWindow extends JFrame {

    /**
     * Czcionka wykorzystywana w programie.
     */
    public static Font UIFont = initializeFont();
    /**
     * Główny kolor wykorzystywany w programie.
     */
    public static Color UIMainColor = new Color(41, 170, 226);

    /**
     * Panel znajdujący się po lewej stronie głównego okna. Zawiera w sobie logo programu, informacje o użytkowniku oraz listę kanałów w których użytkownik jest członkiem.
     */
    private LeftSideBar leftSideBar;
    /**
     * Panel, w którym znajdują się wszystkie elementy prócz lewego sidebara.
     */
    private MainPanel mainPanel;
    /**
     * Pole tekstowe, w którym użytkownik wpisuje treść wiadomości, którą chce wysłać.
     */
    private MessageTextField textField;

    /**
     * Panel zawierający podstawowe informacje o użytkowniku.
     */
    private AccountStatusPanel accountStatusPanel;
    /**
     * Panel znajdujący się na górze okna. Wyświetla nazwę kanału w którym obecnie użytkownik przebywa, oraz informację o ilośći członków w nim aktywnych.
     */
    private TopBar topBar;
    /**
     * Rozwijany panel po prawej stronie okna. Znajduje się w nim lista użytkowników korzystających z czatu oraz dwa przyciski służące do utworzenia konwersacji grupowej oraz wysłania zrzutu ekranu.
     */
    private RightSideBar rightSideBar;
    /**
     * Panel, w którym znajdują się wszystkie wiadomości.
     */
    private MessagePanel messages;
    /**
     * Dolny panel służący do wpisywania treści wiadomości oraz ich wysyłania.
     */
    private BottomBar bottomBar;

    /**
     * JScrollPane umożliwiający przewijanie dymków wiadomości.
     */
    private JScrollPane messagesScrollPane;

    /**
     * Przycisk służący do wysyłania wiadomości.
     */
    private ConfirmButton sendButton;

    /**
     * Konstruktor klasy ApplicationWindow. Inicjalizuje wszystkie elementy interfejsu graficznego oraz ustala ich rozkład.
     * Ten konstruktor wywołuje się w trybie online.
     */
    ApplicationWindow() {

        super("Latte Chat");


        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Client.getClient().closeConnection();
            }
        });



        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(612, 412);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        //create SideBar and Main Panell
        leftSideBar = new LeftSideBar(Client.getClient().getUserName(),true);
        mainPanel = new MainPanel();

        //add things to leftSideBar

        accountStatusPanel = new AccountStatusPanel("general");

        //leftSideBar.add(accountStatusPanel);    // dodanie głównego kanału

        //create Text Fild

        textField = new MessageTextField("napisz wiadomość", 105);

        //add things to MainPanel


        topBar = new TopBar("czat główny", 5);
        messages = new MessagePanel(Client.getClient().getUserName());
        bottomBar = new BottomBar(textField, messages);
        rightSideBar = new RightSideBar(messages);


        messagesScrollPane = new JScrollPane(messages, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messagesScrollPane.setBackground(Color.white);

        mainPanel.add(messagesScrollPane, BorderLayout.CENTER);
        mainPanel.add(topBar, BorderLayout.PAGE_START);
        mainPanel.add(rightSideBar , BorderLayout.LINE_END);
        mainPanel.add(bottomBar, BorderLayout.PAGE_END);

        //BootomBar add


        textField.setFont(ApplicationWindow.UIFont.deriveFont(20f));
        sendButton = new ConfirmButton(messages,textField);

        bottomBar.add(textField);
        bottomBar.add(sendButton);


        // add leftSideBar and MainPanel to Aplication Window
        add(leftSideBar, BorderLayout.LINE_START);
        add(mainPanel, BorderLayout.CENTER);

        // add listeners

        Client.getClient().addMessageListener(messages);
        Client.getClient().addMessageListener(leftSideBar);

        Client.getClient().addUserListener(rightSideBar);

        Client.getClient().addChannelListeners(leftSideBar);
        Client.getClient().addChannelListeners(messages);
        Client.getClient().addChannelListeners(rightSideBar);

        leftSideBar.addChannelListener(rightSideBar);
        leftSideBar.addChannelListener(messages);
        leftSideBar.addChannelListener(topBar);


        setVisible(true);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Client.getClient().getChannelNames();
////                Client.getClient().getChannelHistory("general");
////                Client.getClient().getAllHistory();
//                Client.getClient().getLoggedUsers();
//            }
//        }).start();

    }
    /**
     * Konstruktor klasy ApplicationWindow. Inicjalizuje wszystkie elementy interfejsu graficznego oraz ustala ich rozkład.
     * Ten konstruktor wywołuje się w trybie offline.
     */
    ApplicationWindow(boolean connectionStatus) {
        super("Latte Chat");
        System.out.println("Tworze wersje offline");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(612, 412);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        //create SideBar and Main Panell
        leftSideBar = new LeftSideBar(ClientOffline.getClientOffline().getUserName(),false);
        mainPanel = new MainPanel();

        //add things to leftSideBar

        accountStatusPanel = new AccountStatusPanel("general");

        //leftSideBar.add(accountStatusPanel);    // dodanie głównego kanału

        //create Text Fild

        textField = new MessageTextField("Tryb offline", 105);
        textField.setEnabled(false);
        //add things to MainPanel


        topBar = new TopBar("czat główny", 5);
        messages = new MessagePanel(ClientOffline.getClientOffline().getUserName());
        bottomBar = new BottomBar(textField, messages);
        rightSideBar = new RightSideBarOffline();


        messagesScrollPane = new JScrollPane(messages, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messagesScrollPane.setBackground(Color.white);

        mainPanel.add(messagesScrollPane, BorderLayout.CENTER);
        mainPanel.add(topBar, BorderLayout.PAGE_START);
        mainPanel.add(rightSideBar , BorderLayout.LINE_END);
        mainPanel.add(bottomBar, BorderLayout.PAGE_END);

        //BootomBar add


        textField.setFont(ApplicationWindow.UIFont.deriveFont(20f));

        bottomBar.add(textField);


        // add leftSideBar and MainPanel to Aplication Window
        add(leftSideBar, BorderLayout.LINE_START);
        add(mainPanel, BorderLayout.CENTER);

        // add listeners

        ClientOffline.getClientOffline().addMessageListener(messages);
        ClientOffline.getClientOffline().addMessageListener(leftSideBar);

        //Client.getClient().addUserListener(rightSideBar);

        ClientOffline.getClientOffline().addChannelListeners(leftSideBar);
        ClientOffline.getClientOffline().addChannelListeners(messages);

        leftSideBar.addChannelListener(messages);
        leftSideBar.addChannelListener(topBar);
        new Thread(ClientOffline.getClientOffline()).start();
        setVisible(true);
    }

    /**
     * Pobiera plik czcionki z pliku zamieszczonego w projekcie i umieszcza w programie.
     * @return Czcionka, którą używa program.
     */
    static Font initializeFont() {
        Font font;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            font = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/segoeuil.ttf"));
            ge.registerFont(font);
        } catch(IOException|FontFormatException e) {
            font = new Font("Arial", Font.PLAIN, 20);
        }
        return font;
    }



}
