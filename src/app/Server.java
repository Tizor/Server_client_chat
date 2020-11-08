package app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static final int PORT = 5000;


    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket = serverSocket.accept();
//отослать что-то с помощью in, и принять с помощью out, и наоборот
            // поток чтения из сокета
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // поток записи в сокет
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String word;
            while (true) {
                word = in.readLine();
                if (word.equals("exit")) {
                    out.write("Привет, это Сервер! Подтверждаю прерывание процесса: " + word + "\n");
                    System.out.println("Клиент прервал процесс командой: " + word);
                    out.flush();
                    break;
                }
                out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");
                out.flush();
                System.out.println("Клиент написал: " + word);
            }
            socket.close();
            in.close();
            out.close();
            serverSocket.close();
            System.out.println("Сервер закрыт!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
