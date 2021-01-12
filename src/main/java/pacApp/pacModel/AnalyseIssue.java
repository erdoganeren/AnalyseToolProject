package pacApp.pacModel;

public class AnalyseIssue {
	String severity; 
	String component; 
	int line;
	String message;
	public AnalyseIssue(String severity, String component, int line, String message) {
		this.severity = severity;
		this.component = component;
		this.line = line;
		this.message = message;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
