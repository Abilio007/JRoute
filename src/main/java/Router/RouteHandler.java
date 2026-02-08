package Router;

import Request.HttpRequest;

@FunctionalInterface
public interface RouteHandler {
    Object handle(HttpRequest req) throws Exception;
}