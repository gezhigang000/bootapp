package cc.starapp.bootapp.core.admin.socket;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@WebSocket
public class DefaultJettyWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(DefaultJettyWebSocket.class);

    private static final Set<Session> SESSIONS = new HashSet<>();


    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        logger.info("WebSocket Close: {} - {}", statusCode, reason);
    }

    @OnWebSocketConnect
    public void onWebSocketConnect(Session sess) {
        SESSIONS.add(sess);
        logger.info("Endpoint connected: {}", sess);
    }


    @OnWebSocketError
    public void onWebSocketError(Throwable cause) {
        logger.warn("WebSocket Error", cause);
    }

    @OnWebSocketMessage
    public void onWebSocketText(String message) {
        logger.debug("Echoing back text message [{}]", message);
    }

    public static void push(String text){
        Iterator<Session> iterator = SESSIONS.iterator();
        while(iterator.hasNext()){
            Session next = iterator.next();
            if(!next.isOpen()){
                iterator.remove();
                continue;
            }
            try {
                next.getRemote().sendString(text);
            } catch (Exception e) {
                logger.error("",e);
            }
        }
    }
}
