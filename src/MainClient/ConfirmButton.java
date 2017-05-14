package MainClient;

import Connection.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Przycisk służący do wysyłania wiadomości.
 */
class ConfirmButton extends JButton {
    /**
     * Ikona przycisku w momencie gdy przycisk nie jest aktywny.
     */
    ImageIcon normalButton;
    /**
     * Ikona przycisku w momencie gdy przycisk jest aktywny.
     */
    ImageIcon activeButton;

    /**
     * Pole tekstowe służacę do wysyłania wiadomości.
     */
    private MessageTextField textField;
    /**
     * Referencja na panel, w którym wyświetlają się wiadomości.
     */
    private MessagePanel messages;

    /**
     * Konstruktor klasy ConfirmButton
     * @param messages Referencja na panel, w którym wyświetlają się wiadomości.
     * @param textField Pole tekstowe służące do wpisywania treści wiadomości.
     */
    public ConfirmButton(MessagePanel messages, MessageTextField textField) {
        this.messages = messages;
        this.textField = textField;

        setPreferredSize(new Dimension(35,32));
        setBackground(Color.white);
        setBorder(BorderFactory.createEmptyBorder());
        try {
            normalButton = new ImageIcon(ImageIO.read(getClass().getResource("/resources/images/confirmButton.png")));
            activeButton = new ImageIcon(ImageIO.read(getClass().getResource("/resources/images/activeConfirmButton.png")));
            this.setIcon(normalButton);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        setupListeners();
    }

    /**
     * Metoda inicjalizująca listenery przycisku.
     */
    void setupListeners() {
        this.addMouseListener(new MouseAdapter() {
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
                super.mouseClicked(e);
                Client.getClient().send(messages.getActiveName(),textField.getText(),messages.getIsChannel());
                messages.addNewMyMessageNow(textField.getText(), messages.getActiveName());
                messages.rePaint();
                textField.setText("");
            }
        });
    }

    /**
     * Metoda zmieniająca wygląd przycisku w zależności od potrzeb.
     * @param button Wygląd przycisku.
     */
    void changeIcon(ImageIcon button) {
        this.setIcon(button);
    }
}