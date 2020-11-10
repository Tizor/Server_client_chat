package app;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

/** --------------------------------------------------------------
 *  --- Вспомогательный класс для обработки различных запросов ---
 *  --------------------------------------------------------------
 *  Вместе с сокетом и потоками мы вводим переменную имени, которая
 *  будет содержать имя клиента, подключенного к серверу.
 *
 *  1. Когда обработчик получает какую-либо строку, он разбивает ее на часть сообщения и получателя.
 *  Для этой цели он использует Stringtokenizer с символом «#» в качестве разделителя.
 *  Здесь предполагается, что строка всегда имеет формат:
 *      сообщение # получатель
 * 2. Затем он выполняет поиск имени получателя в списке подключенных клиентов, хранящемся в виде вектора на сервере.
 * 3. Если он находит имя получателя в списке клиентов, он пересылает сообщение в свой выходной поток с именем отправителя, добавленным к сообщению.
 *
 *
 *
 */
public class ClientHandlerV2 implements Runnable {

    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket socket;
    boolean isloggedin;

    public ClientHandlerV2(Socket socket, String name,
                           DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.socket = socket;
        this.isloggedin=true;
    }

    @Override
    public void run() {
        String received;
        while (true)
        {
            try
            {
                // Чтение полученной строки из потока DataInputStream
                received = dis.readUTF();

                System.out.println(this.name + " : " + received);

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.socket.close();
                    break;
                }

                dos.writeUTF(this.name + " : " + received);
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try
        {
            // закрытие ресурсов
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Client -> " +
                "name: " + name +
                " , is logged in: " + isloggedin;
    }
}