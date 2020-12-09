package pacApp.pacHelper;

import java.nio.file.Paths;

public class HelperClass {
	 public static String removeClasspathFromPath(String path) {
	    	return path.substring(0, path.indexOf(".classpath"));
	 } 
	
	public static String getProjectKeyFromPath(String dirPath) {
		 return Paths.get(dirPath).getFileName().toString();
	 }
}
