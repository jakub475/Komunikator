package MainClient;

import ToSend.ScreenshotInfo;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Przemek on 13.01.2017.
 */

/**
 * Klasa dziedicząca po MessagePopUp. Określa wygląd dymka wiadomości, w której zamiast tekstu przesłaliśmy zrzut ekranu.
 */
public class ScreenshotMessagePopUp extends MessagePopUp implements MouseListener{
    /**
     * Pakiet informacji o zrzucie ekranu.
     */
    private ScreenshotInfo screen;

    /**
     * Kostruktor klasy ScreenshotMessagePopUp
     * @param author Autor zrzutu ekranu.
     * @param screenshot Przesyłany zrzut ekranu.
     */
    public ScreenshotMessagePopUp(String author, ScreenshotInfo screenshot) {
        super(author, "Wysłałem zrzut ekranu. Aby go zobaczyć kliknij w tę wiadomość.",false);
        this.screen = screenshot;

        MouseListener listener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showScreenshot();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        };
        this.authorTextArea.addMouseListener(listener);
        this.messageTextArea.addMouseListener(listener);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        showScreenshot();
    }

    /**
     * Metoda otwierająca okno pokazujące zrzut ekranu.
     */
    private void showScreenshot() {
        new ScreenshotFrame(screen);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Zmiana kursora przy najeżdżaniu na obiekt.
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Zmiana kursora przy przesunięciu kursora poza obiekt.
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(Cursor.getDefaultCursor());
    }
}
