package server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ServerTest2 {

    private static final int PORT = 8080;
    private static final String TEST_FILE_PATH = "src/test/resources/testfile.txt";
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
    public void testFileTransfer() throws IOException {
        Socket clientSocket = new Socket("localhost", PORT);
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        String firstMessage = reader.readLine();
        assertEquals("[SERVER] client-1 connected successfully.", firstMessage);

        writer.println("-file " + TEST_FILE_PATH);

        String response = reader.readLine();
        assertEquals("File received successfully.", response);

        assertTrue(checkIfFileExistsOnServer(TEST_FILE_PATH));

        writer.println("-exit");

        String disconnectionMessage = reader.readLine();
        assertEquals("[SERVER] client-1 disconnected.", disconnectionMessage);

        clientSocket.close();
    }

    private boolean checkIfFileExistsOnServer(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }
}

