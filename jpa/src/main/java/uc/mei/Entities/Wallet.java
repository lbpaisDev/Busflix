package uc.mei.Entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id;

    private String CreationDate;

    private float Balance;

    public Wallet(){

    }

    public Wallet(float balance) {
        Balance = balance;
        CreationDate = LocalDateTime.now().toString();

    }

    public UUID getId() {
        return Id;
    }

    public float getBalance() {
        return Balance;
    }

    public void setBalance(float balance) {
        Balance = balance;
    }

    public String getCreationDate(){ return CreationDate;}

    @Override
    public String toString() {
        return "Wallet{" +
                "Id=" + Id +
                ", CreationDate='" + CreationDate + '\'' +
                ", Balance=" + Balance +
                '}';
    }
}