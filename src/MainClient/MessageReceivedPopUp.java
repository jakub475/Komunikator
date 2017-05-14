package MainClient;


import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Created by Przemek on 17.12.2016.
 */

/**
 * Klasa dziedzicząca po MessagePopUp. Określa wiadomość otrzymaną.
 */
public class MessageReceivedPopUp extends MessagePopUp{

    /**
     * Konstruktor klasy MessageReceivedPopUp
     * @param author Autor wiadomości.
     * @param message Treść wiadomości.
     * @param isFromHistory Zmienna określająca czy wiadomość jest nowa, czy pobierana z historii. Wiadomości z historii nie są animowane w oknie programu.Zmienna określająca czy wiadomość jest nowa, czy pobierana z historii. Wiadomości z historii nie są animowane w oknie programu.
     */
    MessageReceivedPopUp(String author, String message, boolean isFromHistory) {
        super(author,message, isFromHistory);
        setBackground(MESSAGE_RECEIVED_COLOR);
    }
}
