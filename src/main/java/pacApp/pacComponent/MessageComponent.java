package pacApp.pacComponent;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class MessageComponent {

	private String message;
	
	private String analyseLink; 

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAnalyseLink() {
		return analyseLink;
	}

	public void setAnalyseLink(String analyseLink) {
		this.analyseLink = analyseLink;
	} 

}
