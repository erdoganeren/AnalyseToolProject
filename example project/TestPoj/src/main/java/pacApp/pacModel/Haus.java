package pacApp.pacModel;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection ="Haus")
public class Haus {
	
    @Id
    private long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "ortid", insertable = true, updatable = true)
	private Ort ort; 
	private String strasse; 
	private int hausnummer;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<Person>personen = new ArrayList<Person>();
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public Ort getOrt() {
		return ort;
	}
	
	public void setOrt(Ort ort) {
		this.ort = ort;
	}
	public String getStrasse() {
		return strasse;
	}
	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}
	public int getHausnummer() {
		return hausnummer;
	}
	public void setHausnummer(int hausnummer) {
		this.hausnummer = hausnummer;
	}
	
	public List<Person> getPersonen() {
		return personen;
	}
	public void setPersonen(List<Person> personen) {
		this.personen = personen;
	}
}
 