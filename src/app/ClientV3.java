package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** ------------------------------------------------
 *  --- Класс, описывающий логику работы клиента ---
 *  ------------------------------------------------
 * 1. При создании объекта ClientV3 идет считывание имени пользователя с консоли.
 * 2. Создается сокет, подключающийся к сокету ServerV3.
 * 3. Запуск потока для Sender.
 * 4. Создание потока на чтение сообщений из сокета.
 * 5. Цикл на чтение и распечатку сообщений из потока.
 */

public class ClientV3 {
    private final int PORT = 5000;
    private final String HOST = "localhost";
    private ExecutorService exec = Executors.newCachedThreadPool();
    private String clientName;

    public static void main(String[] args) throws Exception {
        new ClientV3();
    }

    public ClientV3() {
        try {
            Scanner scn = new Scanner(System.in); // считывание первого сообщения(имени) пользователя
            System.out.println("Введите имя пользователя: ");
            clientName = scn.nextLine();

            Socket socket = new Socket(HOST, PORT); // подключение сокета
            exec.execute(new Sender(socket)); //выполнение класса рассылки сообщений
            System.out.println("[" + clientName + "] Hello, welcome to chat room!");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // поток на чтение сокета сервера

            String msg;
            while ((msg = br.readLine()) != null) { // цикл на принятие сообщения с сервера
                System.out.println(msg);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** --------------------------------------------------------------
     *  ---------- Клиентский класс для работы с сообщениями ---------
     *  --------------------------------------------------------------
     * 1. При создании объекта Sender, в него передается сокет сервера, к которому организуется подключение пользователя.
     * 2. Запускается метод run().
     * 3. Создается поток на чтение из консоли.
     * 4. Отправка имени клиента в поток.
     * 5. В цикле while идет непрерывное чтение входящего, из консоли, потока данных (сообщения клиента), и последующая отправка сообщений в поток сокета.
     * 6. Если входящее сообщение - "bye", идет закрытие потоков на чтение/запись.
     *
     */
    class Sender implements Runnable {
        private Socket socket;

        public Sender(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // поток на чтение из консоли
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true); // поток на запись в сокет
                String msg;

                pw.println(clientName); // запись в поток
                System.out.println("Можете печатать сообщение: ");
                // цикл на отправку сообщений пользователем
                while (true) {
                    msg = br.readLine();
                    pw.println(msg);

                    if (msg.trim().equals("bye")) {
                        pw.close();
                        br.close();
                        exec.shutdownNow();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Client " + clientName + " left room" );
                System.out.println(e.getMessage());
            }
        }
    }
}
