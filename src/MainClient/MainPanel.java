package MainClient;
import javax.swing.*;
import java.awt.*;

/**
 * Created by Przemek on 27.11.2016.
 */

/**
 * Klasa "spinająca" elementy interfejsu.
 */
public class MainPanel extends JPanel {

    /**
     * Konstruktor klasy MainPanel.
     */
    MainPanel() {
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        setBackground(Color.white);
        setVisible(true);
    }

}
