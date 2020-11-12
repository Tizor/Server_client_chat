package app.oldVersion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/** ------------------------------------------------
 *  --- Класс, описывающий логику работы сервера ---
 *  ------------------------------------------------
 * 1. Сервер запускает бесконечный цикл, чтобы продолжать принимать входящие запросы.
 * 2. Когда приходит запрос, на него назначается новый поток для обработки коммуникационной части.
 * 3. Сервер сохраняет имя клиента в списке, чтобы отслеживать подключенные устройства.
 * 4. В списке хранится объект потока, соответствующий текущему запросу.
 * 5. Вспомогательный класс использует этот список, чтобы найти имя получателя, которому должно быть доставлено сообщение.
 * 6. Поскольку этот список содержит все потоки, класс обработчика может использовать его для успешной доставки сообщений определенным клиентам.
 * 7. Вызов метода start()
 */
public class ServerV2 {
    // порт
    private static final int PORT = 5000;
    private static String clientName;
    // список клиентов, которые будут подключаться к серверу
    public static List<ClientHandlerV2> clients = new ArrayList<>();

    // счетчик клиентов
    private static int i = 0;

    public static void main(String[] args) throws IOException {
        // сервер слушает порт
        ServerSocket serverSocket = new ServerSocket(PORT);

        Socket socket;
        System.out.println("Сервер запущен и ожидает новых запросов");
        // запуск бесконечной петли для ожидания подключений
        while(true) {

            // ожидание подключения
            socket = serverSocket.accept();
            socket.setSoTimeout(10_000);
//            System.out.println("Получен новый запрос от клиента: " + socket);
            System.out.println("Получен запрос от нового клиента");

            // создание input и output потоков
            // поток чтения из сокета
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            // поток записи в сокет
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

//            System.out.println("Создание нового ClientHandler для нового клиента...");
            clientName = dis.readUTF();
            // Создание нового clientHandler для этого запроса.
            ClientHandlerV2 mtch = new ClientHandlerV2(socket,clientName, dis, dos);
            System.out.println("Новый пользователь " + clientName + " подключен");


            // Создание нового потока для этого объекта (clientHandler).
            Thread t = new Thread(mtch);

//            System.out.println("Добавление клиента в список клиентов");

            // Добавление клиента в список клиентов
            clients.add(mtch);
//            clients.forEach(System.out::println);

            // запуск потока
            t.start();

        }

    }

}
