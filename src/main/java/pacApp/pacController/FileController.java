package pacApp.pacController;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import pacApp.pacComponent.MessageComponent;
import pacApp.pacModel.AnalyseIssue;
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

    @GetMapping("/getAnalyseCsv")
    @ResponseBody
    public String getAnalyseCsv(@RequestParam("csvAnalyseValues") String csvAnalyseValues) {
    	return csvAnalyseValues;
    }
    
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") List<MultipartFile> file, RedirectAttributes redirectAttributes, HttpServletRequest request,	 Model model) {
    	String analyseLink = ""; 
        analyseLink = getHostNameAndPort()+"/project/issues?id=";
    	    
    	String projectKey =  fileService.uploadFile(file); // create tmp Files
    	// start sonarscanner
    	HashMap<Integer, String> messageStatus = main.execute(Paths.get("tmp/" + projectKey).toAbsolutePath().toString(), projectKey);
    	if (messageStatus.get(0) == null ) {   		
    		model.addAttribute("errorMessage", messageStatus.get(1)!= null ? messageStatus.get(1): messageStatus.get(2));
    		return "errorPage";
    	}
    	analyseLink = analyseLink +  projectKey;
    	// add message
    	model.addAttribute("message", "Projekt wurde erfolgreich gescannt!");
    	// Clear the temp directory 
    	FileService.clearTempDir();
    	
    	//sleep for sonarQube report
    	try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
    	
    	// handle metrics
    	handleIssues(projectKey,model);
    	handleMetrics(projectKey, model);
    	
    	//create link for Sonarqube homepage
    	model.addAttribute("analyseLink", analyseLink);
        return "result";
    }
    
   
    
	private boolean handleIssues(String projectKey, Model model) {
		URL url;
		try {
			url = new URL(getHostNameAndPort()+"/api/issues/search?project=TestPoj&componentKeys="+projectKey);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("accept", "application/json");
			con.setRequestMethod("GET");
			
			String auth = "admin:pwd";
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
			String authHeaderValue = "Basic " + new String(encodedAuth);
			con.setRequestProperty("Authorization", authHeaderValue);

			
			InputStream responseStream = con.getInputStream();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> jsonMap = mapper.readValue(responseStream, Map.class);
			JSONObject json = new JSONObject(jsonMap);
			model.addAttribute("numberOfIssues", json.get("total"));
			
			JSONArray ja = (JSONArray)json.get("issues");
			List<AnalyseIssue> aiList = new ArrayList<AnalyseIssue>();
			for(int i=0; i< ja.length(); i++) {
				JSONObject issue = (JSONObject)ja.get(i);
				if(issue.has("line"))
					aiList.add(new AnalyseIssue(issue.getString("severity"), issue.getString("component"),issue.getInt("line"),issue.getString("message"))) ;
				else
					aiList.add(new AnalyseIssue(issue.getString("severity"), issue.getString("component"),0,issue.getString("message"))) ;
			}
			model.addAttribute("analyseIssueList", aiList);
			return  true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean handleMetrics(String projectKey, Model model) {
		URL url;
		try {
			// create url con connection
			url = new URL(getHostNameAndPort() + "/api/measures/component?component="+projectKey+"&metricKeys="+getAnalyseMetricsInStringList());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("accept", "application/json");
			con.setRequestMethod("GET");
			// add auth String 
			String auth = "admin:pwd";
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
			String authHeaderValue = "Basic " + new String(encodedAuth);
			con.setRequestProperty("Authorization", authHeaderValue);

			InputStream responseStream = con.getInputStream();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> jsonMap = mapper.readValue(responseStream, Map.class);
			JSONObject json = new JSONObject(jsonMap);

			JSONObject jo = (JSONObject) json.get("component");
			model.addAttribute("csvAnalyseValues", convertJsonAnalyseValuesToCsv(jo));
			// handle JsonArray and set to view model
			JSONArray jsonMeasures = (JSONArray)jo.get("measures");
			for(int i=0;i < jsonMeasures.length(); i++) {
				JSONObject jmetrics = (JSONObject)jsonMeasures.get(i);				
				model.addAttribute(jmetrics.getString("metric"), jmetrics.getString("value"));				
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String getAnalyseMetricsInStringList() {
		return "ncloc,complexity,violations,blocker_violations,critical_violations,major_violations,"
				+ "minor_violations,info_violations,duplicated_lines,duplicated_files,"
				+ "comment_lines,classes,comment_lines_density,functions,"
				+ "code_smells,vulnerabilities,bugs,cognitive_complexity";
	}
	private String getHostNameAndPort() {
		return "http://" + InetAddress.getLoopbackAddress().getHostName()+ ":9000";
	}
    
	private String convertJsonAnalyseValuesToCsv(JSONObject jo) { 
		JSONArray jsonMeasures = (JSONArray)jo.get("measures");
		final String newLine = System.getProperty("line.separator");
		final String htmlLineBrake = "<br>";
		StringBuilder csvReturnValue = new StringBuilder();
		for(int i=0;i < jsonMeasures.length(); i++) {
			JSONObject jmetrics = (JSONObject)jsonMeasures.get(i);				
			csvReturnValue.append(jmetrics.getString("metric")+";"+jmetrics.getString("value")+";"+ newLine+htmlLineBrake);
		}
		return csvReturnValue.toString();
	}
}