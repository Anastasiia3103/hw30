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

public class ServerTest3 {

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
    public void testMultipleClientsConnectionAndBroadcast() throws IOException {
        Socket client1 = new Socket("localhost", PORT);
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        PrintWriter writer1 = new PrintWriter(client1.getOutputStream(), true);

        String client1FirstMessage = reader1.readLine();
        assertEquals("[SERVER] client-1 connected successfully.", client1FirstMessage);

        Socket client2 = new Socket("localhost", PORT);
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        PrintWriter writer2 = new PrintWriter(client2.getOutputStream(), true);

        String client2FirstMessage = reader2.readLine();
        assertEquals("[SERVER] client-2 connected successfully.", client2FirstMessage);

        writer1.println("Hello from client1!");

        String client1BroadcastMessage = reader1.readLine();
        assertEquals("[SERVER] Client-2 connected successfully.", client1BroadcastMessage);

        writer2.println("Hello from client2!");

        String client2BroadcastMessage = reader2.readLine();
        assertEquals("[SERVER] Client-1 connected successfully.", client2BroadcastMessage);

        writer1.println("-exit");

        String client1DisconnectionMessage = reader1.readLine();
        assertEquals("[SERVER] client-1 disconnected.", client1DisconnectionMessage);

        writer2.println("-exit");

        String client2DisconnectionMessage = reader2.readLine();
        assertEquals("[SERVER] client-2 disconnected.", client2DisconnectionMessage);

        client1.close();
        client2.close();
    }
}
