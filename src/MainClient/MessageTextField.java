package MainClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by Przemek on 02.12.2016.
 */

/**
 * Klasa pola tekstowego służącego do wpisywania treści wiadomości.
 */
public class MessageTextField extends JTextField implements FocusListener {
    /**
     * Hint "napisz wiadomość" gdy nie jest wpisana treść, lub pusty gdy panel jest aktywny.
     */
    private final String hint;
    /**
     * Boolean określający czy hint powinien być pokazywany czy nie.
     */
    private boolean isHintShowing;

    /**
     * Konstruktor klasy MessageTextField.
     * @param hint Hint który będzie wyświetlany, gdy pole tekstowe nie jest aktywne.
     * @param columns Szerokość pola tekstowego.
     */
    MessageTextField(final String hint, int columns) {
        super(hint, columns);
        this.hint = hint;
        this.isHintShowing = true;
        super.addFocusListener(this);
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(149, 149, 149)));
    }

    @Override
    /**
     * Implementacja metody listenera.
     */
    public void focusGained(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText("");
            isHintShowing = false;
        }
    }

    @Override
    /**
     * Implementacja metody listenera.
     */
    public void focusLost(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText(hint);
            isHintShowing = true;
        }
    }

    @Override
    /**
     * Pobiera treść wiadomości.
     */
    public String getText() {
        return isHintShowing ? "" : super.getText();
    }

    /**
     * Zmienia szerokość pola tekstowego podczas reskalowania okna programu.
     */
    public void adjustSize() {
        this.invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if(getParent() != null) {
            this.setColumns(calculateColumns());
        }
    }

    /**
     * Metoda przeliczająca szerokość okna, na liczbę kolumt pola tekstowego.
     * @return Kolumny - szerokość pola tekstowego.
     */
    private int calculateColumns() {
        Double d = (getParent().getWidth()*0.06-2.16);
        Integer i = d.intValue();
        if (i <= 0)
            return 1;
        else
            return i;
    }
}
