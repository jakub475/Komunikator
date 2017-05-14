package Connection;

import CustomClasses.MessagePair;
import com.sun.glass.ui.Cursor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Przemek on 20.01.2017.
 */

/**
 * Klasa zarządzającą lokalną bazą danych, w której umieszczona jest historia wiadomości.
 */
public class LocalDatabase {

    /**
     * Połączenie do lokalnej bazy danych.
     */
    private Connection localInstance;
    /**
     * Nazwa bazy danych.
     */
    private String dbname;

    /**
     * Konstruktor klasy LocalDatabase. Tworzy się w niej instancja bazy danych, o ile nie została już utworzona.
     */
    public LocalDatabase() {
        localInstance = createDatabase();
        closeConnection();
    }

    /**
     * Metoda tworząca lokalną bazę danych.
     * @return Połączenie do lokalnej historii.
     */
    private Connection createDatabase() {
        Connection connect;
        dbname = "localDB";
        try {
            Class.forName("org.sqlite.JDBC");
            connect = DriverManager.getConnection("jdbc:sqlite:"+dbname+".db");
        } catch (Exception ex) {
            System.err.println("Error while connecting to \n" + ex.getMessage());
            return null;
        }
        return connect;
    }

    /**
     * Daje obiektowi klasy połączenie do bazy danych.
     */
    private void establishConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            localInstance = DriverManager.getConnection("jdbc:sqlite:"+dbname+".db");
        } catch (Exception ex) {
            System.err.println("Error while connecting to \n" + ex.getMessage());
        }
    }

    /**
     * Zamyka połączenie do bazy.
     */
    private void closeConnection() {
        try {
            localInstance.close();
        } catch (SQLException ex) {
            System.err.println("Error during closing connection: "+ ex.getMessage());
        }
    }

    Connection getLocalDB() {
        return localInstance;
    }

    /**
     * Tworzy tabelę dla nowego kanału.
     * @param tableName Nazwa dla tablicy pochodząca od nazwy kanału.
     */
    void createNewTable(String tableName) {
        Statement stat;
        establishConnection();
        try {
            stat = localInstance.createStatement();
            String tableSQL = "CREATE TABLE IF NOT EXISTS " + tableName
                    + " (MSGFROM CHAR(50) NOT NULL,"
                    + "MESSAGE VARCHAR(500),"
                    + "SEND_DATE DATETIME)";
            stat.executeUpdate(tableSQL);
            stat.close();
        } catch (SQLException e) {
            System.out.println("Cannot create table"+e.getMessage());
        }
        closeConnection();
    }

    /**
     * Dodaje do tablicy konkretnego kanału nową wiadomość.
     * @param channel Nazwa kanału w którym pojawiła się nowa wiadomość.
     * @param author Nadawca wiadomości.
     * @param message Treść wiadomości.
     */
    void insertNewMessageToLocalHistory(String channel, String author, String message) {
        establishConnection();
        Statement stat;
        try {
            stat = localInstance.createStatement();
            String statement = "INSERT INTO "+channel+" (MSGFROM,MESSAGE) "
                    + "VALUES ('"+author+"','"+message+"');";
            stat.executeUpdate(statement);
        } catch (SQLException ex) {
            System.err.println("Error during saving message to local history: "+ex.getMessage());
        }
        closeConnection();
    }

    /**
     * Pobiera listę wszystkich kanałów z lokalnej bazy danych.
     * @return Lista nazw kanałów.
     */
    public ArrayList<String> getAllChannelsFromHistory() {
        ArrayList<String> listOfChannels = new ArrayList<>();
        establishConnection();
        try {
            DatabaseMetaData dbm = localInstance.getMetaData();
            ResultSet rs = dbm.getTables(null,null,"%",null);
            while(rs.next()) {
                listOfChannels.add(rs.getString("TABLE_NAME").toString());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        listOfChannels.remove("LASTACTIVE");
        Collections.reverse(listOfChannels);
        System.out.println(listOfChannels);
        return listOfChannels;
    }

    /**
     * Zapisuje ostatniego aktywnego użytkownika w bazie danych.
     * @param username Nazwa użytkownika.
     * @param password Hasło.
     */
    public void saveLastActiveUser(String username, String password) {
        establishConnection();
        Statement stat;
        try {
            stat = localInstance.createStatement();
            String tableSQL = "CREATE TABLE IF NOT EXISTS LASTACTIVE"
                    + " (USERNAME CHAR(50) NOT NULL,"
                    + "PASSWORD CHAR(50) NOT NULL)";
            stat.executeUpdate(tableSQL);
            stat.close();

            Statement clearLast;
            clearLast = localInstance.createStatement();
            String clearSQL = "DELETE FROM LASTACTIVE";
            clearLast.executeUpdate(clearSQL);
            clearLast.close();

            Statement userStat;
            userStat = localInstance.createStatement();
            String userSQL = "INSERT INTO LASTACTIVE (USERNAME,PASSWORD) "
                    + "VALUES ('"+username+"','"+password+"');";
            userStat.executeUpdate(userSQL);
            userStat.close();
        } catch (SQLException ex) {
            System.err.println("Cannot save last active user to database: "+ex.getMessage());
        }
        closeConnection();
    }

    /**
     * Pobiera nazwę ostatnio zalogowanego użytkownika.
     * @return Nazwa użytkownika.
     */
    public String getLastUsername() {
        establishConnection();
        Statement stat;
        String username = null;
        try {
            stat = localInstance.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM LASTACTIVE;");
            while(rs.next()) {
                username = rs.getString("USERNAME");
            }
            rs.close();
            stat.close();
        } catch (SQLException ex) {
            System.err.println("Error during reading last active username: "+ex.getMessage());
            return null;
        }
        closeConnection();
        return username;
    }

    /**
     * Pobiera wszystkie wiadomości z podanego kanału.
     * @param channelName Pobiera wiadomości z kanału.
     * @return Lista wiadomości w postaci obiektu klasy łaczącego autora oraz wiadomość.
     */
    public ArrayList<MessagePair> getMessagesFromChannel(String channelName) {
        establishConnection();
        Statement stat;
        ArrayList<MessagePair> messages = new ArrayList<>();
        try {
            stat = localInstance.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM "+channelName+";");
            while(rs.next()) {
                messages.add(new MessagePair(rs.getString("MSGFROM"),rs.getString("MESSAGE")));
            }
            rs.close();
            stat.close();
        }catch (SQLException ex) {
            System.err.println("Error during importin messages from local history: "+ex.getMessage());
            return null;
        }
        closeConnection();
        return messages;
    }

    /**
     * Jeśli loguje się nowy użytkownik, program czyści bazę danych oraz pobiera historie nowego użytkownika.
     */
    public void cleanHistory() {
        establishConnection();
        Statement stat;
        String SQLDelete;
        ArrayList<String> channelsToDelete = getAllChannelsFromHistory();
        try {
            stat = localInstance.createStatement();

            for(String channel : channelsToDelete) {
                SQLDelete = "DROP TABLE IF EXISTS "+channel;
                stat.executeUpdate(SQLDelete);
            }
            stat.close();
            System.out.println("Poprawnie usunięto");
        } catch (SQLException ex) {
            System.err.println("Error during getting history: "+ex.getMessage());
        }
        closeConnection();
    }

    /**
     * Metoda zwracająca hashcode ostatniego ostatnio używanego hasła.
     * @return
     */
    public String getLastPassword() {
        establishConnection();
        Statement stat;
        String username = null;
        try {
            stat = localInstance.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM LASTACTIVE;");
            while(rs.next()) {
                username = rs.getString("PASSWORD");
            }
            rs.close();
            stat.close();
        } catch (SQLException ex) {
            System.err.println("Error during reading last active password: "+ex.getMessage());
            return null;
        }
        closeConnection();
        return username;
    }
}
