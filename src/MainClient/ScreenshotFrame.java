package MainClient;


import ToSend.ScreenshotInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Przemek on 14.01.2017.
 */

/**
 * Okno wykorzystywane do wysyłania zrzutu ekranu.
 */
public class ScreenshotFrame extends JFrame {
    /**
     * Lista punktów zaznaczenia aktualnie pokazywanych na zrzucie ekranu.
     */
    private ArrayList<Point> points;
    /**
     * Kopia zapasowa punktów, przydatna podczas ich ukrywania.
     */
    private ArrayList<Point> pointsBackup;
    /**
     * Panel, w którym znajdują się wszystkie elementy okna.
     */
    private JPanel panel;
    /**
     * Przycisk służący do włączania/wyłączania punktów.
     */
    private JButton toggleButton;
    /**
     * Label wyświetlający przechwycony obraz.
     */
    private PaintedScreenshotLabel label;

    /**
     * Zmienna określająca czy punkty są wyświetlane, czy nie.
     */
    private boolean pointsToggledOn;

    /**
     * Konstruktor klasy ScreenshotFrame
     * @param screenshotinfo Pakiet informacji o przesłanym zrzucie ekranu.
     */
    public ScreenshotFrame(ScreenshotInfo screenshotinfo) {
        super();
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
        points = screenshotinfo.getPointsArray();
        pointsBackup = clonePointsList(points);
        this.add(panel);
        initToggleButton();
        label = new PaintedScreenshotLabel(screenshotinfo.getScreenshot());
        panel.add(label);
        pointsToggledOn = true;
        setVisible(true);
        pack();
    }

    /**
     * Metoda inicjalizująca przycisk do ukrywania/pokazywania zaznaczenia na zrzucie.
     */
    private void initToggleButton() {
        toggleButton = new JButton("Usuń zaznaczenie");
        this.panel.add(toggleButton);
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pointsToggledOn) {
                    points.clear();
                    System.out.println("Points toggled off");
                    toggleButton.setText("Pokaż zaznaczenie");
                    pointsToggledOn = false;
                }
                else {
                    points = clonePointsList(pointsBackup);
                    System.out.println("Points toggled on");
                    toggleButton.setText("Usuń zaznaczenie");
                    pointsToggledOn = true;
                }
                repaint();
            }
        });
    }

    /**
     * Klasa zagnieżdżona, która wyświetla zrzut ekranu.
     */
    class PaintedScreenshotLabel extends JLabel {

        private int lx = 0;
        private int ly = 0;

        /**
         * Konstruktor klasy PaintedScreenshotLabel
         * @param img Zmienna przechowująca zrzut ekranu.
         */
        public PaintedScreenshotLabel(ImageIcon img) {
            super(img);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            paintPoints(g2d);
        }

        /**
         * Metoda rysująca punkty na ekranie.
         * @param g2d zmienna graficzna
         */
        private void paintPoints(Graphics2D g2d) {
            g2d.setColor(Color.black);
            g2d.setStroke(new BasicStroke(10));
            for(Point pt: points) {
                g2d.fillOval((int) pt.getX() - 2, (int) pt.getY() + getInsets().top - 5, 10, 10);
                if(lx !=  0  && calculateDistance((int) pt.getX() - 2, (int) pt.getY() + getInsets().top - 5,lx,ly) < 50)
                    g2d.drawLine((int) pt.getX() - 2, (int) pt.getY() + getInsets().top,lx,ly);
                lx = (int)pt.getX();
                ly = (int)pt.getY();

            }
        }

        /**
         * Zmiena kalkulująca czy rysowane punkty połączyć linią czy pozostawić je jako oddzielne krzywe.
         * @param x1 Współrzędna x pierwszego punktu.
         * @param y1 Współrzędna y pierwszego punktu.
         * @param x2 Współrzędna x drugiego punktu.
         * @param y2 Współrzędna y drugiego punktu.
         * @return
         */
        public int calculateDistance(int x1, int y1, int x2, int y2) {
            return (int)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
        }

    }

    /**
     * Tworzy kopie listy wszystkich punktów.
     * @param arrayToClone Pierwotna lista punktów.
     * @return Kopia listy punktów.
     */
    public ArrayList<Point> clonePointsList(ArrayList<Point> arrayToClone) {
        ArrayList<Point> cloned = new ArrayList<>();
        for(Point pt: arrayToClone) {
            cloned.add(pt);
        }
        return cloned;
    }
}
