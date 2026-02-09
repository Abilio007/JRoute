package test;

import Request.HttpRequest;
import Template.HttpTemplate;
import jRoute.Controller;
import mapping.GetMapping;
import mapping.PostMapping;

public class Home implements Controller {

    @GetMapping(path = "/")
    public String index(HttpRequest req) {
        return HttpTemplate.view("index.html");
    }

    @GetMapping(path = "/login")
    public String login(HttpRequest req) {
        return HttpTemplate.view("login.html");
    }

    @GetMapping(path = "/register")
    public String register(HttpRequest req) {
        return HttpTemplate.view("register.html");
    }

    @PostMapping(path = "/user")
    public String loginUser(HttpRequest req) {
        String senha = req.body.get("senha").toString();
        return "<h1>Login efetuado</h1><p>Senha recebida: " + senha + "</p>";
    }

    @PostMapping(path = "/register")
    public String createUser(HttpRequest req) {
        return "<h1>Usuário cadastrado com sucesso</h1><a href='/login'>Ir para login</a>";
    }
}