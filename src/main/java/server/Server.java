package server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int PORT = 8080;
    private static final String EXIT_COMMAND = "-exit";
    private static final String FILE_COMMAND = "-file";

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

    public Server() {
        clients = new ArrayList<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
                broadcastMessage("[SERVER] " + clientHandler.getClientName() + " connected successfully.");
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger(Server.class.getName());
            logger.log(Level.SEVERE, "Error occurred while starting the server.", e);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
            for (ClientHandler client : clients) {
                client.disconnect();
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger(Server.class.getName());
            logger.log(Level.SEVERE, "Error occurred while stopping the server.", e);
        }
    }

    private void broadcastMessage(String message, ClientHandler client) {
        client.sendMessage(message);
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private String clientName;
        private LocalDateTime connectionTime;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.clientName = "client-" + (clients.size() + 1);
            this.connectionTime = LocalDateTime.now();
        }

        public String getClientName() {
            return clientName;
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    if (inputLine.equals(EXIT_COMMAND)) {
                        break;
                    } else if (inputLine.startsWith(FILE_COMMAND)) {
                        handleFileCommand(inputLine);
                    }
                }
            } catch (IOException e) {
                Logger logger = Logger.getLogger(ClientHandler.class.getName());
                logger.log(Level.SEVERE, "Error occurred while running the client handler.", e);
            } finally {
                disconnect();
                clients.remove(this);
                broadcastMessage("[SERVER] " + clientName + " disconnected.", this);
            }
        }

        public void sendMessage(String message) {
            writer.println(message);
        }

        public void disconnect() {
            try {
                reader.close();
                writer.close();
                clientSocket.close();
            } catch (IOException e) {
                Logger logger = Logger.getLogger(Client.class.getName());
                logger.log(Level.SEVERE, "Error occurred while disconnecting from the server.", e);
            }
        }

        private void handleFileCommand(String command) {
            String filePath = command.substring(FILE_COMMAND.length()).trim();
            sendMessage("File received successfully.");

            try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(filePath))) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                Logger logger = Logger.getLogger(Server.class.getName());
                logger.log(Level.SEVERE, "Error occurred while handling the file command.", e);
            }
        }


        public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
