package Utils;

import BeansImplementation.TicketBean;
import BeansImplementation.WalletBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uc.mei.Entities.*;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataTransactionsManager {

    @PersistenceContext(unitName = "busflix")
    EntityManager entityManager;

    private final Logger client_logger = LoggerFactory.getLogger(Client.class);
    private final Logger ticket_logger = LoggerFactory.getLogger(Ticket.class);
    private final Logger trip_logger = LoggerFactory.getLogger(Trip.class);
    private final Logger wallet_logger = LoggerFactory.getLogger(Wallet.class);


    /* Client Features */
    public ErrorsEnum Login(String Email, String Password){
        try
        {
            Client client = GetClientByEmail(Email);

            if(client == null)
            {
                return ErrorsEnum.CLIENT_NOT_FOUND;
            }
            else
            {
                try
                {
                    if(client.getPassword().equals(MD5.Encrypt(Password)))
                    {
                        return ErrorsEnum.LOGIN_SUCCESSFUL;
                    }
                    else
                    {   // INCORRECT PASSWORD
                        return ErrorsEnum.LOGIN_WRONG_CREDENTIALS;
                    }
                }
                catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return ErrorsEnum.ERROR_DATABASE;
                }
            }
        }
        catch (Exception e)
        {
            client_logger.error("ERROR WHILE LOGGING IN WITH THE FOLLOWING EMAIL: " + Email + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }
    public Client GetClientByEmail(String Email) {

        try
        {
            TypedQuery<Client> client = entityManager.createQuery("SELECT c FROM Client c WHERE c.Email = (:email)", Client.class).setParameter("email", Email);
            return client.getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
        finally
        {
            entityManager.close();
        }
    }
    public ErrorsEnum Register(String Name, String Email, String Password, String Address){

        try
        {
            Client emailClient = GetClientByEmail(Email);
            if(emailClient != null)
            {
                return ErrorsEnum.CLIENT_ALREADY_EXISTS;
            }

            Wallet wt = new Wallet(0f);
            Client client = new Client(Name, Address, Email, MD5.Encrypt(Password), wt, new ArrayList<Ticket>());
            client.setWallet(wt);

            entityManager.persist(client);

            return ErrorsEnum.CLIENT_ADDED_SUCCESSFULLY;
        }
        catch (Exception e){
            client_logger.error("ERROR WHILE REGISTERING CLIENT WITH EMAIL: " + Email + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }
    public List<Client> GetAllClients(){

        try
        {
            TypedQuery<Client> clients = entityManager.createQuery("FROM Client c", Client.class);

            return clients.getResultList();
        }
        catch(NoResultException e){
            client_logger.error("Error while fetching every client in getAllClients()", e);
            throw e;
        }
    }
    public Client GetClientById(java.util.UUID ClientId) {

        try
        {
            TypedQuery<Client> clients = entityManager.createQuery("SELECT c FROM Client c WHERE c.Id = (:id) ", Client.class).setParameter("id", ClientId);
            return clients.getSingleResult();
        }
        catch(NoResultException e)
        {
            client_logger.error("NOT RESULTS FOUND FOR THAT CLIENT ID: " + ClientId + " \n", e);
            throw e;
        }
    }

    public ErrorsEnum RemoveClientById(java.util.UUID Id){
        try {
            Client client = GetClientById(Id);

            while (client.getTickets().iterator().hasNext()){
                Ticket ticket = client.getTickets().iterator().next();

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
            new WalletBean().removeWallet(client.getWallet().getId());

            //FINALLY, IT REMOVES FROM DATABASE
            entityManager.remove(client);

            return ErrorsEnum.CLIENT_REMOVED_SUCCESSFULLY;
        }
        catch (Exception e){
            client_logger.error("ERROR WHILE REMOVING CLIENT BY ID: " + Id + " \n", e);
            return ErrorsEnum.CLIENT_NOT_REMOVED;
        }
    }
    public ErrorsEnum UpdateClient(Client client) {

        try
        {
            Client queryClient = GetClientById(client.getId());
            queryClient.setWallet(client.getWallet());
            queryClient.setAddress(client.getAddress());
            queryClient.setEmail(client.getEmail());
            queryClient.setName(client.getName());
            queryClient.setTickets(client.getTickets());
            queryClient.setPassword(MD5.Encrypt(client.getPassword()));

            entityManager.merge(queryClient);

            return ErrorsEnum.CLIENT_UPDATED_SUCCESSFULLY;
        }
        catch (Exception e)
        {
            client_logger.error("ERROR WHILE UPDATING CLIENT: " + client.toString() + " \n", e);
            return ErrorsEnum.CLIENT_NOT_UPDATED;
        }
    }

    public List<Client> GetClientsByTripId(java.util.UUID TripId) {
        try
        {

            TypedQuery<Client> query = entityManager.createQuery("SELECT distinct c FROM Client c\n" +
                    "INNER JOIN client_ticket c_t ON c_t.Client_Id = c.Id\n" +
                    "INNER JOIN trip_ticket t_t ON t_t.Tickets_Id = c_t.Tickets_Id\n" +
                    "WHERE t_t.Trip_Id = (:id)", Client.class).setParameter("id", TripId);

            return query.getResultList();
        }
        catch(Exception e){
            client_logger.error("ERROR WHILE GETTING CLIENTS BY TRIP ID: " + TripId, e);
            throw e;
        }

    }

    /* Ticket Features */
    public Ticket AddTicket(Integer SeatNumber, Trip Trip) {
        ticket_logger.info("Adding ticket for Trip: "+ Trip + " with seatNumber: "+ SeatNumber);


        try{
            Ticket ticket = new Ticket(Trip, SeatNumber);

            entityManager.persist(ticket);


            return ticket;
        }
        catch (Exception e){
            ticket_logger.error("ERROR WHILE CREATING NEW TICKET", e);
            throw e;
        }
    }
    public List<Ticket> GetAllTickets() {

        ticket_logger.info("Fetching all tickets.");

        try
        {

            TypedQuery<Ticket> tickets = entityManager.createQuery("SELECT t FROM Ticket t", Ticket.class);
            return tickets.getResultList();
        }
        catch (Exception e){
            ticket_logger.error("ERROR WHILE GETTING ALL TICKETS.", e);
            throw e;
        }

    }
    public Ticket GetTicketById(java.util.UUID Id) {

        ticket_logger.info("Fetching Ticket By Ticket Id: " + Id);

        try
        {
            TypedQuery<Ticket> query = entityManager.createQuery("SELECT t FROM Ticket t WHERE t.Id = (:id)", Ticket.class).setParameter("id", Id);
            return query.getSingleResult();
        }
        catch (Exception e){
            ticket_logger.error("ERROR WHILE GETTING TICKET BY ID: " + Id + " \n", e);
            throw e;
        }
    }
    public boolean RemoveTicketById(java.util.UUID Id) {
        EntityTransaction et = entityManager.getTransaction();
        ticket_logger.info("Removing Ticket By Ticket Id: " + Id);

        try
        {
            Query ticket = entityManager.createQuery("SELECT t FROM Ticket t WHERE t.Id = (:id)", Ticket.class).setParameter("id", Id);
            ticket.getSingleResult();

            entityManager.remove(ticket);


            return true;
        }
        catch (Exception e){
            ticket_logger.error("ERROR WHILE REMOVING TICKET BY ID: " + Id + "\n", e);
            throw e;
        }
    }
    public ErrorsEnum ReAssignTicket(Ticket ticket){
        ticket_logger.info("Reassigning Ticket: " + ticket);

        try
        {
            ticket.setClient(null);

            entityManager.merge(ticket);

            ticket_logger.info("REASSIGNED TICKET \n " + ticket);

            return ErrorsEnum.TICKET_UPDATED_SUCCESSFULLY;
        }
        catch (Exception e){
            ticket_logger.error("ERROR WHILE REASSIGNING TICKET: " + ticket, e);
            throw e;
        }

    }
    public ErrorsEnum BuyTicket(java.util.UUID ClientId, java.util.UUID TicketId){
        try
        {
            Client client = GetClientById(ClientId);
            Ticket ticket = GetTicketById(TicketId);

            client.getTickets().add(ticket);

            client.getWallet().setBalance(client.getWallet().getBalance() - ticket.getTrip().getPrice());
            ticket.getTrip().setCapacity(ticket.getTrip().getCapacity() - 1);

            return ErrorsEnum.TICKET_BOUGHT_SUCCESSFULLY;
        }
        catch(Exception e){
            ticket_logger.error("ERROR WHILE BUYING TICKET: " + TicketId + " FOR CLIENT WITH ID: " + ClientId + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }

    /* Trip Features */
    public List<Trip> GetAllTrips() {
        trip_logger.info("Fetching all trips");

        try
        {
            TypedQuery<Trip> query = entityManager.createQuery("SELECT tr FROM Trip tr", Trip.class);
            return query.getResultList();
        }
        catch (Exception e)
        {
            trip_logger.error("ERROR WHILE GETTING ALL TRIPS.\n", e);
            return null;
        }
    }
    public List<Trip> GetTripsByClientId(java.util.UUID ClientId){
        try
        {
            // GETS THE CLIENT ITSElF
            Client client = GetClientById(ClientId);

            // IN CASE HE HAS TICKETS FROM TRIPS
            if(client.getTickets().iterator().hasNext()){
                List<Trip> trips = new ArrayList<>();
                client.getTickets().forEach(x -> trips.add(x.getTrip()));
                return trips;
            }
        }
        catch (Exception e){
            trip_logger.error("ERROR WHILE GETTING TRIPS OF CLIENT: " + ClientId + " \n", e);
            return null;
        }
        return new ArrayList<Trip>();
    }
    public Trip GetTripById(java.util.UUID Id) {
        trip_logger.info("Fetching Trip By Trip Id: " + Id);
        try {
                     TypedQuery<Trip> query = entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.id = :id ", Trip.class).setParameter("id", Id);
            return query.getSingleResult();
        } catch (Exception e) {
            trip_logger.error("ERROR WHILE GETTING TRIP BY ID: " + Id + " \n", e);
            throw e;
        }
    }
    public List<Trip> GetTripsByDestination(String Destination) {
        trip_logger.info("Fetching all Trips By Destination: " + Destination);

        try
        {
            TypedQuery<Trip> query = entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.Destination LIKE concat('%', (:destination), '%')", Trip.class).setParameter("destination", Destination);
            return query.getResultList();
        }
        catch(NoResultException e)
        {
            trip_logger.error("No Results found for that destination: " + Destination, e);
            throw e;
        }
    }
    public List<Trip> GetTripsByOrigin(String Origin) {
        trip_logger.info("Fetching all Trips By Origin: " + Origin);
        try
        {
            TypedQuery<Trip> query = entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.Origin LIKE concat('%', (:origin), '%')", Trip.class).setParameter("origin", Origin);
            return query.getResultList();
        }
        catch(NoResultException e)
        {
            trip_logger.error("No Results Found for Origin: " + Origin);
            throw e;
        }

    }
    public List<Trip> GetTripsByDepartureTime(String DepartureTime) {
        trip_logger.info("Getting all Trips by Departure Time: " + DepartureTime);
        try
        {
            TypedQuery<Trip> query = entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.DepartureTime LIKE concat('%',:departureTime,'%')", Trip.class).setParameter("departureTime", DepartureTime);
            return query.getResultList();
        }
        catch(NoResultException e){
            trip_logger.error("No Results Founds for Departure Time: " + DepartureTime, e);
            throw e;
        }

    }
    public List<Trip> GetTripsByArrivalTime(String ArrivalTime) {
        trip_logger.info("Getting all Trips by Arrival Time: " + ArrivalTime);
        try
        {
            TypedQuery<Trip> query = entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.ArrivalTime LIKE concat('%', (:arrivalTime), '%')", Trip.class).setParameter("arrivalTime", ArrivalTime);
            return query.getResultList();
        }
        catch(NoResultException e){
            trip_logger.error("No Results Found: " + ArrivalTime, e);
            throw e;
        }
    }
    public ErrorsEnum RemoveTrip(java.util.UUID TripId) {
        trip_logger.info("Removing Trip with Id: " + TripId);

        try
        {
            List<Ticket> tickets;
            try
            {
                // GETS ALL THE TICKETS RELATED TO A GIVEN TRIP
                tickets = entityManager.createQuery("SELECT * FROM ticket t " +
                                "INNER JOIN trip_ticket t_t ON t_t.Tickets_id = t.Id WHERE t_t.trip_id = (:id)", Ticket.class).
                        setParameter("id", TripId).getResultList();
            }
            catch (Exception e){
                trip_logger.error("ERROR WHILE GETTING TICKETS BY TRIP: " + TripId);
                throw e;
            }

            // FOR THIS TRIP, VERIFIES IF THERE ARE TICKETS FOR FUTURE TRIPS
            while(tickets.iterator().hasNext()){
                tickets.forEach(x -> {
                    // IF THERE ARE, UPDATES CLIENT'S BALANCE
                    if(LocalDateTime.parse(x.getTrip().getDepartureTime()).isAfter(LocalDateTime.now())){
                        x.getClient().getWallet().setBalance(x.getClient().getWallet().getBalance() + x.getTrip().getPrice());
                    }

                    // REMOVES THE TICKET ITSELF FROM CLIENT'S TICKETS LIST
                    x.getClient().getTickets().remove(x);

                    // REMOVES FROM THE TICKET CLIENT'S REFERENCE
                    x.setClient(null);
                });
            }

            try
            {
                Trip trip = GetTripById(TripId);
                entityManager.remove(trip);

                return ErrorsEnum.TRIP_REMOVED_SUCCESSFULLY;
            }
            catch(NoResultException e){
                trip_logger.error("No Trip Found: " + TripId, e);
                throw e;
            }
        }
        catch (Exception e)
        {
            trip_logger.error("ERROR WHILE REMOVING TRIPS BY ID: " + TripId + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }
    public ErrorsEnum UpdateTrip(Trip trip) {
        trip_logger.info("Updating Trip: " + trip.toString());

        try
        {
            try
            {
                Trip queryTrip = GetTripById(trip.getId());

                queryTrip.setArrivalTime(trip.getArrivalTime());
                queryTrip.setDepartureTime(trip.getDepartureTime());
                queryTrip.setCapacity(trip.getCapacity());
                queryTrip.setDestination(trip.getDestination());
                queryTrip.setOrigin(trip.getOrigin());
                queryTrip.setPrice(trip.getPrice());
                queryTrip.setTickets(trip.getTickets());

                entityManager.merge(queryTrip);

            }
            catch(NoResultException e){
                trip_logger.error("ERROR WHILE GETTING TRIP: " + trip.getId());
                throw e;
            }
            return ErrorsEnum.TRIP_UPDATED_SUCCESSFULLY;
        }
        catch (Exception e)
        {
            trip_logger.error("ERROR WHILE UPDATING TRIP: " + trip + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }

    /* Wallet Features */
    public List<Wallet> GetAllWallets() {
        wallet_logger.info("Fetching all wallets");

        try
        {
            TypedQuery<Wallet> wallet = entityManager.createQuery("SELECT w FROM Wallet w", Wallet.class);
            return wallet.getResultList();
        }
        catch (Exception e){
            wallet_logger.error("ERROR FETCHING ALL WALLETS.\n", e);
            throw e;
        }

    }
    public Wallet GetWalletByClientId(java.util.UUID ClientId) {
        wallet_logger.info("Fetching Wallet By Client Id: " + ClientId);
        EntityTransaction et = entityManager.getTransaction();

        Client client = GetClientById(ClientId);

        try {

            TypedQuery<Wallet> wallet = entityManager.createQuery("SELECT w FROM Wallet w WHERE w.Id = (:id)", Wallet.class).setParameter("id", client.getWallet().getId());

            return wallet.getSingleResult();
        }
        catch (NoResultException e) {
            wallet_logger.error("ERROR WHILE GETTING WALLET FOR CLIENT: " + ClientId + " \n", e);
            throw e;
        }
    }
    public ErrorsEnum updateWalletByClientId(java.util.UUID ClientId, Float Balance) {
        wallet_logger.info("Updating Wallet of Client Id: " + ClientId + " with: " + Balance);

        try
        {

            Client client = GetClientById(ClientId);
            Wallet wallet = GetWalletByClientId(client.getWallet().getId());

            wallet.setBalance(Balance);
            entityManager.merge(wallet);

            return ErrorsEnum.WALLET_UPDATED_SUCCESSFULLY;
        }
        catch(NoResultException e)
        {
            wallet_logger.error("ERROR WHILE UPDATING WALLET FROM CLIENT: " + ClientId, e);
            throw e;
        }
    }

    public ErrorsEnum MakeReimbursement(java.util.UUID ClientId, java.util.UUID TicketId){
        wallet_logger.info("Making Reimbursement for Client Id: " + ClientId + " of ticket Id: " + TicketId);
        try
        {

            Client client = GetClientById(ClientId);
            if(client != null)
            {
                Ticket ticket = GetTicketById(TicketId);

                if (ticket != null) {
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
            wallet_logger.error("ERROR WHILE MAKING REIMBURSEMENT FOR CLIENT: " + ClientId + " of ticket Id: " + TicketId, e);
            throw e;
        }
    }
    public void RemoveWallet(java.util.UUID Id){
        wallet_logger.info("Removing Wallet by Wallet Id: " + Id);

        try
        {

            TypedQuery<Wallet> wallet = entityManager.createQuery("SELECT w FROM Wallet w WHERE w.Id = (:id)", Wallet.class).setParameter("id", Id);

            entityManager.remove(wallet.getSingleResult());

        }
        catch (NoResultException e){
            wallet_logger.error("ERROR WHILE FETCHING WALLET: " + Id, e);
            throw e;
        }
    }

}

