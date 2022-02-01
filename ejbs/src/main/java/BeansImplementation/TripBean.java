package BeansImplementation;

import BeansInterfaces.ITrip;
import Utils.ErrorsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uc.mei.Entities.*;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.management.DescriptorAccess;
import javax.management.Query;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.beans.Expression;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Session Bean implementation class Trip
 */

@Stateless
@Local
public class TripBean implements ITrip {
    private final Logger trip_logger = LoggerFactory.getLogger(Ticket.class);

    @PersistenceContext(unitName = "busflix")
    EntityManager entityManager;

    @Resource(mappedName = "java:jboss/mail/Default")
    private Session mailSession;

    @Override
    @Transactional
    public List<Trip> getAllTrips() {
        trip_logger.info("Fetching all trips");
        try {
            return entityManager.createQuery("SELECT tr FROM Trip tr", Trip.class).getResultList();
        } catch (Exception e) {
            trip_logger.error("ERROR WHILE GETTING ALL TRIPS.\n", e);
            return null;
        }
    }

    @Override
    public List<Trip> getTripsByClientId(java.util.UUID ClientId) {
        try {
            // GETS THE CLIENT ITSElF
            Client client = entityManager.find(Client.class, ClientId);

            // IN CASE HE HAS TICKETS FROM TRIPS
            if (client.getTickets().iterator().hasNext()) {
                List<Trip> trips = new ArrayList<>();
                client.getTickets().forEach(x -> trips.add(x.getTrip()));
                return trips;
            }
        } catch (Exception e) {
            trip_logger.error("ERROR WHILE GETTING TRIPS OF CLIENT: " + ClientId + " \n", e);
            return null;
        }
        return null;
    }

    @Override
    public Trip getTripById(UUID Id) {
        trip_logger.info("Fetching Trip By Trip Id: " + Id);

        try {
            return entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.id = :id ", Trip.class).setParameter("id", Id).getSingleResult();
        } catch (Exception e) {
            trip_logger.error("ERROR WHILE GETTING TRIP BY ID: " + Id + " \n", e);
            throw e;
        }

    }

    @Override
    public List<Trip> getTripsByDestination(String Destination) {
        trip_logger.info("Fetching all Trips By Destination: " + Destination);

        try {
            return entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.Destination LIKE concat('%', (:destination), '%')", Trip.class).
                    setParameter("destination", Destination).getResultList();
        } catch (NoResultException e) {
            trip_logger.error("No Results found for that destination: " + Destination, e);
            throw e;
        }
    }

    @Override
    public List<Trip> getTripsByOrigin(String Origin) {
        trip_logger.info("Fetching all Trips By Origin: " + Origin);
        try {
            return entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.Origin LIKE concat('%', (:origin), '%')", Trip.class).
                    setParameter("origin", Origin).getResultList();
        } catch (NoResultException e) {
            trip_logger.error("No Results Found for Origin: " + Origin);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Trip> getTripsByDepartureTime(String DepartureTime) {
        trip_logger.info("Getting all Trips by Departure Time: " + DepartureTime);
        try {
            return entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.DepartureTime LIKE concat('%',:departureTime,'%')", Trip.class).
                    setParameter("departureTime", DepartureTime).getResultList();
        } catch (NoResultException e) {
            trip_logger.error("No Results Founds for Departure Time: " + DepartureTime, e);
            throw e;
        }

    }

    @Override
    public List<Trip> getTripsByArrivalTime(String ArrivalTime) {
        trip_logger.info("Getting all Trips by Arrival Time: " + ArrivalTime);

        try {
            return entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.ArrivalTime LIKE concat('%', (:arrivalTime), '%')", Trip.class).
                    setParameter("arrivalTime", ArrivalTime).getResultList();
        } catch (NoResultException e) {
            trip_logger.error("No Results Found: " + ArrivalTime, e);
            throw e;
        }

    }

    @Override
    public ErrorsEnum removeTrip(java.util.UUID TripId) {
        Trip trip = entityManager.find(Trip.class, TripId);

        //Get tickets sold
        TypedQuery<Ticket> q = entityManager.createQuery("SELECT tck FROM Ticket tck WHERE tck.Trip = (:id) AND tck.Id = (SELECT ct.Tickets_Id FROM client_ticket ct WHERE ct.Tickets_Id = tck.Id)", Ticket.class);
        q.setParameter("id", trip);
        List<Ticket> tripTicketsSold = q.getResultList();

        //Get all tickets for that trip
        q = entityManager.createQuery("SELECT tck FROM Ticket tck WHERE tck.Trip = (:id)", Ticket.class);
        q.setParameter("id", trip);
        List<Ticket> tripTickets = q.getResultList();

        System.out.println("\n\n\n\n Number of tickets for trip "+ tripTickets.size());
        System.out.println("\n\n\n\n Number of sold tickets for trip "+ tripTicketsSold.size());

        //Check if trip is in the future
        boolean tripIsFuture = false;
        try {
            LocalDateTime dtt = LocalDateTime.parse(trip.getDepartureTime());
            if (dtt.isAfter(LocalDateTime.now())) {
                tripIsFuture = true;
            }
        } catch (Exception e) {
            LocalDate dtt = LocalDate.parse(trip.getDepartureTime());
            if (dtt.isAfter(LocalDate.now())) {
                tripIsFuture = true;
            }
        }

        System.out.println("\n\n\n\n tripIsFuture"+tripIsFuture);
        System.out.println("\n\n\n\n Cycle:");

        //For each ticket
        for (Ticket tck :
                tripTickets) {

            System.out.println(" Ticket id "+ tck.getId());
            if (tripTicketsSold.contains(tck)) {
                System.out.println(" Ticket is sold "+ tck.getId());
                if (tripIsFuture) {
                    System.out.println(" Ticket is in future "+ tck.getId());

                    TypedQuery<Client_Ticket> query = entityManager.createQuery("SELECT ct FROM client_ticket ct WHERE ct.Tickets_Id = (:id)", Client_Ticket.class).setParameter("id", tck.getId());
                    Client client = getClientById(query.getSingleResult().getClient_Id());

                    if (client != null) {
                        System.out.println(" Ticket owner "+ client.getName());

                        TypedQuery<Client_Ticket> ctq = entityManager.createQuery("SELECT ct FROM client_ticket ct WHERE ct.Tickets_Id = (:tid) AND ct.Client_Id = (:cid)", Client_Ticket.class).setParameter("tid", tck.getId()).setParameter("cid", client.getId());
                        entityManager.remove(ctq.getSingleResult());
                        System.out.println("Deleted entry from client_ticket");

                        //Refund
                        client.getWallet().setBalance(client.getWallet().getBalance() + trip.getPrice());
                        System.out.println("Made a refund for " + trip.getPrice());

                        //Mail warning
                        try {
                            MimeMessage m = new MimeMessage(mailSession);
                            Address from = new InternetAddress("busflix32@gmail.com");
                            Address[] to = new InternetAddress[]{new InternetAddress(client.getEmail())};
                            m.setFrom(from);
                            m.setRecipients(Message.RecipientType.TO, to);
                            m.setSubject("Busflix notification");
                            m.setSentDate(new java.util.Date());
                            m.setContent("Your trip schedule for " + trip.getDepartureTime() + " has been canceled\nA reimbursement has been made for " + trip.getPrice() + " euros." + "\nSorry for the inconvenience.", "text/plain");
                            Transport.send(m);
                        } catch (javax.mail.MessagingException e) {
                            e.printStackTrace();
                            trip_logger.error("Error in Sending Mail: " + e);
                        }
                        System.out.println("Email sent");

                        entityManager.remove(tck);
                        System.out.println("Removed ticket");
                    } else {
                        trip_logger.info("Something went wrong client is null");
                    }
                } else {
                    TypedQuery<Client_Ticket> query = entityManager.createQuery("SELECT ct FROM client_ticket ct WHERE ct.Tickets_Id = (:id)", Client_Ticket.class).setParameter("id", tck.getId());
                    Client client = getClientById(query.getSingleResult().getClient_Id());
                    if (client != null) {
                        TypedQuery<Client_Ticket> ctq = entityManager.createQuery("SELECT ct FROM client_ticket ct WHERE ct.Tickets_Id = (:tid) AND ct.Client_Id = (:cid)", Client_Ticket.class).setParameter("tid", tck.getId()).setParameter("cid", client.getId());
                        entityManager.remove(ctq.getSingleResult());
                    }else{
                        trip_logger.info("Something went wrong client is null");

                    }
                    entityManager.remove(tck);
                    System.out.println("Trip is old so no reimbursement");
                }
            } else {
                entityManager.remove(tck);
                System.out.println("Ticket is not sold so just remove");
            }
        }

        entityManager.remove(trip);
        return ErrorsEnum.TRIP_REMOVED_SUCCESSFULLY;
    }

    @Override
    public ErrorsEnum updateTrip(Trip trip) {
        trip_logger.info("Updating Trip: " + trip.toString());

        try {

            Trip queryTrip = entityManager.find(Trip.class, trip.getId());
            queryTrip.setArrivalTime(trip.getArrivalTime());
            queryTrip.setDepartureTime(trip.getDepartureTime());
            queryTrip.setCapacity(trip.getCapacity());
            queryTrip.setDestination(trip.getDestination());
            queryTrip.setOrigin(trip.getOrigin());
            queryTrip.setPrice(trip.getPrice());
            queryTrip.setTickets(trip.getTickets());

            entityManager.merge(queryTrip);

        } catch (NoResultException e) {
            trip_logger.error("ERROR WHILE GETTING TRIP: " + trip.getId());
            throw e;
        } finally {
            entityManager.close();
        }
        return ErrorsEnum.TRIP_UPDATED_SUCCESSFULLY;
    }

    @Override
    public List<Trip> getAvailableTrips(String DepartureTime, String ArrivalTime) {
        trip_logger.info("Fetching all available trips from: " + DepartureTime + " to " + ArrivalTime);
        try {
            List<Trip> trips = getAllTrips();

            List<Trip> finalTrips = new ArrayList<>();

            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate dpStart = LocalDate.parse(DepartureTime);
            LocalDate dpEnd = LocalDate.parse(ArrivalTime);


            for (Trip tp :
                    trips) {
                try {
                    LocalDate tpDp = LocalDateTime.parse(tp.getDepartureTime()).toLocalDate();
                    if ((tpDp.isAfter(dpStart) || tpDp.isEqual(dpStart)) && (tpDp.isBefore(dpEnd) || tpDp.isEqual(dpEnd))) {
                        finalTrips.add(tp);
                    }
                } catch (Exception e) {
                    LocalDate tpDp = LocalDate.parse(tp.getDepartureTime());
                    if ((tpDp.isAfter(dpStart) || tpDp.isEqual(dpStart)) && (tpDp.isBefore(dpEnd) || tpDp.isEqual(dpEnd))) {
                        finalTrips.add(tp);
                    }
                }

            }

            trip_logger.info("Number of fetched trips" + finalTrips.size());

            return finalTrips;
        } catch (Exception e) {
            trip_logger.error("Error while fetching all available trips from: " + DepartureTime + " to " + ArrivalTime);
            throw e;
        }
    }

    public Client getClientById(java.util.UUID Id) {
        try {
            return entityManager.createQuery("SELECT c FROM Client c WHERE c.Id = (:id) ", Client.class).
                    setParameter("id", Id).
                    getSingleResult();
        } catch (NoResultException e) {
            trip_logger.error("NOT RESULTS FOUND FOR THAT CLIENT ID: " + Id + " \n", e);
            throw e;
        }
    }

    public List<Client> getTripPassengers(UUID TripId) {

        TypedQuery<Ticket> q = entityManager.createQuery("SELECT tck FROM Ticket tck WHERE tck.Trip = (:id)", Ticket.class).setParameter("id", entityManager.find(Trip.class, TripId));

        List<Ticket> tickets = q.getResultList();

        List<Client> clnts = new ArrayList<>();

        for (Ticket tck :
                tickets) {
            TypedQuery<Client_Ticket> qu = entityManager.createQuery("SELECT ct FROM client_ticket ct WHERE ct.Tickets_Id = (:id)", Client_Ticket.class).setParameter("id", tck.getId());
            List<Client_Ticket> cts = qu.getResultList();
            for (Client_Ticket ct :
                    cts) {
                clnts.add(getClientById(ct.getClient_Id()));
            }
        }
        return clnts;
    }
}
