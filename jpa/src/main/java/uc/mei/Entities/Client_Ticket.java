package uc.mei.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity(name = "client_ticket")
public class Client_Ticket implements Serializable {
    @Id
    private UUID Client_Id;
    @Id
    private UUID Tickets_Id;

    public Client_Ticket(){

    }

    public UUID getClient_Id() {
        return Client_Id;
    }

    public void setClient_Id(UUID client_Id) {
        Client_Id = client_Id;
    }

    public UUID getTickets_Id() {
        return Tickets_Id;
    }

    public void setTickets_Id(UUID tickets_Id) {
        Tickets_Id = tickets_Id;
    }
}
