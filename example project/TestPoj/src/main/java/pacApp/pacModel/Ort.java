package pacApp.pacModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Ort")
public class Ort {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ortid", updatable = false, nullable = false)
	private long ortid;
	private long plz;
	private String ortsname;
	
	public long getId() {
		return ortid;
	}
	public void setId(long ortid) {
		this.ortid = ortid;
	}
	public long getPlz() {
		return plz;
	}
	public void setPlz(long plz) {
		this.plz = plz;
	}
	public String getOrtsname() {
		return ortsname;
	}
	public void setOrtsname(String ortsname) {
		this.ortsname = ortsname;
	}	
	
}
