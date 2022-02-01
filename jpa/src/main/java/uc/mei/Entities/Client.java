package uc.mei.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Entity(name = "Client")
public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id;

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet Wallet;

    @OneToMany//(cascade = CascadeType.ALL)
    private List<Ticket> Tickets;

    private String CreationDate;

    private String Name;

    private String Address;

    private String Email;

    private String Password;

    private int ntrips;

    public Client(){

    }


    public Client(String name, String address, String email, String password, uc.mei.Entities.Wallet wallet, List<Ticket> tickets) {
        Name = name;
        Address = address;
        Email = email;
        Wallet = wallet;
        Tickets = tickets;
        CreationDate = LocalDateTime.now().toString();
        Password = password;
        ntrips = 0;
    }

    public UUID getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
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

    public uc.mei.Entities.Wallet getWallet() {
        return Wallet;
    }

    public void setWallet(uc.mei.Entities.Wallet wallet) {
        Wallet = wallet;
    }

    public List<Ticket> getTickets() {
        return Tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        Tickets = tickets;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Client{" +
                "Id=" + Id +
                ", Wallet=" + Wallet +
                ", Tickets=" + Tickets +
                ", CreationDate='" + CreationDate + '\'' +
                ", Name='" + Name + '\'' +
                ", Address='" + Address + '\'' +
                ", Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }

    public int getNtrips() {
        return ntrips;
    }

    public void setNtrips(int ntrips) {
        this.ntrips = ntrips;
    }

    public static Comparator<Client> comparator = new Comparator<Client>() {

        public int compare(Client s1, Client s2) {

            int rollno1 = s1.getNtrips();
            int rollno2 = s2.getNtrips();

            /*For ascending order*/
            return rollno2-rollno1;
        }
    };
}