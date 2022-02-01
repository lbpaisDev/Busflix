package uc.mei.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity(name = "trip_ticket")
public class Trip_Ticket implements Serializable {
    @Id
    private UUID Trip_Id;

    @Id
    private UUID Tickets_Id;

    public Trip_Ticket(){

    }

    public UUID getTrip_Id() {
        return Trip_Id;
    }

    public void setTrip_Id(UUID trip_Id) {
        Trip_Id = trip_Id;
    }

    public UUID getTickets_Id() {
        return Tickets_Id;
    }

    public void setTickets_Id(UUID tickets_Id) {
        Tickets_Id = tickets_Id;
    }
}
