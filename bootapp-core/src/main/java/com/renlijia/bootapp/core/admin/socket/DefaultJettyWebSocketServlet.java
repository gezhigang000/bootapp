package com.renlijia.bootapp.core.admin.socket;

import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;

public class DefaultJettyWebSocketServlet extends JettyWebSocketServlet {
    @Override
    protected void configure(JettyWebSocketServletFactory jettyWebSocketServletFactory) {

        jettyWebSocketServletFactory.register(DefaultJettyWebSocket.class);

    }
}
