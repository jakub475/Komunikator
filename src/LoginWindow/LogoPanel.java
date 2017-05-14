package LoginWindow;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Przemek on 03.11.2016.
 */

/**
 * Panel w którym znajduje się logo programu. Funkcja czysto estetyczna.
 */
public class LogoPanel extends JPanel{
    /**
     * Zmienna przechowująca obrazek - logo programu.
     */
    private BufferedImage latteLogo;

    /**
     * Konstruktor klasy LogoPanel.
     */
    public LogoPanel() {
        File logoFile = new File("resources/images/logoLogin.png");
        try {
            latteLogo = ImageIO.read(logoFile);
            System.out.println("Odczytano");
        } catch(IOException e) {
            System.err.println("Blad odczytu obrazka");
            e.printStackTrace();
        }
        Dimension dimension = new Dimension(latteLogo.getWidth()+50, latteLogo.getHeight());
        setPreferredSize(dimension);
    }

    /**
     * Funkcja rysująca logo programu.
     * @param g Zmienna grafiki.
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(latteLogo, 0, 0, this);
    }
}
