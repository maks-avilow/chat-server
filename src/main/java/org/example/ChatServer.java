package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private final AuthService authService;
    private final List<ClientHandler> clients;


    public ChatServer() {
        clients = new ArrayList<>();
        authService = new SimpleAuthService();
        System.out.println("Соединение с базой данных установлено");

        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("SERVER: Сервер запущен");
            SimpleAuthService.connectBase();
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("SERVER: Клиент подключился");
                new ClientHandler(socket, this);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            SimpleAuthService.disconnectBase();
        }
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clients "); //служебные сообщения
        for (ClientHandler client : clients)
        sb.append(client.getName()).append(" ");
        broadcast(sb.toString());
    }

    public void broadcast(String msg) {
        for (ClientHandler client : clients) {
            client.sendMessage(msg);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        System.out.println("SERVER: Клиент " + clientHandler.getName() + " login...");
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        System.out.println("SERVER: Клиент " + clientHandler.getName() + " logout...");
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNicknameBusy(String nickname) {
        for (ClientHandler client : clients) {
            if(client.getName().equals(nickname)) {
                return true;
            }
        }
        return false;
    }
  // реализуем личные сообщения в т.ч. от кого сообщения и кому
    public void sendMsgToClient(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o : clients) {  // ищем нужного клиента которому отправляем сообщение
            if (o.getName().equals(nickTo)) { // нашли
                o.sendMessage("от " + from.getName() + ": " + msg);
                from.sendMessage("клиенту " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMessage("Участника с ником " + nickTo + " нет здесь");
    }
}