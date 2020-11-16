package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/** --------------------------------------------------------------
 *  --- Вспомогательный класс для обработки различных запросов ---
 *  --------------------------------------------------------------
 * 1. При создании объекта ClientHandlerV3, в него передается сокет акттивного пользователя, подключившегося через serverSocket.
 * 2. Создается поток на чтение из передаваемого сокета.
 * 3. Первые данные из потока - имя пользователя, которое он вводит при входе в чат.
 * 4. Далее идет сообщение, информирующее чат о новом пользователе.
 * 5. Выполнение метода sendMessage().
 * 6. Итого: при создании нового ClientHandlerV3 в чат отправляется сообщение из п.4.
 * 7. Запускается метод run().
 * 8. В цикле while идет непрерывное чтение входящего потока, созданном в конструкторе ClientHandlerV3 из п.2.
 * 9. Если входящее сообщение - "bye", то:
 *  9.1 Идет удаление пользователя из списка активных пользователей.
 *  9.2 Идет закрытие потоков на чтение/запись.
 *  9.2 Создается сообщение о покидании пользователем чата.
 *  9.3 Закрытие пользовательского сокета.
 *  9.4 Выполнение метода sendMessage().
 * 10. Если входящее сообщение не "bye", то:
 *  10.1 Из входящего потока ClientHandlerV3 формируется сообщение msg.
 *  10.2 Выполнение метода sendMessage().
 *
 *  --------------------------------------------------------------
 *  ------------------- Метод sendMessage() ----------------------
 *  --------------------------------------------------------------
 *  По списку клиентов из класса ServerV3 делает рассылку формируемого сообщения.
 *  В цикле for берем сокет каждого активного клиента, берем его OutputStream и отправляем в него соответствющее сообщение.
 *  Заметка:
 *  sendMessage() - сомнительный метод в плане организации кода.
 *  Он использует ресурсы, которые не передаются в него напрямую, что нехорошо. Основная проблема состоит в том, что я не
 *  смог придумать, как передать в sendMessage() поток pw, т.к. для инициализации потока необходим клиент из списка пользователей
 *  с сервера, который достается при обходте в цикле for, уже в самом методе sendMessage(); т.е мы не можем использовать передачу
 *  параметров в метод т.к. для инцилизации параметров нужны данные из метода, котрые не могут быть получены до самого методо.
 *  Можно вообще не использовать этот метод и писать все напрямую в коде, однако это ведет к дублированию кода, что, опять же,
 *  является плохой практикой.
 */

public class ClientHandlerV3 implements Runnable {
    private Socket socket; // Клиентский сокет
    private BufferedReader br; // Поток на чтение сокета
    private PrintWriter pw; // Поток на запись в сокет
    private String msg; // Обрабатываемое сообщение
    private List<Socket> userSocketList = ServerV3.getSocketList(); // Список пользователей с сервера
    private String clientName; // Имя пользователя

    public ClientHandlerV3(Socket socket) throws IOException {
        this.socket = socket;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // поток чтения из сокета
        clientName = br.readLine(); // считывание имени клиента
        msg = returnGreetingOrGoodbyeMessage(1, clientName, userSocketList.size()); // см. описание метода
        sendMessageToAllClient(userSocketList, msg); // рассылка msg в чат о подключении нового пользователя
    }

    public void run() {
        try {
            // цикл на непрерывное чтение сообзений из сокета
            while ((msg = br.readLine()) != null) {
                if (msg.trim().equals("bye")) { // условие выхода из цикла по команде "bye"
                    userSocketList.remove(socket); // удаление пользователя из списка пользователей на сервере
                    br.close(); // закрытие потока на чтение
                    pw.close(); // закрытие потока на запись
                    socket.close(); // закрытие сокета
                    msg = returnGreetingOrGoodbyeMessage(0, clientName, userSocketList.size());
                    sendMessageToAllClient(userSocketList, msg); // рассылка msg в чат об отключении пользователя
                    break;
                } else {
                    msg = "[" + clientName + "] says: " + msg; //обычный msg в чат
                    sendMessageToAllClient(userSocketList, msg); // рассылка msg в чат
                }
            }
        } catch (IOException e) {
            System.out.println("Client " + clientName + " left room" );
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод для рассылки сообщения всем в данном чате
     * В цикле for берем сокет каждого активного клиента, берем его OutputStream и отправляем в него соответствующее сообщение.
     * Метод flush() для BufferedWriter – он выталкивает содержимое буфера. Если этого не сделать, информация не будет передана, а, следовательно, не будет получена.
     * Если вам это кажется неудобным, не расстраивайтесь, всегда можно воспользоваться классом PrintWriter,
     * которым нужно обернуть out, указать вторым аргументом true и тогда выталкивание из буфера будет происходить автоматически.
     */

    private void sendMessageToAllClient(List<Socket> userSocketList, String msg) throws IOException {
        System.out.println(msg);
        for (Socket client : userSocketList) {
            pw = new PrintWriter(client.getOutputStream(), true); // открытие потока на рассылку всем client из списка сообщения msg
            pw.println(msg); // пишет msg в поток
        }
    }
    /**
     * Метод формирования GreetingMessage и GoodbyeMessage. Создан для удобочитаемости кода.
     * messageType - параметр для выбора типа сообщения. 1 - GreetingMessage, 2 - GoodbyeMessage.
     * clientName - имя пользователя.
     * userSocketListSize - размер списка подключенных пользователей.
     */
    private String returnGreetingOrGoodbyeMessage(int messageType, String clientName, int userSocketListSize) {
        if (messageType == 1) { // Сообщение при входе пользователя в чат
            StringBuilder greetingMsg = new StringBuilder();
            greetingMsg.append("[");
            greetingMsg.append(clientName);
            greetingMsg.append("] Enter the chat room! The current chat room has [");
            greetingMsg.append(userSocketListSize);
            greetingMsg.append("] person");
            return greetingMsg.toString();
        } else { // Сообщение при выходе пользователя из чата
            StringBuilder goodbyeMsg = new StringBuilder();
            goodbyeMsg.append("[");
            goodbyeMsg.append(clientName);
            goodbyeMsg.append("] Leave the chat room! The current chat room has [");
            goodbyeMsg.append(userSocketListSize);
            goodbyeMsg.append("] person");
            return goodbyeMsg.toString();
        }
    }
// v.0.1 первая версия метода для рассылки сообщения всем в данном чате
//    private void sendMessage() throws IOException {
//        System.out.println(msg);
//        for (Socket client : userSocketMap) {
//            pw = new PrintWriter(client.getOutputStream(), true); // открытие потока на рассылку всем client из списка сообщения msg
//            pw.println(msg); // пишет msg в поток
//        }
//    }
}
