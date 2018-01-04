package bgu.spl181.net.impl.myProtocol;

import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.api.bidi.ConnectionHandler;
import bgu.spl181.net.srv.NonBlockingConnectionHandler;

import java.util.HashMap;
import java.util.Map;

public class MyConnections<T> implements Connections<T> {

    private HashMap <Integer, ConnectionHandler<T>> connectedClients=new HashMap<>();

    @Override
    public boolean send(int connectionId, T msg) {
        if (!connectedClients.containsKey(connectionId))
            return false;
        else {
            connectedClients.get(connectionId).send(msg);
            return true;
        }
    }

    @Override
    public void broadcast(T msg) {
        for (Map.Entry<Integer, ConnectionHandler<T>> entry :connectedClients.entrySet()) {
            entry.getValue().send(msg);
        }

    }

    @Override
    public void disconnect(int connectionId) {
        connectedClients.remove(connectionId);
    }
}
