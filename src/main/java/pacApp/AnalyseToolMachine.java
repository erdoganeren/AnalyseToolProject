package pacApp;

import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*
 * Start Application
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration	
@SpringBootApplication
public class AnalyseToolMachine extends SpringBootServletInitializer{
	public static void main(String[] args) {
		try {
			SpringApplication.run(AnalyseToolMachine.class, args);	
			//Runtime.getRuntime().exec("cmd /c start \"\" StartQube.bat");			
			String qubePath = Paths.get("sonar").toFile().getAbsolutePath() + Paths.get("/sonarqube-8.5.1.38104/bin/windows-x86-64/StartSonar.bat");
			Runtime.getRuntime().exec("cmd /c start \"\" " + qubePath); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AnalyseToolMachine.class);
    }
}
