
import jRoute.JRouter;
import test.Home;

public class Main {
		
	public static void main(String[] args) throws Exception{
		var server = JRouter.getInstance();
		server.controller(new Home());
		server.start();
	}
}