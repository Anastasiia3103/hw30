package client;

import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class ClientTest3 {

    private static final int SERVER_PORT = 8080;

    @Test
    public void testMessageReceiving() throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        Socket clientSocket = serverSocket.accept();

        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        Client client = new Client();

        client.connect();

        writer.println("Hello from the server!");

        String receivedMessage = reader.readLine();
        assertEquals("Hello from the server!", receivedMessage);

        serverSocket.close();
        clientSocket.close();
    }
}

