package server;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class ServerTest1 {

    private static final int PORT = 8080;
    private Server server;

    @Before
    public void setUp() {
        server = new Server();
        new Thread(() -> server.start()).start();
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testClientConnectionAndDisconnection() throws IOException {
        Socket clientSocket = new Socket("localhost", PORT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        String firstMessage = reader.readLine();
        assertEquals("[SERVER] client-1 connected successfully.", firstMessage);

        writer.println("-exit");

        String disconnectionMessage = reader.readLine();
        assertEquals("[SERVER] client-1 disconnected.", disconnectionMessage);

        clientSocket.close();
    }
}
