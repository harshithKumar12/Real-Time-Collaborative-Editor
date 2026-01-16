package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import static shared.SharedConstants.*;
public class ServerMain {
    private ServerSocket serverSocket;
    private ServerBroadcaster broadcaster;
    private boolean running;
    public ServerMain() {
        this.broadcaster = new ServerBroadcaster();
        this.running = false;
    }
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("========================================");
            System.out.println("Collaborative Editor Server Started");
            System.out.println("Listening on port: " + port);
            System.out.println("Waiting for clients...");
            System.out.println("========================================");
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n[Server] New connection from: " + clientSocket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, broadcaster);
                Thread clientThread = new Thread(clientHandler);
                clientThread.setDaemon(true);
                clientThread.start();
            }
            
        } catch (IOException e) {
            if (running) {
                System.err.println("[Server] Error accepting connection: " + e.getMessage());
            }
        }
    }
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("[Server] Server stopped");
        } catch (IOException e) {
            System.err.println("[Server] Error stopping server: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        int port = SERVER_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default: " + SERVER_PORT);
            }
        }
        ServerMain server = new ServerMain();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[Server] Shutting down...");
            server.stop();
        }));
        server.start(port);
    }
}