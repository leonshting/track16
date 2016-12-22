package track.messenger.net;

import track.messenger.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * Created by leonshting on 21.12.16.
 */

public class ServerWorker implements Runnable {

    private Socket clientSocket;
    private StoreSet storeSet;
    private Map<User, Session> sessions;

    public ServerWorker(Socket clientSocket, StoreSet storeSet, Map<User, Session> sessions) {
        this.clientSocket = clientSocket;
        this.sessions = sessions;
        this.storeSet = storeSet;
    }

    public void run() {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {
            Session session = new Session(clientSocket, input, output, storeSet.getUserStore(),
                    storeSet.getMessageStore(), storeSet.getChatStore(), sessions);
            session.sessionRun();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}