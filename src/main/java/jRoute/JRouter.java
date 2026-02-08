package jRoute;

import java.lang.reflect.Method;

import Request.HttpRequest;
import Router.RouteHandler;
import Server.HttpServer;
import mapping.GetMapping;
import mapping.PostMapping;

public class JRouter extends HttpServer {

	private static JRouter jRouter;

	private JRouter() {}
	private JRouter(int port) {
		super(port);
	}

	public static synchronized JRouter getInstance(int port) {
		if (jRouter == null)
			jRouter = new JRouter(port);
		return jRouter;
	}

	public static synchronized JRouter getInstance() {
		if (jRouter == null)
			jRouter = new JRouter();
		return jRouter;
	}

	public void start()throws Exception {
		super.start();
	}

	public void controller(Object controller) {

		Method[] methods = controller.getClass().getDeclaredMethods();

		for (Method method : methods) {

			if (method.isAnnotationPresent(GetMapping.class)) {

				GetMapping mapping = method.getAnnotation(GetMapping.class);
				String path = mapping.path();

				var handler = getHandler(method, controller);
				super.getRouter().get(path, handler);
			}else if(method.isAnnotationPresent(PostMapping.class)){
			    PostMapping mapping = method.getAnnotation(PostMapping.class);
				String path = mapping.path();

				var handler = getHandler(method, controller);
				super.getRouter().post(path, handler);
			}
		}
	}

	public RouteHandler getHandler(Method method, Object controller) {
		method.setAccessible(true);

		return (req) -> {

			// método sem parâmetros
			if (method.getParameterCount() == 0) {
				return method.invoke(controller);
			}

			// método com HttpRequest
			if (method.getParameterTypes()[0] == HttpRequest.class) {
				return method.invoke(controller, req);
			}

			throw new RuntimeException(
				"Assinatura inválida em " + method.getName()
			);
		};
	}
}