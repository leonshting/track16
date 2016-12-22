package track.messenger.net;

import track.messenger.User;
import track.messenger.store.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 *
 */


public class MessengerServer implements Runnable {


    private int serverPort;
    private static final String dbURL = "jdbc:sqlite:/Users/leonshting/Programming/track/" +
            "track16/src/main/resources/messenger";

    private ServerSocket serverSocket;
    private boolean isStopped;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private StoreSet storeSet;

    public Map<User, Session> sessions = null;

    public MessengerServer(int port) {
        sessions = new ConcurrentHashMap<>();
        serverPort = port;
        StoreSet.setUrl(dbURL);
        storeSet = StoreSet.getInstance();
    }

    public void run() {
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    break;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            this.threadPool.execute(new ServerWorker(clientSocket, storeSet, sessions));

        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.");
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }

    public static void main(String[] args) {
        MessengerServer server = new MessengerServer(9999);
        new Thread(server).start();

        try {
            while (true) {
                Thread.sleep(2 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
}
