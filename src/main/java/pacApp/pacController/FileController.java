package pacApp.pacController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pacApp.pacComponent.MessageComponent;
import pacApp.pacService.FileService;
import pacApp.pacSonarScanner.Main;

@Controller
public class FileController {

    @Autowired
    FileService fileService;
    
    @Autowired
    Main main;
    
    @Autowired
    MessageComponent messageComp;

    @GetMapping("/upload")
    public String index() {
        return "upload";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") List<MultipartFile> file, RedirectAttributes redirectAttributes, HttpServletRequest request,	 Model model) {
    	request.getLocalAddr();
    	String analyseLink = ""; 
        analyseLink = "http://" + InetAddress.getLoopbackAddress().getHostName()+":8181/project/issues?id=";
    	    
    	String projectKey =  fileService.uploadFile(file); // create tmp Files
    	//String projectKey = HelperClass.getProjectKeyFromPath(dirPath); 
    	//main.execute(Paths.get("tmp/" + projectKey).toAbsolutePath().toString(), projectKey);
    	analyseLink = analyseLink +  projectKey;
    	model.addAttribute("message", "Projekt wurde erfolgreich gescannt!");
    	
    	
    	model.addAttribute("analyseLink", analyseLink);
        return "index";
    }
      
}