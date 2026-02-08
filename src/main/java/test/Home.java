package test;

import Request.HttpRequest;
import jRoute.Controller;
import mapping.GetMapping;
import mapping.PostMapping;


public class Home implements Controller {
	
	@GetMapping(path = "/")
	public String ola(HttpRequest req){
	    return "Ola mundo";
	}
	
	@PostMapping(path = "/user")
	public String olaAbilio(HttpRequest req){
	    return "Ola mundo, from Abílio "+req.body.get("senha");
	}
}