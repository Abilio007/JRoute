package Response;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private OutputStream out;

    public HttpResponse(OutputStream out) {
        this.out = out;
    }

    // envia texto simples
    public void send(int status, String body) throws Exception {
        send(status, "text/plain", body);
    }

    // envia HTML
    public void sendHtml(int status, String body) throws Exception {
        send(status, "text/html", body);
    }

    // envia JSON
    public void sendJson(int status, String body) throws Exception {
        send(status, "application/json", body);
    }

    // método genérico
    private void send(int status, String contentType, String body) throws Exception {
        String response = "HTTP/1.1 " + status + " OK\r\n" +
                          "Content-Type: " + contentType + "\r\n" +
                          "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                          "\r\n" +
                          body;
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}