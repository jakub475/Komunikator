package MainClient;

import Connection.Client;
import Connection.ClientOffline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Przemek on 29.01.2017.
 */

/**
 * Wersja klasy prawego panelu dla trybu offline.
 */
public class RightSideBarOffline extends RightSideBar {
    private JButton loginButton;
    /**
     * Konsturktor klasy RightSideBarOffline.
     */
    public RightSideBarOffline() {
        super();
        initLoginButton();
    }

    /**
     * Inicjacja przycisku, który nawiąże próbę logowania.
     */
    private void initLoginButton() {
        loginButton = new JButton("Zaloguj się");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryToConnect();
            }
        });
        this.expandedList.add(loginButton);
    }

    /**
     * Metoda umożliwiająca ponowną próbę zalogowania się do serwera podczas pracy offline.
     */
    void tryToConnect() {
        try{
            Client.getClient().setUserName(ClientOffline.getClientOffline().getUserName());
            Client.getClient().setUserPassword(ClientOffline.getClientOffline().getUserPassword());
            Client.getClient().startClient();
            new Thread(Client.getClient()).start();
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    SwingUtilities.getWindowAncestor(loginButton).dispose();
                }
            });
        }
        catch(IOException ioe)
        {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(loginButton),"Błąd logowania");

        }
    }

}
