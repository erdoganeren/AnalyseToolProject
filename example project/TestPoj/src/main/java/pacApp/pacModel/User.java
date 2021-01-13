package pacApp.pacModel;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "User")
public class User implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", updatable = false, nullable = false)
    private long id;
    private String email;
    private String password;

    protected User(){}

    public User(String email){
        this.email = email;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(long id, String email){
        this.id = id;
        this.email = email;
    }

    public User(long id, String email, String password){
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return this.id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String name) {
        this.email = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password);
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, email='%s', password='%s']",id , email, password);
    }

    //Cloneable

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
