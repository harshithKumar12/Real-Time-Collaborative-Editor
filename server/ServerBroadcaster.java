package server;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.IOException;
public class ServerBroadcaster {
    private final CopyOnWriteArrayList<ClientHandler> clients;
    public ServerBroadcaster() {
        this.clients = new CopyOnWriteArrayList<>();
    }
    public void addClient(ClientHandler client) {
        clients.add(client);
        System.out.println("[Broadcaster] Client added. Total clients: " + clients.size());
    }
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("[Broadcaster] Client removed. Total clients: " + clients.size());
    }
    public void broadcast(String message, ClientHandler sender) {
        int sentCount = 0;
        for (ClientHandler client : clients) {
            if (client != sender && client.isConnected()) {
                try {
                    client.sendMessage(message);
                    sentCount++;
                } catch (IOException e) {
                    System.err.println("[Broadcaster] Failed to send to client: " + e.getMessage());
                }
            }
        }
        System.out.println("[Broadcaster] Message broadcasted to " + sentCount + " client(s)");
    }

    /**
     * Broadcasts a message to ALL clients including sender
     * Used for system messages like user list updates
     *
     * @param message The message to broadcast
     */
    public void broadcastToAll(String message) {
        int sentCount = 0;
        for (ClientHandler client : clients) {
            if (client.isConnected()) {
                try {
                    client.sendMessage(message);
                    sentCount++;
                } catch (IOException e) {
                    System.err.println("[Broadcaster] Failed to send to client: " + e.getMessage());
                }
            }
        }
        System.out.println("[Broadcaster] Message broadcasted to all " + sentCount + " client(s)");
    }
    public String[] getConnectedUsernames() {
        return clients.stream()
                .filter(ClientHandler::isConnected)
                .map(ClientHandler::getUsername)
                .toArray(String[]::new);
    }
    public int getClientCount() {
        return (int) clients.stream()
                .filter(ClientHandler::isConnected)
                .count();
    }
}