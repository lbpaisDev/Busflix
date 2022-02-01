package BeansInterfaces;

import java.util.List;
import java.util.UUID;

import uc.mei.Entities.Client;
import Utils.ErrorsEnum;
import uc.mei.Entities.Ticket;

public interface IClient {

    ErrorsEnum login(String Email, String Password);
    boolean logout(String Email, String Password);
    ErrorsEnum register(String Name, String Email, String Password, String Address);

    List<Client> getAllClients();
    Client getClientById(java.util.UUID Id);
    Client getClientByEmail(String Email);
    List<Client> getClientsByTripId(java.util.UUID TripId);
    ErrorsEnum removeClientById(java.util.UUID Id);
    ErrorsEnum updateClient(Client client, int param);
    List<java.util.UUID> getClientTickets(UUID clientId);
    void setMockTrips();
    ErrorsEnum makeReimbursement(java.util.UUID ClientId, java.util.UUID TicketId);
}
