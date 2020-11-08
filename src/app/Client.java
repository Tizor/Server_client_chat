package app;

import java.io.*;
import java.net.Socket;

public class Client {
    static final int PORT = 5000;
    static final String HOST = "localhost";

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(HOST, PORT);
//отослать что-то с помощью in, и принять с помощью out, и наоборот
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // поток чтения из сокета
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // поток записи в сокет
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        System.out.println("Вы что-то хотели сказать? Введите это здесь:");

        String word;
        while (true){
            word = reader.readLine();
            out.write(word + "\n");
            out.flush();
            String serverWord = in.readLine();

            System.out.println(serverWord);
            if(word.equals("exit")) { break;}
        }

        socket.close();
        in.close();
        out.close();
        System.out.println("Клиент был закрыт...");

    }
}
