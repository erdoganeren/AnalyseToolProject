package pacApp.pacTest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/testHallo")
	public String sayHallo() {
		return "Hallo du!";
	}
	
	
}
