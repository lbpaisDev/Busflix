package uc.mei.Entities;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "Trip")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id;

    @ManyToMany(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Ticket> Tickets;

    private String CreationDate;

    private String DepartureTime;

    private String ArrivalTime;

    private String Origin;

    private String Destination;

    private float Price;

    private int Capacity;

    public Trip(){

    }

    public Trip(List<Ticket> tickets, String departureTime, String arrivalTime, String origin, String destination, float price, int capacity) {
        Tickets = tickets;
        CreationDate = LocalDateTime.now().toString();
        DepartureTime = departureTime;
        ArrivalTime = arrivalTime;
        Origin = origin;
        Destination = destination;
        Price = price;
        Capacity = capacity;
    }

    public UUID getId() {
        return Id;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    public String getDepartureTime() {
        return DepartureTime;
    }

    public void setDepartureTime(String departureTime) {
        DepartureTime = departureTime;
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        ArrivalTime = arrivalTime;
    }

    public String getOrigin() {
        return Origin;
    }

    public void setOrigin(String origin) {
        Origin = origin;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public List<Ticket> getTickets() {
        return Tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        Tickets = tickets;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "Id=" + Id +
                ", Tickets=" + Tickets.iterator() +
                ", CreationDate='" + CreationDate + '\'' +
                ", DepartureTime='" + DepartureTime + '\'' +
                ", ArrivalTime='" + ArrivalTime + '\'' +
                ", Origin='" + Origin + '\'' +
                ", Destination='" + Destination + '\'' +
                ", Price=" + Price +
                ", Capacity=" + Capacity +
                '}';
    }
}