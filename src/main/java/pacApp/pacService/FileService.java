package pacApp.pacService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import pacApp.pacComponent.MessageComponent;
import pacApp.pacException.FileStorageException;

@Service
public class FileService {

    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;
    
    public Path tmpPath; 
    
    @Autowired
    MessageComponent messageComp;
    
    public String uploadFile(List<MultipartFile> fileList) {
        try { 
        	Path uploadDirPath = Paths.get("tmp").toAbsolutePath();
        	tmpPath = uploadDirPath;
        	uploadDir = uploadDirPath.toString();
        	for (MultipartFile file : fileList) {
        		Path copyLocation = Paths.get(uploadDirPath + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
        		mkdirForFile(copyLocation);
        		try {
        			Files.copy(file.getInputStream(), copyLocation , StandardCopyOption.REPLACE_EXISTING);
        		}catch( Exception nsfe) {
        			nsfe.printStackTrace();
        		}
        	}
        	
            return  Paths.get(StringUtils.cleanPath(fileList.get(0).getOriginalFilename())).getName(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + fileList.get(0).getOriginalFilename()+ ". Please try again!");
        }
    }
       
    public void mkdirForFile(Path dirPath){
    	try {
	    	String dirPathTmp = dirPath.toString();
	    	dirPathTmp = dirPathTmp.substring(0, dirPathTmp.indexOf(File.separator + dirPath.getFileName()));
	    	File directory = new File(dirPathTmp);
	        if (! directory.exists()){
				Files.createDirectories(directory.toPath());
	        }	
	        messageComp.setMessage(dirPathTmp);
    	} catch (IOException e) {
			e.printStackTrace();
		}    
    }
    public static String getProjectKey() {
    	return null;
    }
    
    public static boolean clearTempDir() {
    	try {
    		Path uploadDirPath = Paths.get("tmp").toAbsolutePath();
			FileUtils.cleanDirectory(uploadDirPath.toFile());			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	return false;
    }
}