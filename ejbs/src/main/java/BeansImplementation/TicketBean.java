package BeansImplementation;

import BeansInterfaces.ITicket;
import Utils.ErrorsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uc.mei.Entities.Client;
import uc.mei.Entities.Client_Ticket;
import uc.mei.Entities.Ticket;
import uc.mei.Entities.Trip;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Session Bean implementation class Ticket
 */
@Stateless
@Local
public class TicketBean implements ITicket {
    private final Logger ticket_logger = LoggerFactory.getLogger(Ticket.class);

    @PersistenceContext(unitName = "busflix")
    EntityManager entityManager;

    @Override
    public Ticket addTicket(Integer SeatNumber, Trip Trip) {
        ticket_logger.info("Adding ticket for Trip: "+ Trip + " with seatNumber: "+ SeatNumber);
        try
        {
            Ticket ticket = new Ticket(Trip, SeatNumber);
            entityManager.persist(ticket);

            return ticket;
        }
        catch (Exception e)
        {
            ticket_logger.error("ERROR WHILE CREATING NEW TICKET", e);
            throw e;
        }
    }

    @Override
    public List<Ticket> getAllTickets() {
        ticket_logger.info("Fetching all tickets.");

        try
        {
            return entityManager.createQuery("SELECT t FROM Ticket t", Ticket.class).getResultList();
        }
        catch (Exception e){
            ticket_logger.error("ERROR WHILE GETTING ALL TICKETS.", e);
            throw e;
        }
    }

    @Override
    public Ticket getTicketById(java.util.UUID Id) {
        ticket_logger.info("Fetching Ticket By Ticket Id: " + Id);

        try
        {
            return entityManager.createQuery("SELECT t FROM Ticket t WHERE t.Id = (:id)", Ticket.class).setParameter("id", Id).getSingleResult();
        }
        catch (Exception e)
        {
            ticket_logger.error("ERROR WHILE GETTING TICKET BY ID: " + Id + " \n", e);
            throw e;
        }
    }

    @Override
    public boolean removeTicketById(java.util.UUID Id) {
        ticket_logger.info("Removing Ticket By Ticket Id: " + Id);

        try
        {

            Query ticket = entityManager.createQuery("SELECT t FROM Ticket t WHERE t.Id = (:id)", Ticket.class).setParameter("id", Id);
            ticket.getSingleResult();

            entityManager.remove(ticket);

            return true;
        }
        catch (Exception e)
        {

            ticket_logger.error("ERROR WHILE REMOVING TICKET BY ID: " + Id + "\n", e);
            throw e;
        }
    }

    @Override
    public ErrorsEnum reAssignTicket(Ticket ticket){

        ticket_logger.info("Reassigning Ticket: " + ticket);
        try
        {

            ticket.setClient(null);

            entityManager.merge(ticket);

            ticket_logger.info("REASSIGNED TICKET \n " + ticket);

            return ErrorsEnum.TICKET_UPDATED_SUCCESSFULLY;
        }
        catch (Exception e)
        {
            ticket_logger.error("ERROR WHILE REASSIGNING TICKET: " + ticket, e);
            throw e;
        }
    }

    @Override
    public ErrorsEnum buyTicket(java.util.UUID ClientId, java.util.UUID TicketId){
        try
        {
            Client client = entityManager.find(Client.class, ClientId);
            Ticket ticket = getTicketById(TicketId);

            client.getTickets().add(ticket);

            ticket.setClient_id(ClientId);

            entityManager.persist(ticket);

            client.getWallet().setBalance(client.getWallet().getBalance() - ticket.getTrip().getPrice());
            ticket.getTrip().setCapacity(ticket.getTrip().getCapacity() - 1);


            return ErrorsEnum.TICKET_BOUGHT_SUCCESSFULLY;
        }
        catch(Exception e)
        {
            ticket_logger.error("ERROR WHILE BUYING TICKET: " + TicketId + " FOR CLIENT WITH ID: " + ClientId + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }

    public List<Ticket> getAvailableTripTickets(java.util.UUID tripId){

        System.out.println("\n\n\n Fetching tickets with tripId" + tripId + "\n");

        TypedQuery<Ticket> query = entityManager.createQuery("SELECT tck FROM Ticket tck WHERE tck.Trip = (:id)", Ticket.class);
        query.setParameter("id", entityManager.find(Trip.class, tripId));

        List<Ticket> availableTickets = new ArrayList<>();
        for (Ticket tck:
                query.getResultList()) {

            TypedQuery<Client_Ticket> ctq = entityManager.createQuery("SELECT ct from client_ticket ct where ct.Tickets_Id = (:id)", Client_Ticket.class).setParameter("id", tck.getId());

            if(ctq.getResultList().size() == 0){
                availableTickets.add(tck);
            }

        }

        return availableTickets;
    }
}
