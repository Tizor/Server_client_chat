package app.oldVersion;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


/** --------------------------------------------------------------
 *  --- Вспомогательный класс для обработки различных запросов ---
 *  --------------------------------------------------------------
 *  Вместе с сокетом и потоками мы вводим переменную имени, которая
 *  будет содержать имя клиента, подключенного к серверу.
 */
public class ClientHandlerV2 implements Runnable {

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
            }catch (SocketException e) {
                System.out.println("Пользователь "+  this.name + " отключен");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
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