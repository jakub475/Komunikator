package MainClient;
import Connection.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Przemek on 28.11.2016.
 */

/**
 * Dolny panel służący do wpisywania treści wiadomości oraz ich wysyłania.
 */
public class BottomBar extends JPanel {

    /**
     * Referencja na panel, w którym wyświetlają się wiadomości.
     */
    private MessagePanel messages;
    /**
     * Pole tekstowe służące do wpisywania treści wiadomości.
     */
    private MessageTextField textField;

    /**
     * Konstruktor klasy BottomBar
     * @param textField Pole tekstowe służące do wpisywania treści wiadomości.
     * @param messages Referencja na panel, w którym wyświetlają się wiadomości.
     */
    BottomBar(MessageTextField textField, MessagePanel messages) {

        super();

        this.textField = textField;
        this.messages = messages;
        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(41, 170, 226)));
        setPreferredSize(new Dimension(474, 54));
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        textField.addKeyListener(new KeyListener(){

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    Client.getClient().send(messages.getActiveName(),textField.getText(),messages.getIsChannel());
                    messages.addNewMyMessageNow(textField.getText(), messages.getActiveName());
                    messages.rePaint();
                    textField.setText("");
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        } );
    }

    /**
     * Aktualizuje interfejs.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        textField.adjustSize();
    }
}
