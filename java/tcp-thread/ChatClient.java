// استيراد الكلاسات اللازمة للواجهة الرسومية
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

// إنشاء كلاس للكلاينت
public class ChatClient extends JFrame {

    // مساحة نصية لإدخال الرسائل
    private JTextField messageField;

    // مساحة نصية لعرض الرسائل
    private JTextArea messagesArea;

    // مجرى تدفق لإرسال البيانات إلى السيرفر
    private PrintWriter out;

    // مجرى تدفق لاستقبال البيانات من السيرفر
    private BufferedReader in;

    // مقبس الكلاينت
    private Socket clientSocket;

    // عنوان IP ورقم المنفذ الخاص بالسيرفر
    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 1234;

    // دالة البناء لإنشاء الواجهة الرسومية والاتصال بالسيرفر
    public ChatClient() {
        // إعطاء عنوان للنافذة
        super("Chat Client");

        // إنشاء مساحة نصية لإدخال الرسائل وإضافة مستمع للأحداث
        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            // دالة تنفذ عند الضغط على زر Enter
            public void actionPerformed(ActionEvent e) {
                // إرسال الرسالة المدخلة إلى السيرفر
                out.println(messageField.getText());

                // مسح محتوى المساحة النصية
                messageField.setText("");
            }
        });

        // إضافة المساحة النصية إلى النافذة
        add(messageField, BorderLayout.SOUTH);

        // إنشاء مساحة نصية لعرض الرسائل وجعلها غير قابلة للتعديل
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        add(new JScrollPane(messagesArea), BorderLayout.CENTER);

        // تحديد حجم النافذة وجعلها مرئية
        setSize(400, 300);
        setVisible(true);

        // محاولة الاتصال بالسيرفر وإنشاء مجاري التدفق
        try {
            // إنشاء مقبس كلاينت باستخدام عنوان IP ورقم المنفذ الخاص بالسيرفر
            clientSocket = new Socket(SERVER_IP, SERVER_PORT);
            messagesArea.append("Connected to server\n");

            // إنشاء مجرى تدفق لإرسال البيانات إلى السيرفر
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // إنشاء مجرى تدفق لاستقبال البيانات من السيرفر
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // إنشاء خيط جديد لاستقبال الرسائل من السيرفر
            Thread receiver = new Receiver();
            receiver.start();
        } catch (IOException e) {
            messagesArea.append("Error: Could not connect to server\n");
            e.printStackTrace();
        }
    }

    // صنف داخلي لاستقبال الرسائل من السيرفر بشكل متزامن
    private class Receiver extends Thread {

        // دالة لتنفيذ الخيط
        public void run() {
            try {
                // تكرار دائم لقراءة الرسائل من السيرفر
                while (true) {
                    // قراءة رسالة من السيرفر
                    String message = in.readLine();

                    // إذا كانت الرسالة فارغة، فإنهاء الخيط
                    if (message == null) {
                        break;
                    }

                    // عرض الرسالة في المساحة النصية
                    messagesArea.append(message + "\n");
                }
            } catch (IOException e) {
                messagesArea.append("Error: Could not read message from server\n");
                e.printStackTrace();
            } finally {
                // إغلاق المقبس
                try {
                    clientSocket.close();
                    messagesArea.append("Disconnected from server\n");
                } catch (IOException e) {
                    messagesArea.append("Error: Could not close client socket\n");
                    e.printStackTrace();
                }
            }
        }
    }

    // دالة الرئيسية لتشغيل البرنامج
    public static void main(String[] args) {
        // إنشاء كائن من الكلاينت وجعل البرنامج ينتهي عند إغلاق النافذة
        ChatClient client = new ChatClient();
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
