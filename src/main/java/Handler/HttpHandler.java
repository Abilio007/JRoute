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
import Template.HttpTemplate;

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

		StringBuilder headers = new StringBuilder();
		String line;
		int contentLength = 0;

		// 1️⃣ Ler headers
		while ((line = in.readLine()) != null && !line.isEmpty()) {
			headers.append(line).append("\r\n");

			if (line.toLowerCase().startsWith("content-length:")) {
				contentLength = Integer.parseInt(line.split(":")[1].trim());
			}
		}

		// 2️⃣ Ler body (JSON, texto, etc.)
		char[] bodyChars = new char[contentLength];
		if (contentLength > 0) {
			in.read(bodyChars, 0, contentLength);
		}

		var body = HttpTemplate.bodyToMap(new String(bodyChars));

		// 3️⃣ Criar request completo
		HttpRequest req = new HttpRequest(headers.toString());
		req.body = body;

		HttpResponse res = new HttpResponse(client.getOutputStream());

		router.handle(req, res);
	}

	public void close() throws IOException {
		in.close();
		client.close();
	}
}