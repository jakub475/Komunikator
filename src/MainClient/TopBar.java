package MainClient;
import Connection.Client;
import Listeners.ChannelListener;
import Listeners.UserLoggedListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Przemek on 27.11.2016.
 */

/**
 * Panel znajdujący się na górze interfejsu graficznego.
 */
public class TopBar extends JPanel implements UserLoggedListener, ChannelListener {
    /**
     * Nazwa aktualnie przeglądanego kanału.
     */
    private String channelName;
    /**
     * Liczba uczestników rozmowy (poza aktualnym użytkownikiem).
     */
    private int membersAmount;

    /**
     * Konstruktor klasy TopBar
     * @param title tytuł wyświetlający się na górze interfejsu
     * @param members liczba uczestników rozmowy
     */
    TopBar(String title, int members) {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(41, 170, 226)));
        setPreferredSize(new Dimension(474, 70));
        channelName = title;
        membersAmount = 0;
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        JPanel marginTop = new JPanel();
        JPanel marginBottom = new JPanel();
        JPanel marginLeft = new JPanel();
        marginTop.setPreferredSize(new Dimension(474, 20));
        marginBottom.setPreferredSize(new Dimension(474, 20));
        marginLeft.setPreferredSize(new Dimension(45, 30));
        marginTop.setBackground(Color.white);
        marginBottom.setBackground(Color.white);
        marginLeft.setBackground(Color.white);
        add(marginTop, BorderLayout.PAGE_START);
        add(marginBottom, BorderLayout.PAGE_END);
        add(marginLeft, BorderLayout.LINE_START);
        add(new TitleBar(), BorderLayout.CENTER);
        add(new MemberCounter(), BorderLayout.LINE_END);
        Client.getClient().addUserListener(this);
    }

    /**
     * Klasa zagnieżdżona zawierająca panel, w którym znajduje się nazwa kanału.
     */
    class TitleBar extends JPanel {
        /**
         * Konstruktor klasy TitleBar.
         */
        TitleBar() {
            setPreferredSize(new Dimension(200, 25));
            setBackground(Color.white);
        }

        /**
         * Rysowanie panelu TitleBar - nazwy aktualnego kanału.
         * @param g zmienna graficzna
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.black);
            g2d.setFont(ApplicationWindow.UIFont.deriveFont(25f));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawString(channelName, 1, 20);
        }
    }

    /**
     * Klasa zagnieżdżona zawierająca licznik uczestników rozmowy.
     */
    class MemberCounter extends JPanel {
        /**
         * Konstruktor klasy MemberCounter.
         */
        MemberCounter() {
            setBackground(Color.white);
            setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(41, 170, 226)));
            setPreferredSize(new Dimension(150, 20));
        }

        /**
         * Rysowanie panelu MemberCounter - liczby użytkowników.
         * @param g zmienna graficzna
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.black);
            g2d.setFont(ApplicationWindow.UIFont.deriveFont(25f));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawString(Integer.toString(membersAmount) + " Online", 15, 20);
        }
    }

    /**
     * Dodaje ilość aktywnych użytkowników podczas ich zalogowania.
     * @param userName nazwa użytkownika
     */
    @Override
    public void userLoggedIn(String userName) {
        if(!Client.getClient().getUserName().equals(userName))
            ++membersAmount;
    }

    @Override
    public void userLoggedOut(String userName) {
        --membersAmount;
    }

    @Override
    public void newUserRegister(String userName) {

    }

    /**
     * Aktualizacja interfejsu.
     */
    @Override
    public void rePaint() {
        revalidate();
        repaint();
    }

    //Channel Listener


    @Override
    public void addChannelLater(String channelName, ArrayList<String> usersName) {

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
        channelName = activeChannelName;
        rePaint();
    }
}
