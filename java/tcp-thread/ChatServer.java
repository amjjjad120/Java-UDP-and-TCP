// استيراد الكلاسات اللازمة
import java.io.*;
import java.net.*;
import java.util.*;

// إنشاء كلاس للسيرفر
public class ChatServer {

    // مجموعة لحفظ الكلاينتات المتصلة
    private HashSet<PrintWriter> clients;

    // مقبس سيرفر لاستقبال الطلبات
    private ServerSocket serverSocket;

    // رقم المنفذ الذي يستخدمه السيرفر
    private final int PORT = 1234;

    // دالة البناء لبدء السيرفر
    public ChatServer() {
        // إنشاء مجموعة فارغة للكلاينتات
        clients = new HashSet<>();

        // محاولة إنشاء مقبس سيرفر وبدء الاستماع
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            startListening();
        } catch (IOException e) {
            System.out.println("Error: Could not start server on port " + PORT);
            e.printStackTrace();
        }
    }

    // دالة لبدء الاستماع إلى الطلبات الواردة من الكلاينتات
    public void startListening() {
        // تكرار دائم لقبول الطلبات
        while (true) {
            try {
                // قبول طلب من كلاينت وإنشاء مقبس له
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new client has connected");

                // إنشاء مجرى تدفق لإرسال البيانات إلى الكلاينت
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // إضافة الكلاينت إلى المجموعة
                clients.add(out);

                // إنشاء خيط جديد لمعالجة الكلاينت
                Thread handler = new ClientHandler(clientSocket, out);
                handler.start();
            } catch (IOException e) {
                System.out.println("Error: Could not accept client");
                e.printStackTrace();
            }
        }
    }

    // دالة لبث الرسالة إلى جميع الكلاينتات المتصلة
    public void broadcast(String message) {
        // تكرار على كل كلاينت في المجموعة وإرسال الرسالة إليه
        for (PrintWriter out : clients) {
            out.println(message);
        }
    }

    // دالة الرئيسية لتشغيل البرنامج
    public static void main(String[] args) {
        // إنشاء كائن من السيرفر
        ChatServer server = new ChatServer();
    }

    // صنف داخلي لمعالجة الكلاينتات بشكل متزامن
    private class ClientHandler extends Thread {

        // مقبس الكلاينت
        private Socket clientSocket;

        // مجرى تدفق لإرسال البيانات إلى الكلاينت
        private PrintWriter out;

        // مجرى تدفق لاستقبال البيانات من الكلاينت
        private BufferedReader in;

        // دالة البناء لتهيئة المقبس ومجاري التدفق
        public ClientHandler(Socket clientSocket, PrintWriter out) {
            this.clientSocket = clientSocket;
            this.out = out;
            try {
                // إنشاء مجرى تدفق لاستقبال البيانات من الكلاينت
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                System.out.println("Error: Could not get input stream from client");
                e.printStackTrace();
            }
        }

        // دالة لتنفيذ الخيط
        public void run() {
            try {
                // تكرار دائم لقراءة الرسائل من الكلاينت
                while (true) {
                    // قراءة رسالة من الكلاينت
                    String message = in.readLine();

                    // إذا كانت الرسالة فارغة، فإنهاء الخيط
                    if (message == null) {
                        break;
                    }

                    // بث الرسالة إلى جميع الكلاينتات الأخرى
                    broadcast(message);
                }
            } catch (IOException e) {
                System.out.println("Error: Could not read message from client");
                e.printStackTrace();
            } finally {
                // إغلاق المقبس وإزالة الكلاينت من المجموعة
                try {
                    clientSocket.close();
                    clients.remove(out);
                    System.out.println("A client has disconnected");
                } catch (IOException e) {
                    System.out.println("Error: Could not close client socket");
                    e.printStackTrace();
                }
            }
        }
    }
}