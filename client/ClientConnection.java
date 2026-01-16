package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class ClientConnection {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread listenerThread;
    private volatile boolean connected;
    private MessageListener messageListener;
    public interface MessageListener {
        void onMessageReceived(String message);
        void onConnectionLost();
    }
    public ClientConnection(MessageListener messageListener) {
        this.messageListener = messageListener;
        this.connected = false;
    }
    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(0); // No timeout for reading
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            listenerThread = new Thread(this::listenForMessages, "ClientListener");
            listenerThread.setDaemon(true);
            listenerThread.start();
            System.out.println("[ClientConnection] Connected to server: " + host + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("[ClientConnection] Failed to connect: " + e.getMessage());
            connected = false;
            return false;
        }
    }
    public boolean sendMessage(String message) {
        if (connected && writer != null && !writer.checkError()) {
            try {
                writer.println(message);
                writer.flush();
                return !writer.checkError();
            } catch (Exception e) {
                System.err.println("[ClientConnection] Error sending message: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    private void listenForMessages() {
        try {
            String message;
            while (connected && (message = reader.readLine()) != null) {
                final String finalMessage = message;
                if (messageListener != null) {
                    messageListener.onMessageReceived(finalMessage);
                }
            }
        } catch (IOException e) {
            if (connected) {
                System.err.println("[ClientConnection] Connection lost: " + e.getMessage());
            }
        } finally {
            // Connection lost
            if (connected) {
                connected = false;
                if (messageListener != null) {
                    messageListener.onConnectionLost();
                }
            }
        }
    }
    public void disconnect() {
        if (!connected) {
            return;
        }
        System.out.println("[ClientConnection] Disconnecting...");
        connected = false;
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            System.err.println("[ClientConnection] Error closing writer: " + e.getMessage());
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("[ClientConnection] Error closing reader: " + e.getMessage());
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("[ClientConnection] Error closing socket: " + e.getMessage());
        }
        if (listenerThread != null && listenerThread.isAlive()) {
            try {
                listenerThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[ClientConnection] Disconnected from server");
    }
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed() && socket.isConnected();
    }
}
