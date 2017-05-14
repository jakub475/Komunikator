package LoginWindow;

import javax.swing.*;
import java.awt.*;

import Connection.Client;
import Connection.ConfigFile;




/**
 * Created by Przemek on 03.11.2016.
 */

/**
 * Okno ekranu logowania.
 */
public class LoginFrame extends JFrame {

    /**
     * Konstruktor klasy LoginFrame.
     */
    public LoginFrame() {
        super("Latte Chat");
        setSize(600,200);
        getContentPane().setBackground(ConfigFile.getConfigFile().getBackgroundColor());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);

        JPanel logoPanel = new LogoPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(logoPanel, gbc);

        LoginFieldsPanel loginFields = new LoginFieldsPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(loginFields,gbc);
        setVisible(true);
        pack();
        setLocation(600,400);
        setSize(700,420);
        setMinimumSize(new Dimension(660,400));
        Client.getClient().setLoginFrame(this);

        ///////////
    }
}