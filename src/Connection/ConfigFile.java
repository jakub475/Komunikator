/**
 * Created by Kuba on 01.12.2016.
 */
package Connection;
import java.awt.*;
import java.io.*;
import java.util.Properties;


/**
 * Klasa służąca do parsowania pliku konfiguracyjnego
 */
public class ConfigFile {

    /**
     * Pole z nazwą pliku
     */
    private String FileName = "config.properties";
    /**
     * Nazwa użytkownika
     */
    private String userName = null;
    /**
     * Hasło użytkownika
     */
    private String userPassword = null;
    /**
     * Domyślny adres serwera, może zostać zmodyfikowany poprzez użycie pliku konfiguracyjnego
     */
    private String serverAddress = "138.68.83.57";
    /**
     * Port na którym połączymy się z serwerem
     */
    private int port = 8000;
    /**
     * Uchwyt do pliku
     */
    private static ConfigFile confFile = null;
    /**
     * Zmianne informująca o isteniniu pliku konfiguracyjnego
     */
    private boolean fileExist = false;
    /**
     * Domyślny kolor tła, może zostać nadpisany przez zmiany w pliku konfiguracyjnym
     */
    private Color backgroundColor = new Color(229,243,249);

    static public void main(String[] args) {
        ConfigFile conf = getConfigFile();
        conf.setUserName("testuser3");
        conf.setUserPassword("testuser");
        conf.setServerAddress("138.68.83.57");
        conf.SaveFile();
        conf.LoadFile();
    }

    /**
     * Konstruktor klasy
     */
    private ConfigFile() {}

    /**
     * Funkcja zwracająca instancję klasy configFile
     * @return instancja klasy
     */
    public static ConfigFile getConfigFile()
    {
        if(confFile == null)
        {
            confFile = new ConfigFile();
            return confFile;
        }
        else
        {
            return confFile;
        }
    }


    /**
     * Funkcja zapisująca plik na dysku
     * @return Wartość  logiczna informująca o tym czy zapis się powiódł
     */
    public boolean SaveFile()  //
    {
        if (userName == null || userPassword == null || serverAddress == null)
            return false;
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream(FileName);

            // set the properties value
            prop.setProperty("userName", userName);
            prop.setProperty("userPassword", userPassword);
            prop.setProperty("serverAddress", serverAddress);
            prop.setProperty("backgroundColorR",Integer.toString(backgroundColor.getRed()));
            prop.setProperty("backgroundColorG",Integer.toString(backgroundColor.getGreen()));
            prop.setProperty("backgroundColorB",Integer.toString(backgroundColor.getBlue()));

            // save properties to project root folder
            prop.storeToXML(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return true;
    }

    /**
     * Funkcja służąca do załadowania pliku konfiguracyjnego
     */
    public void LoadFile() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.loadFromXML(input);

            // get the property value and print it out
            userName = prop.getProperty("userName");
            userPassword = prop.getProperty("userPassword");
            serverAddress = prop.getProperty("serverAddress");
            int red =  Integer.parseInt(prop.getProperty("backgroundColorR"));
            int green =  Integer.parseInt(prop.getProperty("backgroundColorG"));
            int blue =  Integer.parseInt(prop.getProperty("backgroundColorB"));
            backgroundColor = new Color(red,green,blue);
            fileExist = true;

        }
        catch (FileNotFoundException fnf) {
            fileExist = false;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
         finally
         {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * Funkcja służąca do ustawiania nazwy użytkownika
     * @param userName nazwa użytkownika
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * Ustawianie hasła
     * @param userPassword Hasło użytkownika
     */
    public void setUserPassword(String userPassword)
    {
        this.userPassword = userPassword;
    }

    /**
     * Ustawianie adresu serwera
     * @param serverAddress Adres serwera
     */
    public void setServerAddress(String serverAddress)
    {
        this.serverAddress = serverAddress;
    }

    /**
     * Zwracanie hasła użytkownika
     * @return Hasło użytkownika
     */
    public String getUserPassword()
    {
        return userPassword;
    }

    /**
     * Zwracanie nazwy użytkownika
     * @return nazwa użytkownika
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Zwracanie adresu serwera
     * @return Adres serwera
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Zwracanie kooru tła
     * @return Kolor tła
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

}

