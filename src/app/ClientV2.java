package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/** ------------------------------------------------
 *  --- Класс, описывающий логику работы клиента ---
 *  ------------------------------------------------
 *
 *  В реализации на стороне клиента будет создаваться два потока:
 *  -> SendMessage
 *  SendMessage: этот поток будет использоваться для отправки сообщения другим клиентам.
 *  Работа очень проста, требуется ввести сообщение (которое нужно отправить) и получателя (которому нужно доставить).
 *  Обратите внимание, что эта реализация предполагает, что сообщение имеет формат сообщение # получатель,
 *  где получатель - это имя получателя.
 *  Затем он записывает сообщение в свой выходной поток, который подключен к обработчику этого клиента.
 *  Обработчик разбивает сообщение и часть получателя и доставляет конкретному получателю.
 *  -> ReadMessage
 *  ReadMessage: аналогичный подход используется для создания потока для получения сообщений.
 *  Когда какой-либо клиент пытается выполнить запись во входном потоке этого клиента, мы используем метод readUTF () для чтения этого сообщения.
 * Описание процесса работы:
 * 1. Установите подключение к Socket
 * 2. Общение
 * 3. Связь происходит с помощью потоков readMessage и sendMessage.
 *    Отдельные потоки для чтения и записи обеспечивают одновременную отправку и получение сообщений.
 *
 *
 */
public class ClientV2 {
    final static int ServerPort = 5000;

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // Установка соединения
        Socket s = new Socket(ip, ServerPort);

        // получение input и out потока
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    // получение сообщение от отправителя
                    String msg = scn.nextLine();

                    try {
                        // запись в output поток
                        dos.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // чтение отправляемого сообщения нужному получателю
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}
