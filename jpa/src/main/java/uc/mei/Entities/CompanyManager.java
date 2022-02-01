package uc.mei.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class CompanyManager implements Serializable {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private UUID Id;

    private String Name;

    private String Email;

    private String Password;

    public CompanyManager(){

    }

    public CompanyManager(String name, String email, String password) {
        Name = name;
        Email = email;
        Password = password;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
