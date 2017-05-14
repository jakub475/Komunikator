package MainClient;

import Connection.ConfigFile;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Przemek on 27.11.2016.
 */

/**
 * Główna klasa programu, wywoływana po zalogowaniu się.
 */
public class MainWindow {

    public MainWindow()
    {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                new ApplicationWindow();
            }
        });
    }
    public MainWindow(boolean connectionStatus)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                new ApplicationWindow(connectionStatus);
            }
        });
    }
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                new ApplicationWindow();
            }
        });
        ConfigFile.getConfigFile().LoadFile();
    }



}
