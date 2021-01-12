package pacApp.pacController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import pacApp.pacService.FileService;
@Controller
public class PageController {
    @Autowired
    FileService fileService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/help")
    public String getHelpPage() {
        return "help";
    }
   
    @GetMapping("/about")
    public String getAboutPage() {
        return "about";
    }
}