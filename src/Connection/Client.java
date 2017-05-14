package Connection;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

import Listeners.ChannelListener;
import Listeners.MessageListener;
import Listeners.UserLoggedListener;
import LoginWindow.LoginFrame;
import MainClient.MainWindow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ToSend.ScreenshotInfo;

import javax.swing.*;

/**
 * @author Kuba
 */
public class Client implements Runnable {

    /**
     * Numer portu połączenia z serwerem
     */
    private int PORT = 8000;
    //    private String HOST = "138.68.83.57";
    /**
     * Numer ip serwera
     */
    private String HOST = "138.68.83.57";
    /**
     * Nazwa użytkownika
     */
    private String userName = null;
    /**
     * Hasło użytkownika
     */
    private String userPassword = null;
    /**
     * Strumień wyjściowy
     */
    private PrintWriter out;
    /**
     * Bufor wejściowy
     */
    private BufferedReader in;
    /**
     * Instancja klasy Client
     */
    private static Client client = null;
    /**
     * Ramka logowania
     */
    private LoginFrame loginFrame = null;
    /**
     * Wektor słuchaczy logowania użytkowników
     */
    private Vector<UserLoggedListener> userLoggedListeners = new Vector<>();
    /**
     * Wektor słuchaczy nowych wiadomości
     */
    private Vector<MessageListener> messageListeners = new Vector<>();
    /**
     * Wektor słuchaczy tworzenia nowych kanałów
     */
    private Vector<ChannelListener> channelListeners = new Vector<>();
    /**
     * Lista nazw kanałów
     */
    private ArrayList<String> channelNames = new ArrayList<>();
    /**
     * Flaga sterująca działaniem pętli głównej klienta
     */
    private volatile boolean isWorking = true;
    /**
     * Obiekt Socket służący do otwarcia połączenia z serwerem
     */
    private Socket echoSocket;

    boolean wasPreviouslyLogged;
    /**
     * Obiekt lokalnej historii.
     */
    private LocalDatabase localHistory;

    public static Client getClient() {
        if (client == null) {
            client = new Client();
            return client;
        } else
            return client;
    }

    /**
     * @param userName     - nazwa użytkownika
     * @param userPassword - hasło użytkownika
     * @param host         - nazwa hosta z którym się łączymy
     */
//    private Client(String userName, String userPassword, String host, int port) {
//        this.userName = userName;
//        this.userPassword = userPassword;
//        this.HOST = host;
//        this.PORT = port;
//    }

    /**
     * Konstruktor klasy Client
     */
    private Client() {
        HOST = ConfigFile.getConfigFile().getServerAddress();
    }

    /**
     * Funkcja realizująca połączenie z serwerem
     *
     * @throws IOException
     */
    public void startClient() throws IOException {
        echoSocket = new Socket(HOST, PORT);
        out = new PrintWriter(echoSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
    }

    /**
     * Zamyka strumienie wejściowe i wyjściowe klienta
     * @throws IOException
     */

    private void stopClient() throws IOException {
        out.close();
        in.close();
    }

    /**
     * Logowanie na serwerze na serwerze.
     */
    private void login() {
        JSONObject json;
        json = new JSONObject();
        json.accumulate("name", userName);
        json.accumulate("password", userPassword);
        out.println(json.toString());
    }

    /**
     * Główna pętla klienta
     */
    @Override
    public void run() {
        localHistory = new LocalDatabase();
        JSONObject message;
        login();

        try {
            String serverInput;
            if ((serverInput = in.readLine()) != null) {
                System.out.println("Echo: " + serverInput);
                message = new JSONObject(serverInput);
                if (message.has("loginUser")) { // jeśli potwierdzono zalogowanie to zamknij okno logowania i odpal okno główne
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            loginFrame.dispose();
                        }
                    });
                    if(!(wasPreviouslyLogged = userName.equals(localHistory.getLastUsername())))
                        localHistory.cleanHistory();
                    localHistory.saveLastActiveUser(userName, userPassword);
                    ConfigFile.getConfigFile().setUserName(userName);
                    ConfigFile.getConfigFile().setUserPassword(userPassword);
                    ConfigFile.getConfigFile().SaveFile();
                } else { // w przypadku niepoprawnych danych wyświetlane jest okno z powiadomieniem
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(null, "Niepoprawna nazwa użytkownika i/lub hasło");
                        }
                    });
                    throw new WrongNameExceptions("Niepoprawne dane logowania");
                }
            }

            new MainWindow();
            try{
            Thread.sleep(500);
            }catch (InterruptedException ie){
                System.out.println("To opóźnienie w kliencie było bez sensu");
            }

            getChannelNames();
            getLoggedUsers();

            while (isWorking && (serverInput = in.readLine()) != null) {
                System.out.println("Echo: " + serverInput);
                message = new JSONObject(serverInput);
                if (message.has("Server")) {
                    if (message.getString("Server").equals("loggedIn")) {
                        userLoggedIn(message.getString("loginUser"));
                    } else if (message.getString("Server").equals("newUser")) {
                        newUserRegister(message.getString("UserName"));
                    } else if (message.getString("Server").equals("loggedUsers")) {
                        message.remove("Server");

                        for (String userName : message.keySet()) {
                            userLoggedIn(message.getString(userName));
                        }
                    } else if (message.getString("Server").equals("loggedOut")) {
                        removeUser(message.getString("loginUser"));
                    } else if (message.getString("Server").equals("channelName")) {
                        if ((message.has("userNames"))) {
                            String channel = message.getString("channelName");
                            ArrayList<String> userNameArray = new ArrayList<>();
                            try {
                                JSONArray jsonArray = message.getJSONArray("userNames");

                                for (Object one : jsonArray) {
                                    userNameArray.add((String) one);
                                }

                            } catch (JSONException jse) {
                                System.out.println("Nie ma użytkowników w kanale");
                            }
                            addNewChannel(channel, userNameArray);
                            channelNames.add(channel);
                            getChannelHistory(channel);
                        }
                    } else if (message.getString("Server").equals("History")) {
                        if (message.has("channelName")) {
                            newMessageShow(message.getString("from"), message.getString("channelName"), message.getString("message"), true);
                        }
                    } else if (message.getString("Server").equals("sendPhoto")) {
                        try {
                            echoSocket = new Socket(HOST, 5000);
                            ObjectInputStream inObject = new ObjectInputStream(echoSocket.getInputStream());
                            ScreenshotInfo readObject = (ScreenshotInfo) inObject.readObject();
                            inObject.close();
                            newMessageShow(message.getString("from"), message.getString("channelName"), readObject, false);
                        } catch (ClassNotFoundException cle) {
                            System.out.println("Odebrano nieznany obiekt");
                        }
                    }
                } else {
                    if (message.has("from") && message.has("to")) {
                        if (message.getString("from").equals(userName)) {
                            System.out.println("Echo");
                        } else {
                            System.out.println("Echo Przycisk: " + serverInput);
                            newMessageShow(message.getString("from"), message.getString("message"));
                        }
                    } else if (message.has("from") && message.has("channelName")) {
                        System.out.println("Echo Przycisk: " + serverInput);
                        newMessageShow(message.getString("from"), message.getString("channelName"), message.getString("message"), false);
                    }

                }

            }
        } catch (UnknownHostException e) {
            System.err.println("Nieznany adres:  " + HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Nie otworzono I/O " + HOST);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "Połączenie zostało zerwane");
                }
            });
        } catch (JSONException JSONe) {
            System.out.println("Nieprawidłowy JSON");

        }
        catch (WrongNameExceptions wde){
            System.out.println(wde.getMessage());
        }
        finally {
            try {
                stopClient();
            } catch (IOException e) {
                System.err.println("Nie zamknięto I/O");
            }
            System.out.println("Thread close");

        }

    }

    /**
     * Funkcja pozwala na wysyłanie wiadomości
     *
     * @param to      nazwa użytkownika do którego ma dotrzeć wiadomość
     * @param message treść wiadomości
     */
    public void send(String to, String message, boolean isChannel) {
        JSONObject json = new JSONObject();
        json.accumulate("from", userName);
        json.accumulate("message", message);
        if (isChannel) {
            json.accumulate("channelName", to);
        } else {
            json.accumulate("to", to);
        }
        out.println(json.toString());
        localHistory.insertNewMessageToLocalHistory(to, getUserName(), message);
    }

    /**
     * Wysyła zapytanie do serwera z prośbą o podanie nazwy aktualnie stworzonych na serwerze kanałów.
     */
    public void getChannelNames() {
        JSONObject json = new JSONObject();
        json.accumulate("Server", "getChannelNames");
        out.println(json.toString());
    }

    /**
     * Wysyła zapytanie do serwera z prośbą o podanie listy aktualnie zalogowanych użytkowników, stosowana przy logowaniu
     */
    public void getLoggedUsers() {
        JSONObject json = new JSONObject();
        json.accumulate("Server", "getLoggedUsers");
        out.println(json.toString());
    }

    /**
     * Wysyła do serwera zapytanie z prośbą o podanie historii kanału
     *
     * @param channelName nazwa kanału
     */
    public void getChannelHistory(String channelName) {
        JSONObject json = new JSONObject();
        json.accumulate("Server", "History");
        json.accumulate("channelName", channelName);
        out.println(json.toString());
    }

    /**
     * Pobierz historię wszystkich kanałów
     */
    public void getAllHistory() {
        for (String oneName : channelNames) {
            getChannelHistory(oneName);
        }
    }

    /**
     * Pobierz historię użytkownika
     * @param from nazwa nadawcy wiadomości
     * @param to nazwa odbiorcy
     */
    public void getUserHistory(String from, String to) {
        JSONObject json = new JSONObject();
        json.accumulate("Server", "History");
        json.accumulate("from", from);
        json.accumulate("to", to);
        out.println(json.toString());
    }

    /**
     * Funkcja zwraca nazwę użytkownika
     *
     * @return podana nazwa użytkownika
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Służy do podania nazwy użytkownika podawanej przy logowani do serwera
     *
     * @param userName nazwa użytkownika
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Służy do podania hasła użytkownika podawanej przy logowani do serwera
     *
     * @param userPassword hasło lub hash hasła
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Ustawia ramkę logowania
     *
     * @param loginFrame obiekt ramki
     */
    public void setLoginFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
    }

    /**
     * Dodaj słuchacza logowania nowych użytkowników
     *
     * @param user obiekt słuchacza
     */
    public void addUserListener(UserLoggedListener user) {
        userLoggedListeners.add(user);
    }

    /**
     * Funkcja dodaje słuchacza nowych wiadomości
     *
     * @param newListener
     */
    public void addMessageListener(MessageListener newListener) {
        messageListeners.add(newListener);
    }

    /**
     * Funckaj dodaje słuchaczy utworzenia nowego kanału
     *
     * @param channelListener obiekt słuchacza
     */
    public void addChannelListeners(ChannelListener channelListener) {
        this.channelListeners.add(channelListener);
    }

    /**
     * Funkcja powiadamiająca serwer o potrzebiue stworzenia nowego kanału
     *
     * @param channelName nawa kanału
     * @param userNames   lista użytkowników w kanale
     */
    public void createChannel(String channelName, ArrayList<String> userNames) {
        JSONObject message = new JSONObject();
        message.put("Server", "createNewChannel");
        message.put("channelName", channelName);
        for (String name : userNames) {
            message.accumulate("Users", name);
        }
        out.println(message.toString());
    }

    /**
     * Funkcja informująca wszystkie obiekty na liście userLoggedListeners o zalogowaniu się nowego użytkownika
     *
     * @param newUserName nazwa użytkownika
     */
    private void userLoggedIn(String newUserName) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (UserLoggedListener listener : userLoggedListeners) {
                    listener.userLoggedIn(newUserName);
                    listener.rePaint();
                }
            }
        });
    }

    /**
     * Funkcja informująca wszystkie obiekty na liście userLoggedListeners o stworzeniu nowego użytkownika
     *
     * @param userName nazwa użytkownika
     */
    private void newUserRegister(String userName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (UserLoggedListener listener : userLoggedListeners) {
                    listener.newUserRegister(userName);
                    listener.userLoggedIn(userName);
                    listener.rePaint();
                }
            }
        });
    }

    /**
     * Funkcja informująca wszystkie obiekty  z listy userLoggedListener o tym że użytkownik o podanej nazwie się wylogował
     *
     * @param newUserName nazwa użytkownika
     */
    private void removeUser(String newUserName) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (UserLoggedListener listener : userLoggedListeners) {
                    listener.userLoggedOut(newUserName);
                    listener.rePaint();
                }
            }
        });
    }

    /**
     * Funkcja wyświetlająca wiadomość przy bezpośrednim kontakcie między użytkownikami z pominięciem kanałów.
     *
     * @param from       nazwa użytkownika który nadał wiadomość
     * @param newMessage treść wiadomości
     */
    private void newMessageShow(String from, String newMessage) {
        for (MessageListener oneListener : messageListeners) {
            oneListener.addNewMessage(from, newMessage, false);
        }
    }

    /**
     * Funkcja informująca wszystkie obiekty na liście messageListener o przyjściu nowej wiadomości
     *
     * @param from        - nazwa użytkownika od którego przyszła wiadomość
     * @param channelName nazwa kanału na który wiadomość została wysłana
     * @param newMessage  treść wiadomości
     * @param history     pole wyboru czy wiadomość została już odczytana
     */
    private void newMessageShow(String from, String channelName, String newMessage, boolean history) {
        for (MessageListener oneListener : messageListeners) {
            oneListener.addNewMessage(from, channelName, newMessage, history);
        }
        if (!history) {
            localHistory.insertNewMessageToLocalHistory(channelName, from, newMessage);
        } else if(!wasPreviouslyLogged) {
            localHistory.insertNewMessageToLocalHistory(channelName,from,newMessage);
        }

    }

    /**
     * Służy do wywołania na słuchaczach metody dodawania nowych zrzutów ekranów
     * @param from nadawca wiadomości
     * @param channelName nazwa kanału
     * @param screenshotInfo obiekt przechowujący zrzut
     * @param history pole czy obiekt jest historyczny
     */
    private void newMessageShow(String from, String channelName, ScreenshotInfo screenshotInfo, boolean history) {
        for (MessageListener oneListener : messageListeners) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    oneListener.addNewMessage(from, channelName, screenshotInfo, history);
                }
            });

        }
    }


    //channel listenr

    /**
     * Funkcja służąca do dodawania nowego kanału
     *
     * @param channelName nazwa nowego kanału
     * @param usersName   Lista użytkowników znajdujących się w kanale
     */
    private void addNewChannel(String channelName, ArrayList<String> usersName) {
        for (ChannelListener one : channelListeners) {
            one.addChannelLater(channelName, usersName);
        }
        localHistory.createNewTable(channelName);
    }


    /**
     * Funkcja pozwalająca na wysłanie zdjęcia do danego kanału rozmó
     *
     * @param channelName     nazwa kanału do którego chcemy wysłać zdjęcie
     * @param screenshotShare obiekt który wysyłamy
     */
    public void sendPhotoToChannel(String channelName, ScreenshotInfo screenshotShare) {
        JSONObject json = new JSONObject();
        json.accumulate("Server", "sendPhoto");
        json.accumulate("channelName", channelName);
        json.accumulate("from", userName);
        out.println(json.toString());
        ObjectOutputStream outObject = null;
        try {
            echoSocket = new Socket(HOST, 5000);
            outObject = new ObjectOutputStream(echoSocket.getOutputStream());
            outObject.writeObject(screenshotShare);
            outObject.close();
//            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

        } catch (IOException ioe) {
            System.out.println("Nie wysłano zdjęcia");
        } finally {

        }

    }

    /**
     * Zamyka połączenie z serwerem
     */
    public void closeConnection() {
        JSONObject message = new JSONObject();
        isWorking = false;
        message.put("Server", "close");
        out.println(message.toString());
        try {
            stopClient();
        } catch (IOException ioe) {
            System.err.println("Nie zamknięto połączenia");
        }
    }
}


