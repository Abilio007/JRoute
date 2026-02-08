package Request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    public String method;
    public String path;
    public Map<String, Object> body;

    public Map<String, String> headers = new HashMap<>();
    public Map<String, String> params = new HashMap<>();
    public Map<String, String> pathParams = new HashMap<>();

    public HttpRequest(String raw) {

        if (raw == null || raw.isEmpty()) {
            throw new IllegalArgumentException("Requisição vazia");
        }

        String[] lines = raw.split("\r\n");

        if (lines.length == 0) {
            throw new IllegalArgumentException("Nenhuma linha HTTP encontrada");
        }

        // Linha: GET /path?x=1&y=2 HTTP/1.1
        String[] firstLine = lines[0].split(" ");

        if (firstLine.length < 2) {
            throw new IllegalArgumentException("Linha HTTP inválida: " + lines[0]);
        }

        method = firstLine[0];

        String fullPath = firstLine[1];

        // separa path e query
        String[] parts = fullPath.split("\\?", 2);
        path = normalize(parts[0]);

        if (parts.length == 2) {
            parseParams(parts[1]);
        }

        // parse headers
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (!line.contains(":")) continue;

            String[] h = line.split(":", 2);
            headers.put(
                h[0].toLowerCase().trim(),
                h[1].trim()
            );
        }
    }

    /* ===================== PARAMS ===================== */

    private void parseParams(String queryString) {
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);

            String key = decode(kv[0]);
            String value = kv.length > 1 ? decode(kv[1]) : "";

            params.put(key, value);
        }
    }

    public String get(String name) {
        if (pathParams.containsKey(name))
            return pathParams.get(name);
        return params.get(name);
    }

    public int getInt(String name) {
        return Integer.parseInt(require(name));
    }

    public long getLong(String name) {
        return Long.parseLong(require(name));
    }

    private String require(String name) {
        String value = get(name);
        if (value == null)
            throw new IllegalArgumentException(
                "Parâmetro '" + name + "' não encontrado"
            );
        return value;
    }

    /* ===================== JSON ===================== */

    public boolean isJson() {
        return headers
            .getOrDefault("content-type", "")
            .contains("application/json");
    }

    /* ===================== UTILS ===================== */

    private String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private String normalize(String p) {
        if (!p.startsWith("/")) p = "/" + p;
        if (p.endsWith("/") && p.length() > 1)
            p = p.substring(0, p.length() - 1);
        return p;
    }
}