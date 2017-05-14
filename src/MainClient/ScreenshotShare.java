package MainClient;

import Connection.Client;
import ToSend.ScreenshotInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Przemek on 13.01.2017.
 */

/**
 * Okno służące do przesyłania zrzutu ekranu. Lącznie z zaznaczaniem na nim pewnych punktów.
 */
public class ScreenshotShare extends JFrame {
    /**
     * Zmienna przechowująca obraaz zrzutu ekranu.
     */
    private BufferedImage screenshot;
    /**
     * Punkty zaznaczone na zrzucie.
     */
    private ArrayList<Point> points;
    /**
     * Panel w którym wyświetlane są wszystkie elementy okna.
     */
    private JPanel panel;
    /**
     * Przycisk służący do wysyłania gotowego zrzutu ekranu.
     */
    private JButton sendButton;
    /**
     * Przycisk służący do usunięcia zaznaczenia na zrzucie.
     */
    private JButton clearButton;
    /**
     * Label, w którym wyświetlany jest zrzut ekranu.
     */
    private ScreenshotLabel label;
    /**
     * Nazwa aktywnego kanału.
     */
    private String activeChannleName;

    /**
     * Referencja na panel, w którym znajdują się wszystkie wiadomości.
     */
    private MessagePanel messages;

    /**
     * Konstruktor klasy ScreenshotShare
     * @param caller Wywołujący ten konstruktor, co w praktyce jest panelem wiadomości. Potrzebne, aby odwołać się do głównego okna programu.
     * @param messages Panel, w którym znajdują się wiadomości.
     * @param activeChannelName Nazwa kanału, w którym zostało wywołane przesyłanie obrazu.
     */
    public ScreenshotShare(JPanel caller, MessagePanel messages, String activeChannelName) {
        super();
        this.activeChannleName = activeChannelName;
        this.messages = messages;
        getScreenshot(caller);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
        points = new ArrayList<>();
        this.add(panel);
        initSendButton();
        initClearButton();
        label = new ScreenshotLabel(new ImageIcon(screenshot));
        panel.add(label);
        setVisible(true);
        pack();
    }

    /**
     * Metoda inicjalizująca przycisk czyszczący zaznaczenie.
     */
    private void initClearButton() {
        clearButton = new JButton("Usuń zaznaczenie");
        this.panel.add(clearButton);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                points.clear();
                repaint();
            }
        });
    }

    /**
     * Metoda inicjalizująca przycisk potwierdzający wysłanie zrzutu.
     */
    private void initSendButton() {
        sendButton = new JButton("Wyślij zrzut");
        this.panel.add(sendButton);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messages.addNewMyMessageNow(messages.getActiveName(),sendPackedScreenshot(screenshot,points));
                messages.rePaint();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Client.getClient().sendPhotoToChannel(activeChannleName ,sendPackedScreenshot(screenshot,points));
                    }
                }).start();
                dispose();
            }

        });

    }

    /**
     * Metoda przechwytująca zrzut ekranu. Na ułamek sekundy ukrywa wszystkie okna, aby pobrać zrzut, zapisuje zrzut do zmiennej screenshot i ponownie pokazuje okna.
     * @param caller
     */
    private void getScreenshot(JPanel caller) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(caller);
        topFrame.setState(Frame.ICONIFIED);
        Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            screenshot = new Robot().createScreenCapture(screenSize);
        } catch(AWTException ex) {
            ex.printStackTrace();
        }
        topFrame.setState(Frame.NORMAL);
    }

    /**
     * Metoda dodająca zaznaczony punkt do listy punktów, oraz wyrysowująca ten punkt na ekranie.
     * @param pt
     */
    private void addPoint(Point pt) {
        points.add(pt);
        repaint();
    }


    /**
     * Metoda przesyłająca pakiet informacji o zrzucie ekranu.
     * @param img Zmienna przechowująca obraz zrzutu ekranu.
     * @param pts Kontener zawierający wszystkie punkty zaznaczenia.
     * @return Nowy obiekt, zawierający pakiet informacji o zrzucie.
     */
    public ScreenshotInfo sendPackedScreenshot(BufferedImage img, ArrayList<Point> pts) {
        return new ScreenshotInfo(img,pts);
    }

    /**
     * Klasa zagnieżdżona, która wyświetla zrzut ekranu.
     */
    public class ScreenshotLabel extends JLabel implements MouseListener, MouseMotionListener {

        /**
         * Ostatni punkt x.
         */
        private int lx = 0;
        /**
         * Ostatni punkt y.
         */
        private int ly = 0;

        /**
         * Konstruktor klasy ScreenshotLabel
         * @param img Zmienna przechowująca zrzut ekranu.
         */
        public ScreenshotLabel(ImageIcon img) {
            super(img);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        /**
         * Dodaje punkty do listy przy klinięciu.
         * @param e mouseEvent
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println(new Point(e.getX(),e.getY()));
            addPoint(new Point(e.getX(),e.getY()));
            lx = e.getX();
            ly = e.getY();
        }

        /**
         * Dodaje punkty do listy przy przytrzymaniu przycisku.
         * @param e mouseEvent
         */
        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println(new Point(e.getX(),e.getY()));
            addPoint(new Point(e.getX(),e.getY()));
        }

        /**
         * Resetuje ostatnie punkty przy puszczeniu przycisku myszy.
         * @param e mouseEvent
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            lx = 0;
            ly = 0;
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        /**
         * Dodaje punkty do listy przy przeciąganiu myszki z wciśniętym kursorem.
         * @param e
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            System.out.println(new Point(e.getX(),e.getY()));
            addPoint(new Point(e.getX(),e.getY()));
        }

        /**
         * Resetuje ostatnie punkty przy poruszeniu kursora bez wciśniętego przycisku myszy.
         * @param e mouseEvent
         */
        @Override
        public void mouseMoved(MouseEvent e) {
            lx = 0;
            ly = 0;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            paintPoints(g2d);

        }

        /**
         * Metoda wyrysowująca punkty
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

}
