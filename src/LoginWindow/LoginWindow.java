package LoginWindow;
import Connection.ConfigFile;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Przemek on 03.11.2016.
 */

/**
 * Klasa tworzona podczas uruchamiania programu. Tworzy obiekt klasy LoginFrame i umożliwia działanie programu.
 */
public class LoginWindow {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                LoginFrame logFrame = new LoginFrame();
            }
        });
        ConfigFile.getConfigFile().LoadFile();
    }
}
