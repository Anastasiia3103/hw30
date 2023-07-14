package client;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class ClientTest1 {

    private static final int SERVER_PORT = 8080;

    @Test
    public void testClientConnectionAndDisconnection() throws IOException {
        // Start a mock server
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        Socket clientSocket = serverSocket.accept();

        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        // Start the client
        Client client = new Client();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));

        // Connect the client
        client.connect();

        // Verify the connection message received from the server
        String connectionMessage = reader.readLine();
        assertEquals("[SERVER] client-1 connected successfully.", connectionMessage);

        // Disconnect the client
        client.disconnect();

        // Verify the disconnection message received from the server
        String disconnectionMessage = reader.readLine();
        assertEquals("[SERVER] client-1 disconnected.", disconnectionMessage);

        // Verify the client output
        String consoleOutput = outputStream.toString().trim();
        assertEquals("[SERVER] client-1 connected successfully.", consoleOutput);

        // Clean up resources
        serverSocket.close();
        clientSocket.close();
    }
}

