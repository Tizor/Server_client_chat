package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
//    public static Map<String, Socket> userSocketMap = new TreeMap<>(); // сохраняет подключенные объекты
    private ExecutorService exec; // ExecutorService для управления потоками
    private ServerSocket serverSocket; // Серверный сокет
    private Socket client; // Клиентский сокет
    private static String clientName;

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

                socketList.add(client); // добавление пользователя в список подключенных клиентов
//                                userSocketMap.put(clientHandler.getClientName(), client); // добавление пользователя в список подключенных клиентов
                ClientHandlerV3 clientHandler = new ClientHandlerV3(client);
                exec.execute(clientHandler); // При вызове метода execute исполняется поток thread. То есть, метод execute запускает указанный поток на исполнение.

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public Map<String, Socket> getUserSocketMap() {
//        return userSocketMap;
//    }


    public static List<Socket> getSocketList() {
        return socketList;
    }
}
