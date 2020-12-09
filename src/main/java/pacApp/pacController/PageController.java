package pacApp.pacController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import pacApp.pacService.FileService;

public class PageController {
    @Autowired
    FileService fileService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

   
}