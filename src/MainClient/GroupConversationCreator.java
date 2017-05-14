package MainClient;

import Connection.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Przemek on 12.01.2017.
 */

/**
 * Klasa okna służąca do stworzenia konwersacji grupowej.
 */
public class GroupConversationCreator extends JFrame{

    /**
     * Checkbox w którym wyświetla się lista wszystkich użytkowników.
     */
    private JPanel checkBoxPanel;
    /**
     * Lista przechowująca checkboxy użytkowników.
     */
    private ArrayList<JCheckBox> checkBoxList;
    /**
     * Przycisk do potwierdzenia chęci założenia kanału.
     */
    private JButton confirmButton;
    /**
     * Pole tekstowe do nadania nazwy kanałowi.
     */
    private JTextField channelName;

    /**
     * Konstruktor klasy GroupConversationCreator
     * @param userSet Kontener zawierający wszystkich użytkowników, których można dodać do konwersacji grupowej.
     */
    public GroupConversationCreator(Set<String> userSet) {
        super();
        this.checkBoxPanel = new JPanel();
        this.setLayout(new FlowLayout());

        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel,BoxLayout.PAGE_AXIS));
        initCheckBoxes(userSet);

        this.add(checkBoxPanel);
        channelName = new JTextField();
        channelName.setPreferredSize(new Dimension(60,20));
        this.add(channelName);
        initConfirmButton();
        this.setMinimumSize(new Dimension(200,200));
        setVisible(true);
        pack();
    }

    /**
     * Metoda inicjalizująca checkboxy.
     * @param userSet Kontener zawierający wszystkich użytkowników, których można dodać do konwersacji grupowej.
     */
    private void initCheckBoxes(Set<String> userSet) {
        checkBoxList = new ArrayList<>();
        for(String i: userSet) {
            JCheckBox checkBox = new JCheckBox(i);
            checkBoxList.add(checkBox);
            checkBoxPanel.add(checkBox);
        }
    }

    /**
     * Metoda inicjalizująca przycisk do potwierdzenia stworzenia konwersacji grupowej.
     */
    private void initConfirmButton() {
        this.confirmButton = new JButton("Utwórz konwersację grupową");
        this.checkBoxPanel.add(confirmButton);
        this.confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createConversationUsersList();
                dispose();
            }
        });
    }

    /**
     * Metoda tworząca listę użytkowników, którzy zostali wybrani do utworzenia rozmowy grupowej.
     * @return Lista użytkowników do rozmowy grupowej.
     */
    private ArrayList<String> createConversationUsersList() {
        ArrayList<String> userList = new ArrayList<>();

        for(JCheckBox i: checkBoxList) {
            if(i.isSelected()) {
                userList.add(i.getText());
            }
        }
        userList.add(Client.getClient().getUserName());
        //System.out.println(userList);
        Client.getClient().createChannel(channelName.getText(),userList);
        return userList;
    }

}
