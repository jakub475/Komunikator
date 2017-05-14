package MainClient;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Przemek on 01.12.2016.
 */

/**
 * Klasa prezentująca informacje o innych użytkownikach czatu.
 */
public class UserStatusPanel extends JPanel {
    /**
     * Pseudonim użytkownika.
     */
    private final String username;

    /**
     * Zmienna przechowująca informację o statusie połączenia z serwerem.
     */
    private boolean isOnline;


    /**
     * Konstruktor klasy UserStatusPanel.
     * @param name Nazwa użytkownika.
     * @param connectionStatus Informacja o statusie połączenia z serwerem.
     */
    UserStatusPanel(String name,boolean connectionStatus) {
        username = name;
        isOnline = connectionStatus;
        setPreferredSize(new Dimension(150, 25));
        setBackground(new Color(41, 170, 226));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new UsernameCaster());
        add(new ConnectStatusCaster());
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    /**
     * Klasa zagnieżdżona, której rolą jest wyświetlanie nazwy użytkownika.
     */
    class UsernameCaster extends JPanel {
        /**
         * Konstruktor klasy UsernameCaster.
         */
        UsernameCaster() {
            setPreferredSize(new Dimension(110, 25));
            setBackground(new Color(41, 170, 226));
        }

        /**
         * Rysowanie nazwy użytkownika.
         * @param g zmienna graficzna
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.white);
            g2d.setFont(ApplicationWindow.UIFont.deriveFont(Font.BOLD,20f));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawString(username, 16, 16);
        }
    }

    /**
     * Klasa zagnieżdżona, której rolą jest wyświetlanie statusu użytkownika.
     */
    class ConnectStatusCaster extends JPanel {
        /**
         * Konstruktor klasy ConnectStatusCaster.
         */
        ConnectStatusCaster() {
            setPreferredSize(new Dimension(15, 20));
            setBackground(new Color(41, 170, 226));
        }

        /**
         * Rysowanie statusu połączenia użytkownika.
         * @param g zmienna graficzna
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(isOnline)
                g.setColor(new Color(41, 218, 0));
            else
                g.setColor(Color.white);
            g.fillOval(0, 0, 13, 13);
        }
    }
}
