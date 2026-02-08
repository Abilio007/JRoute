package Router;

import java.util.*;
import java.util.function.Function;

import Request.HttpRequest;
import Response.HttpResponse;
import Template.HttpTemplate;

public class Router {

    // -----------------------
    // Classe interna da rota
    // -----------------------
    private static class Route {
        String method;
        String pattern;
        RouteHandler handler;

        Route(String method, String pattern, RouteHandler handler) {
            this.method = method;
            this.pattern = pattern;
            this.handler = handler;
        }
    }

    private List<Route> routes = new ArrayList<>();

    // -----------------------
    // Registrar GET
    // -----------------------
    public void get(String pattern, RouteHandler handler) {
        routes.add(new Route("GET", pattern, handler));
    }

    public void get(RouteHandler handler, String pattern) {
        get(pattern, handler);
    }

    // -----------------------
    // Handle de requisição
    // -----------------------
    public void handle(HttpRequest req, HttpResponse res) throws Exception {
        for (Route r : routes) {
            if (!r.method.equals(req.method)) continue;

            Map<String, String> pathParams = match(r.pattern, req.path);
            if (pathParams != null) {
                req.pathParams = pathParams; // adiciona path params
                Object result = r.handler.handle(req);
                sendResponse(res, result);
                return;
            }
        }

        res.send(404, "Rota não encontrada");
    }

    // -----------------------
    // Decide como enviar a resposta
    // -----------------------
    private void sendResponse(HttpResponse res, Object result) throws Exception {
        if (result == null) {
            res.send(204, "");
        } else if (result instanceof String) {
            String s = ((String) result).trim();
            if (s.startsWith("<")) {
                // HTML detectado
                res.sendHtml(200, s);
            } else {
                // texto simples
                res.send(200, s);
            }
        } else if (result instanceof Integer) {
            res.send((Integer) result, "");
        } else if (result instanceof Map){
            res.sendJson(200,HttpTemplate.json(result));
         }else if(result instanceof List) {
            res.sendJson(200, HttpTemplate.json(result));
        } else {
            // objeto custom → JSON
            res.sendJson(200, HttpTemplate.json(result));
        }
    }

    // -----------------------
    // Comparar pattern com path e extrair path params
    // -----------------------
    private Map<String, String> match(String pattern, String path) {
        String[] p1 = normalize(pattern).split("/");
        String[] p2 = normalize(path).split("/");

        if (p1.length != p2.length) return null;

        Map<String, String> params = new HashMap<>();

        for (int i = 0; i < p1.length; i++) {
            if (p1[i].startsWith("{") && p1[i].endsWith("}")) {
                String name = p1[i].substring(1, p1[i].length() - 1);
                params.put(name, p2[i]);
            } else if (!p1[i].equals(p2[i])) {
                return null;
            }
        }

        return params;
    }

    // -----------------------
    // Normalizar paths
    // -----------------------
    private String normalize(String p) {
        if (p.startsWith("/")) p = p.substring(1);
        if (p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

}