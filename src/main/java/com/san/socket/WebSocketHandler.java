package com.san.socket;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.san.co.SocketRequestCO;
import com.san.co.SocketResponseCO;
import com.san.util.CommonUtil;
import com.san.util.WebSocketAPIHelperUtil;

// Reference : https://www.devglan.com/spring-boot/spring-websocket-integration-example-without-stomp
// Implement WebSocket without using STOMP
@Component
public class WebSocketHandler extends TextWebSocketHandler {

	List<WebSocketSession> sessions = new CopyOnWriteArrayList<WebSocketSession>();

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
		String data = message.getPayload();
		SocketRequestCO socketCO = null;
		SocketResponseCO socketResponseCO = new SocketResponseCO();
		if (data != null && data.length() > 0) {
			try {
				socketCO = (SocketRequestCO) CommonUtil.bindJSONToObject(data, SocketRequestCO.class);
			} catch (Exception e) {
			}
			if (socketCO != null) {
				socketResponseCO = WebSocketAPIHelperUtil.invokeSocketAPI(socketCO, session);
			} else {
				socketResponseCO.setMessage("Request couldn't be parsed.");
			}
		} else {
			socketResponseCO.setMessage("Invalid request made.");
		}
		TextMessage textResponse = new TextMessage(CommonUtil.convertToJsonString(socketResponseCO));
		if (socketCO.isBroadcastResponse()) {
			for (WebSocketSession webSocketSession : sessions) {
				webSocketSession.sendMessage(textResponse);
			}
		} else {
			session.sendMessage(textResponse);
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// the messages will be broadcasted to all users.
		for (WebSocketSession webSocketSession : sessions) {
			webSocketSession.sendMessage(new TextMessage("Anonymous joined conversation"));
		}
		sessions.add(session);
		session.sendMessage(new TextMessage("Connection establised"));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		for (WebSocketSession webSocketSession : sessions) {
			webSocketSession.sendMessage(new TextMessage("Anonymous left conversation"));
		}
	}
}