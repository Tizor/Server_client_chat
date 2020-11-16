package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** ------------------------------------------------
 *  --- Класс, описывающий логику работы сервера ---
 *  ------------------------------------------------
 * 1. Создание объекта Server.
 * 2. Создание объекта ServerSocket.
 * 3. Создание кэшированного пула потоков.
 * 4. Запуск while цикла.
 * 5. Ожидание подключения клиента к сокету.
 * 6. Добавление подключенного клиента в список активных пользователей.
 * 7. Создание объекта ClientHandlerV3, в который передается сокет активного пользователя.
 * 8. Запуск потока для ClientHandlerV3
 */

public class ServerV3 {
    private static final int PORT = 5000; // номер порта
    private static List<Socket> socketList = new ArrayList<>(); // сохраняет подключенные объекты
    private ExecutorService exec; // ExecutorService для управления потоками
    private ServerSocket serverSocket; // Серверный сокет
    private Socket client; // Клиентский сокет
    private PrintWriter sendAcceptToClient; // Поток на отсылку информационных сообщений клиенту со стороны сервера

    public static void main(String[] args) {
        new ServerV3();
    }

    /**
     * Все делается через дефолтный конструктор. В таком случае, для запуска программы,
     * будет достаточно всего лишь создания объекта.
     */
    public ServerV3() {
        try {
            serverSocket = new ServerSocket(PORT);
            // Чтобы лучше управлять системными ресурсами, кэшированные пулы потоков удаляют потоки, которые остаются простаивающими в течение одной минуты.
            // Кэшированные пулы потоков используют «синхронную передачу обслуживания» для постановки новых задач в очередь.
            exec = Executors.newCachedThreadPool();
            System.out.println("Сервер запущен и ожидает новых запросов");
            // запуск бесконечной петли для ожидания подключений
            while (true) {
                client = serverSocket.accept(); // ожидание подключения
                sendAcceptToClient = new PrintWriter(client.getOutputStream(), true);
                sendAcceptToClient.println("Вы подключились к серверу"); // сообщение клиенту об удачном подключении к серверу

                socketList.add(client); // добавление пользователя в список подключенных клиентов
                ClientHandlerV3 clientHandler = new ClientHandlerV3(client);
                exec.execute(clientHandler); // При вызове метода execute исполняется поток thread. То есть, метод execute запускает указанный поток на исполнение.
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Socket> getSocketList() {
        return socketList;
    }
}
