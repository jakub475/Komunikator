package MainClient;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Przemek on 01.12.2016.
 */

/**
 * Klasa, która wyświetla informacje o użytkownikach, ich nazwę oraz status połączenia do serwera, oraz kanałach.
 */
public class AccountStatusPanel extends JPanel {
    /**
     * Zmienna przechowująca pseudonim użytkownika.
     */
    private final String username;
    /**
     * Zmienna kulki widocznej w interfejsie przy nazwie użytkownika. Zmienia kolor w zależności od statusu połączenia z serwerem.
     */
    private ConnectStatusCaster connectStatusCaster;
    /**
     * Zmienna przechowująca informacje o kolorze, którym powinna być wypełniona kulka informująca o połączeniu.
     */
    private  Color logStatus = new Color(41, 218, 250);
    private Color chooseStatus = new Color(41, 170, 226);

    /**
     * Konstruktor klasy AccountStatusPanel.
     * @param name Pseudonim użytkownika, który będzie wyświetlany w interfejsie.
     */
    AccountStatusPanel(String name) {
        username = name;
        setPreferredSize(new Dimension(150, 20));
        setBackground(new Color(41, 170, 226));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new AccountCaster());
        connectStatusCaster = new ConnectStatusCaster();
        add(connectStatusCaster);
    }

    /**
     * Zmienia kolor kulki powiadomień w zależności od połączenia z serwerem.
     * @param log
     */
    public void changeStatus(boolean log){
        if(log){
            logStatus = new Color(41, 218, 0);
        }
        else{
            logStatus = new Color(41, 218, 250);
        }
    }

    /**
     * Zmienia tło przycisku zmiany kanału.
     * @param choose
     */
    public void changeBackground(boolean choose){
        if(choose){
            chooseStatus = MessagePopUp.MESSAGE_SENT_COLOR;
        }
        else{
            chooseStatus = new Color(41, 170, 226);
        }
    }

    /**
     * Klasa zagnieżdżona klasy AccountStatusPanel. Wykorzystywana do rysowania nazwy użytkownika w panelu informacyjnym.
     */
    class AccountCaster extends JPanel {
        /**
         * Konstruktor klasy AccountCaster.
         */
        AccountCaster() {
            setPreferredSize(new Dimension(110, 20));
            setBackground(new Color(41, 170, 226));
        }

        /**
         * Wyrysowuje pseudonim użytkownika.
         * @param g Zmienna graficzna
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.white);
            g2d.setFont(ApplicationWindow.UIFont.deriveFont(18f));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            setBackground(chooseStatus);
            g2d.drawString(username, 16, 13);
        }
    }

    /**
     * Klasa zagnieżdżona klasy AccountStatusPanel. Wykorzystywana do rysowania kulki statusu połączenia w panelu informacyjnym.
     */
    class ConnectStatusCaster extends JPanel {
        /**
         * Konstruktor klasy ConnectStatusCaster.
         */
        ConnectStatusCaster() {
            setPreferredSize(new Dimension(15, 20));
//            setBackground(new Color(41, 170, 226));
        }

        /**
         * Wyrysowuje kulkę statusu połączenia.
         * @param g Zmienna graficzna
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(logStatus);
            setBackground(chooseStatus);
            getParent().setBackground(chooseStatus);
            g.fillOval(0, 2, 10, 10);
        }
    }

    /**
     * Zwraca aktualny kolor przycisku.
     * @return
     */
    Color getStatusColor() {
        return chooseStatus;
    }
}
