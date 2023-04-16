import java.io.*;
import java.net.Socket;

public class FileUploader {
    public static void main(String[] args) {
        String filePath = "src/cat.jpg";
        String boundary = "===mpostnikov===";
        String serverAddress = "194.99.21.219";
        int port = 80;

        try {
            Socket socket = new Socket(serverAddress, port);
            OutputStream outputStream = socket.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(filePath);

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
            String fileName = new File(filePath).getName();

            // Создаем HTTP-заголовки
            writer.println("POST /upload.php HTTP/1.0");
            writer.println("Host: " + serverAddress);
            writer.println("Content-Type: multipart/form-data; boundary=" + boundary);
            writer.println("Connection: close");

            // Создаем тело запроса
            writer.println();
            writer.println("--" + boundary);
            writer.println("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"");
            writer.println("Content-Type: image/jpeg");
            writer.println();

            writer.flush();

            // Передаем файл
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            fileInputStream.close();

            // Завершаем запрос
            writer.println();
            writer.println("--" + boundary + "--");
            writer.flush();

            // Получаем ответ сервера
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Закрываем соединение
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}