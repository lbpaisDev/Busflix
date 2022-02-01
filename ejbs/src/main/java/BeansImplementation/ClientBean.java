package BeansImplementation;


import BeansInterfaces.IClient;
import Utils.ErrorsEnum;
import Utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uc.mei.Entities.*;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Session Bean implementation class Client
 */

@Stateless
@Local
public class ClientBean implements IClient {
    private final Logger client_logger = LoggerFactory.getLogger(Client.class);

    @PersistenceContext(unitName = "busflix")
    EntityManager entityManager;

    Random random = new Random();

    @Override
    public ErrorsEnum register(String Name, String Email, String Password, String Address) {

        try {
            Client emailClient = getClientByEmail(Email);
            if (emailClient != null) {
                return ErrorsEnum.CLIENT_ALREADY_EXISTS;
            }

            Wallet wt = new Wallet(0f);
            Client client = new Client(Name, Address, Email, MD5.Encrypt(Password), wt, new ArrayList<Ticket>());
            client.setWallet(wt);

            entityManager.persist(client);
            return ErrorsEnum.CLIENT_ADDED_SUCCESSFULLY;
        } catch (Exception e) {

            client_logger.error("ERROR WHILE REGISTERING CLIENT WITH EMAIL: " + Email + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }

    @Override
    public ErrorsEnum login(String Email, String Password) {
        try {
            Client client = getClientByEmail(Email);

            if (client == null) {
                return ErrorsEnum.CLIENT_NOT_FOUND;
            } else {
                try {
                    if (client.getPassword().equals(MD5.Encrypt(Password))) {
                        return ErrorsEnum.LOGIN_SUCCESSFUL;
                    } else {   // INCORRECT PASSWORD
                        return ErrorsEnum.LOGIN_WRONG_CREDENTIALS;
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return ErrorsEnum.ERROR_DATABASE;
                }
            }
        } catch (Exception e) {
            client_logger.error("ERROR WHILE LOGGING IN WITH THE FOLLOWING EMAIL: " + Email + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }

    @Override
    public boolean logout(String Email, String Password) {
        return true;
    }

    @Override
    public List<Client> getAllClients() {
        try {
            return entityManager.createQuery("FROM Client c", Client.class).getResultList();
        } catch (NoResultException e) {
            client_logger.error("Error while fetching every client in getAllClients()", e);
            throw e;
        }
    }

    @Override
    public Client getClientById(java.util.UUID Id) {
        try {
            return entityManager.createQuery("SELECT c FROM Client c WHERE c.Id = (:id) ", Client.class).
                    setParameter("id", Id).
                    getSingleResult();
        } catch (NoResultException e) {
            client_logger.error("NOT RESULTS FOUND FOR THAT CLIENT ID: " + Id + " \n", e);
            throw e;
        }
    }

    @Override
    public Client getClientByEmail(String Email) {
        try {
            return entityManager.createQuery("SELECT c FROM Client c WHERE c.Email = (:email)", Client.class).
                    setParameter("email", Email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public ErrorsEnum removeClientById(java.util.UUID Id) {
        client_logger.info("Removing Client with Id: " + Id);

        try
        {
            Client client = getClientById(Id);

            for (Ticket ticket : client.getTickets())
            {
                // IN CASE, THE CLIENT OWNS TICKETS TO FUTURE TRIPS
                if (LocalDateTime.parse(ticket.getTrip().getDepartureTime()).isAfter(LocalDateTime.now())) {
                    TicketBean ticketBean = new TicketBean();

                    // IT REMOVES THAT TICKET FROM HIS LIST (TO NOT GET REMOVED PERMANENTLY FROM DATABASE)
                    client.getTickets().remove(ticket);

                    // REASSIGNS TICKET TO BE AVAILABLE TO SELL
                    ticketBean.reAssignTicket(ticket);

                    // READJUSTS TRIP CAPACITY
                    ticket.getTrip().setCapacity(ticket.getTrip().getCapacity() - 1);
                }
            }

            //FINALLY, IT REMOVES FROM DATABASE
            entityManager.remove(client);

            return ErrorsEnum.CLIENT_REMOVED_SUCCESSFULLY;
        }
        catch (Exception e)
        {
            client_logger.error("ERROR WHILE REMOVING CLIENT BY ID: " + Id + " \n", e);
            return ErrorsEnum.CLIENT_NOT_REMOVED;
        }
    }

    @Override
    public ErrorsEnum updateClient(Client client, int param) {
        try {

            Client queryClient = getClientById(client.getId());

            switch (param){
                case 1:
                    queryClient.setWallet(client.getWallet());
                    break;
                case 2:
                    queryClient.setAddress(client.getAddress());
                    break;
                case 3:
                    queryClient.setEmail(client.getEmail());
                    break;
                case 4:
                    queryClient.setName(client.getName());
                    break;
                case 5:
                    queryClient.setPassword(MD5.Encrypt(client.getPassword()));
                    break;
            }

            entityManager.merge(queryClient);

            return ErrorsEnum.CLIENT_UPDATED_SUCCESSFULLY;
        } catch (Exception e) {
            client_logger.error("ERROR WHILE UPDATING CLIENT: " + client.toString() + " \n", e);
            return ErrorsEnum.CLIENT_NOT_UPDATED;
        }
    }

    @Override
    public List<Client> getClientsByTripId(java.util.UUID TripId) {
        try {

            TypedQuery<Client> query = entityManager.createQuery("SELECT distinct c FROM Client c\n" +
                    "INNER JOIN client_ticket c_t ON c_t.Client_Id = c.Id\n" +
                    "INNER JOIN trip_ticket t_t ON t_t.Tickets_Id = c_t.Tickets_Id\n" +
                    "WHERE t_t.Trip_Id = (:id)", Client.class).setParameter("id", TripId);

            return query.getResultList();
        } catch (Exception e) {
            client_logger.error("ERROR WHILE GETTING CLIENTS BY TRIP ID: " + TripId, e);
            throw e;
        }
    }

    @Override
    public List<java.util.UUID> getClientTickets(UUID clientId){

        TypedQuery<Client_Ticket> query = entityManager.createQuery("SELECT ctck FROM client_ticket ctck WHERE ctck.Client_Id = (:id)", Client_Ticket.class).setParameter("id", clientId);

        List<java.util.UUID> tIds = new ArrayList<>();

        for (Client_Ticket ct:
             query.getResultList()) {
            tIds.add(ct.getTickets_Id());
        }

        return  tIds;
    }

    @Override
    public void setMockTrips() {




        Trip t1 = new Trip(new ArrayList<Ticket>(), LocalDateTime.now().toString(), LocalDateTime.now().plusHours(1).toString(), "Coimbra", "Carregal do Sal" , 4.30f, 1);
        Trip t2 =new Trip(new ArrayList<Ticket>(), LocalDateTime.now().plusMonths(1).toString(), LocalDateTime.now().plusMonths(1).plusHours(1).toString(), "Figueira da Foz", "Santarém" , 4.30f, 2);
        Trip t3 = new Trip(new ArrayList<Ticket>(), LocalDateTime.now().plusMonths(2).toString(), LocalDateTime.now().plusMonths(1).plusHours(2).toString(), "Santa Comba", "Poiares" , 4.30f, 3);
        Trip t4 = new Trip(new ArrayList<Ticket>(), LocalDateTime.now().plusMonths(3).toString(), LocalDateTime.now().plusMonths(1).plusHours(1).toString(), "Vila Nova", "Ilha das Cores" , 4.30f, 4);

        Ticket t1_1 = new Ticket(t1, 1);
        Ticket t2_1 = new Ticket(t2, 1);
        Ticket t2_2 = new Ticket(t2, 2);
        Ticket t3_1 = new Ticket(t3, 1);
        Ticket t3_2 = new Ticket(t3, 2);
        Ticket t3_3 = new Ticket(t3, 3);
        Ticket t4_1 = new Ticket(t4, 1);
        Ticket t4_2 = new Ticket(t4, 2);
        Ticket t4_3 = new Ticket(t4, 3);
        Ticket t4_4 = new Ticket(t4, 4);

        entityManager.persist(t1_1);
        entityManager.persist(t2_1);
        entityManager.persist(t2_2);
        entityManager.persist(t3_1);
        entityManager.persist(t3_2);
        entityManager.persist(t3_2);
        entityManager.persist(t4_1);
        entityManager.persist(t4_2);
        entityManager.persist(t4_3);
        entityManager.persist(t4_4);

        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.persist(t3);
        entityManager.persist(t4);

    }

    @Override
    public ErrorsEnum makeReimbursement(java.util.UUID ClientId, java.util.UUID TicketId){
        client_logger.info("Making Reimbursement for Client Id: " + ClientId + " of ticket Id: " + TicketId);
        try
        {
            client_logger.info("REFUND 1");
            Client client = getClientById(ClientId);
            if(client != null)
            {
                client_logger.info("REFUND 2");
                Ticket ticket = entityManager.find(Ticket.class, TicketId);

                if (ticket != null) {
                    client_logger.info("REFUND 3");
                    // HERE WE HAVE TO UPDATE CLIENT'S BALANCE, ADDING THE TICKET'S PRICE TO CLIENT'S CURRENT BALANCE

                    client.getWallet().setBalance(client.getWallet().getBalance() + ticket.getTrip().getPrice());

                    // BECAUSE IT'S A REIMBURSEMENT WE HAVE REMOVE THE TICKET ITSELF FROM THE CLIENTS'S LIST OF TICKETS
                    client.getTickets().remove(ticket);

                    // AND BECAUSE IT'S A REIMBURSEMENT, HE HAVE TO PUT AVAILABLE A THIS SEAT, BECAUSE NOW IT'S AVAILABLE TO SELL
                    ticket.getTrip().setCapacity(ticket.getTrip().getCapacity() - 1);

                    entityManager.merge(ticket);
                    entityManager.merge(ticket.getTrip());
                    entityManager.merge(client);

                    return ErrorsEnum.WALLET_REFUNDED_SUCCESSFULLY;
                }
                else
                {
                    return ErrorsEnum.TICKET_NOT_FOUND;
                }
            }
            else
            {
                return ErrorsEnum.CLIENT_NOT_FOUND;
            }
        }
        catch(Exception e){
            client_logger.error("ERROR WHILE MAKING REIMBURSEMENT FOR CLIENT: " + ClientId + " of ticket Id: " + TicketId, e);
            throw e;
        }
    }
}
