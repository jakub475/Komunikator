package MainClient;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Klasa reprezentująca dymek wiadomości.
 */
public abstract class MessagePopUp extends JPanel implements ComponentListener{
    /**
     * Autor wiadomości.
     */
    protected final String author;
    /**
     * Treść wiadomości.
     */
    protected String message;
    /**
     * JTextPane wyświetlający autora wiadomości.
     */
    protected Author authorTextArea;
    /**
     * JTextPane wyświetlający treść wiadomości.
     */
    protected Message messageTextArea;
    /**
     * Zmienna, która zawiera informacje o wielkości dymka. Edytowana przez różne metody i stale wykorzystywania przy aktualizowaniu interfejsu graficznego.
     */
    protected Dimension size;

    /**
     * Zmienna wykorzystywana do animacji pokazywania wiadomości. Zmienia wartość na true po zakończeniu animacji.
     */
    private boolean isShowed;

    /**
     * Referancja na panel w którym znajdują się wszystkie wiadomości.
     */
    MessagePanel parent;

    /**
     * Konstruktor klasy MessagePopUp.
     * @param author Autor wiadomości.
     * @param message Treść Wiadomości.
     * @param isFromHistory Zmienna określająca czy wiadomość jest nowa, czy pobierana z historii. Wiadomości z historii nie są animowane w oknie programu.
     */
    MessagePopUp(String author, String message, boolean isFromHistory) {
        this.author = author;
        this.message = message;
        this.isShowed = isFromHistory;
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        authorTextArea = new Author();
        messageTextArea = new Message();
        add(authorTextArea,BorderLayout.PAGE_START);
        add(messageTextArea,BorderLayout.CENTER);
        setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(),
                new EmptyBorder(new Insets(10,20,10,20))
        ));
        size = this.getSize();
        setVisible(true);
        addComponentListener(this);
    }

    /**
     * Klasa zagnieżdżona klasy MessagePopUp, wyświetela autora wiadomości.
     */
    class Author extends JTextPane {
        /**
         * Konstruktor klasy Author.
         */
        Author() {
            setText(author + " mówi:");
            setEditable(false);
            setFont(ApplicationWindow.UIFont);
            Font areaFont = this.getFont();
            float size = areaFont.getSize() + 13.0f;
            setFont(areaFont.deriveFont(size));
        }
    }
    /**
     * Klasa zagnieżdżona klasy MessagePopUp, wyświetela autora wiadomości.
     */
    class Message extends JTextPane {
        /**
         * Konstruktor klasy Message.
         */
        Message() {
            setText(message);
            setEditable(false);
            setFont(ApplicationWindow.UIFont);
            Font areaFont = this.getFont();
            float size = areaFont.getSize() + 13.0f;
            setFont(areaFont.deriveFont(size));
        }
    }

    /**
     * Metoda do zmiany wielkości dymków wiadomości przy reskalowaniu okna. Wywoływana przez panel, w którym znajdują się wszystkie wiadomości.
     */
    void adjustSize() {
        this.invalidate();
    }

    /**
     * Aktualizowanie interfejsu
     */
    @Override
    public void invalidate() {
        super.invalidate();
        if(isShowed) {
            size.width = getParent().getWidth();
            size.height = messageTextArea.getPreferredSize().height + authorTextArea.getPreferredSize().height + 20;
        }
        this.setPreferredSize(size);
        authorTextArea.setBackground(getBackground());
        messageTextArea.setBackground(getBackground());
    }

    /**
     * Meteoda wywołująca animację nowej wiadomości.
     */
    synchronized protected void animatePopUp() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Dimension tempDimension = new Dimension(parent.getWidth(),5);
                while(messageTextArea.getPreferredSize().height + authorTextArea.getPreferredSize().height + 20 > tempDimension.getHeight()) {
                    try {
                        size = tempDimension;
                        tempDimension.height += 1;
                        Thread.sleep(4);
                        invalidate();
                        updateUI();
                        parent.goToBottom();
                    } catch(InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                isShowed = true;
                invalidate();
            }
        });
        t.start();
    }

    @SuppressWarnings("serial")
    /**
    * Klasa definiująca nowe obramowanie, które daje wrażenie, że wiadomość znajduje się w dymku.
    */
    class RoundBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            super.paintBorder(c, g, x, y, width, height);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(12));
            g2d.setColor(Color.white);
            g2d.drawRoundRect(x, y, width - 1, height - 1, 25, 25);
            g2d.drawRoundRect(x,y,width+2,height+2,25,25);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
    }

    /**
     * Kolor wykorzystywany do pokazywania wiadomości wysłanych przez użytkownika.
     */
    static Color MESSAGE_SENT_COLOR = new Color(0, 147, 239);
    /**
     * Kolor wykorzystwany do pokazywania wiadomości odebranych od innych użytkowników.
     */
    static Color MESSAGE_RECEIVED_COLOR = new Color(118, 217, 254);

    /**
     * Metoda nie wykorzysywana
     * @param e
     */
    @Override
    public void componentResized(ComponentEvent e) {
    }

    /**
     * Wywoływana przy każdym ruchu elementu - praktycznie przy pojawieniu się wiadomości.
     * @param e Zmienna zdarzenia.
     */
    @Override
    public void componentMoved(ComponentEvent e) {
        parent = (MessagePanel) SwingUtilities.getAncestorOfClass(MessagePanel.class, this);
        if(!isShowed)
            animatePopUp();
    }

    /**
     * Metoda nie wykorzysywana
     * @param e
     */
    @Override
    public void componentShown(ComponentEvent e) {
    }

    /**
     * Metoda nie wykorzysywana
     * @param e
     */
    @Override
    public void componentHidden(ComponentEvent e) {
    }
}