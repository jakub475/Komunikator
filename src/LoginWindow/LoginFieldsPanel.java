package LoginWindow;

import Connection.Client;
import Connection.ClientOffline;
import Connection.ConfigFile;
import MainClient.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Created by Przemek on 27.11.2016.
 */

/**
 * Panel w którym znajdują się pola tekstowe, w których użytkownik wpisuje swoje dane podczas logowania.
 */
public class LoginFieldsPanel extends JPanel {
    private ConfirmButton login;
    /**
     * Konstruktor klasy LoginFieldsPanel. Inicjowane są w nim wszystkie elementy tego panelu.
     */
    LoginFieldsPanel() {
        setBackground(ConfigFile.getConfigFile().getBackgroundColor());
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,0,10,0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        UsernameTextField ussName = new UsernameTextField(ConfigFile.getConfigFile().getUserName(),18);
        add(ussName,gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        PasswordTextField passField = new PasswordTextField(ConfigFile.getConfigFile().getUserPassword(),18);
        add(passField,gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        login = new ConfirmButton("Zaloguj");
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hey");
                try{
                    Client.getClient().setUserName(ussName.getText());
                    Client.getClient().setUserPassword(passField.getText());
                    Client.getClient().startClient();
                    new Thread(Client.getClient()).start();
//                    EventQueue.invokeLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            SwingUtilities.getWindowAncestor(login).dispose();
//                        }
//                    });

                }
                catch(IOException ioe)
                {
                    ClientOffline.getClientOffline().setUserName(ussName.getText());
                    ClientOffline.getClientOffline().setUserPassword(passField.getText());
                    if(ussName.getText().equals(ClientOffline.getClientOffline().getLocalHistory().getLastUsername())) {
                        Object[] options = {"Tak", "Nie"};
                        int result = JOptionPane.showOptionDialog(null, "Nie udało się nawiązać połączenia z serwerem.\nCzy chcesz przeglądać wiadomości w trybie offline?", "Warning",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                        if (result == 0) {
                            System.out.println(ClientOffline.getClientOffline().getLocalHistory().getLastPassword());
                            System.out.println(passField.getText());
                            if(ClientOffline.getClientOffline().getLocalHistory().getLastPassword().equals(passField.getText())) {
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        SwingUtilities.getWindowAncestor(login).dispose();
                                    }
                                });
                                new MainWindow(false);
                            }
                            else {
                                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(login),"Podałeś błędne hasło.");
                            }
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(login),"Brak połączenia.\nNie jesteś ostatnim użytkownikiem, a tylko dla niego dostępna jest historia.");
                    }
                }


            }
        });
        add(login,gbc);

        setVisible(true);
    }

    /**
     * Klasa zagnieżdżona klasy LoginFieldsPanel. Pole tekstowe o predefiniowanym wyglądzie.
     */
    class UsernameTextField extends JTextField {
        UsernameTextField(String text, int columns) {
            super(text, columns);
            setBackground(Color.white);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(62, 171,249),1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            Font fieldFont = new Font("Arial", Font.ITALIC, 20);
            setFont(fieldFont);
            setPreferredSize(new Dimension(200,35));
            setForeground(new Color(106,106,106));

        }
    }

    /**
     * Klasa zagnieżdżona klasy LoginFieldsPanel. Pole tekstowe o predefiniowanym wyglądzie.
     */
    class PasswordTextField extends JPasswordField {
        PasswordTextField(String text, int columns) {
            super(text, columns);
            setBackground(Color.white);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(62, 171,249),1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            Font fieldFont = new Font("Arial", Font.ITALIC, 20);
            setFont(fieldFont);
            setPreferredSize(new Dimension(200,35));
            setForeground(new Color(106,106,106));
        }
    }

    /**
     * Klasa zagnieżdżona klasy LoginFieldsPanel. Przycisk potwierdzający chęć zalogowania się.
     */
    class ConfirmButton extends JButton  {
        ConfirmButton(String text) {
            super(text);
            setPreferredSize(new Dimension(100,30));
            setBackground(new Color(62, 171,249));
            setForeground(Color.white);
            setFocusPainted(false);
            setupListeners();
        }

        /**
         * Prosta metoda inicjalizująca słuchacze.
         */
        void setupListeners() {
            this.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                }
            });
        }
    }
}
