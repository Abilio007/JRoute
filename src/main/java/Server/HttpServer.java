package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import Handler.HttpHandler;
import Router.Router;

public class HttpServer {
    private int port;
    private static Router router;
    
    static{
        router = new Router();
    }
    
    public HttpServer(){
        this.port = 8080;
    }

    public HttpServer(int port) {
        this.port = port;    
    }
    
    public Router getRouter(){
        return router;
    }

    public void start() throws Exception {
    ServerSocket server = new ServerSocket(port);
    System.out.println("Servidor HTTP na porta " + port);

    while (true) {
        Socket client = server.accept();

        new Thread(() -> {
            try {
                HttpHandler handler = new HttpHandler(client, router);
                handler.handleClient();
                handler.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
}