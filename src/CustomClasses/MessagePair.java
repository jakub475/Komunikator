package CustomClasses;

/**
 * Created by Przemek on 29.01.2017.
 */

/**
 * Klasa umożliwiająca szybkie przesyłanie informacji o wiadomości bez konieczności skomplikowanego dostosowania słownika.
 */
public class MessagePair {
    private String key;
    private String value;

    public MessagePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

}
