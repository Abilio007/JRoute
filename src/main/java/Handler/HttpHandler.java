package Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import Request.HttpRequest;
import Response.HttpResponse;
import Router.Router;

public class HttpHandler {

    private Socket client;
    private Router router;
    private BufferedReader in;
    private final String id;
    private Map<String, String> session;

    public HttpHandler(Socket client, Router router) throws IOException {
        this.client = client;
        this.router = router;

        this.in = new BufferedReader(
                new InputStreamReader(client.getInputStream())
        );

        this.id = UUID.randomUUID().toString();
    }

    public void handleClient() throws Exception {

        StringBuilder raw = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null && !line.isEmpty()) {
            raw.append(line).append("\r\n");
        }

        HttpRequest req = new HttpRequest(raw.toString());
        HttpResponse res = new HttpResponse(client.getOutputStream());

        router.handle(req, res);
    }

    public void close() throws IOException {
        in.close();
        client.close();
    }
}