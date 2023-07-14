package client;


import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public void connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            startMessageReader();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(Client.class.getName());
            logger.log(Level.SEVERE, "Error occurred while connecting to the server.", e);
        }
    }

    public void disconnect() {
        try {
            writer.println("-exit");
            socket.close();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(Client.class.getName());
            logger.log(Level.SEVERE, "Error occurred while disconnecting from the server.", e);
        }
    }

    public void sendFile(String filePath) {
        writer.println("-file " + filePath);
    }

    private void startMessageReader() {
        Logger logger = Logger.getLogger(Client.class.getName());
        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = reader.readLine()) != null) {
                    logger.log(Level.INFO, serverMessage);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error occurred while reading the message from the server.", e);
            }
        }).start();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }
}

