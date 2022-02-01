package uc.mei.Entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private UUID Id;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Trip Trip;

    @ManyToOne(cascade = CascadeType.ALL)
    private Client Client;

    private String CreationDate;

    private int SeatNumber;

    public Ticket(){

    }

    public Ticket(Trip trip, int seatNumber) {
        Trip = trip;
        CreationDate = LocalDateTime.now().toString();
        SeatNumber = seatNumber;
    }

    public UUID getId() {
        return Id;
    }

    public uc.mei.Entities.Trip getTrip() {
        return Trip;
    }

    public void setTrip(uc.mei.Entities.Trip trip) {
        Trip = trip;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    public int getSeatNumber() {
        return SeatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        SeatNumber = seatNumber;
    }

    public Client getClient() {
        return Client;
    }

    public void setClient(Client client) {
        Client = client;
    }

    public void setClient_id(java.util.UUID clientId){clientId = clientId;}

    @Override
    public String toString() {
        return "Ticket{" +
                "Id=" + Id +
                ", Trip=" + Trip +
                ", Client=" + Client +
                ", CreationDate='" + CreationDate + '\'' +
                ", SeatNumber=" + SeatNumber +
                '}';
    }
}