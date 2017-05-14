package MainClient;

import java.awt.*;

/**
 * Created by Przemek on 17.12.2016.
 */

/**
 * Klasa dziedzicząca po MessagePopUp. Określa wiadomość wysłaną.
 */
public class MessageSentPopUp extends MessagePopUp {

    /**
     * Konstruktor klasy MessageSentPopUp
     * @param author Autor wiadomości.
     * @param message Treść wiadomości.
     * @param isFromHistory Zmienna określająca czy wiadomość jest nowa, czy pobierana z historii. Wiadomości z historii nie są animowane w oknie programu.Zmienna określająca czy wiadomość jest nowa, czy pobierana z historii. Wiadomości z historii nie są animowane w oknie programu.
     */
    MessageSentPopUp(String author, String message, boolean isFromHistory) {
        super(author, message, isFromHistory);
        setBackground(MESSAGE_SENT_COLOR);
    }
}
