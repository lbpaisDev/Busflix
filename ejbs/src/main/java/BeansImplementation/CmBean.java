package BeansImplementation;

import BeansInterfaces.ICm;
import Utils.ErrorsEnum;
import Utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uc.mei.Entities.*;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Comparator.comparing;

@Stateless
@Local
public class CmBean implements ICm {
    private final Logger cm_logger = LoggerFactory.getLogger(Client.class);

    @PersistenceContext(unitName = "busflix")
    EntityManager entityManager;

    @Override
    public CompanyManager getCmByEmail(String Email) {
        try {
            return entityManager.createQuery("SELECT cm FROM CompanyManager cm WHERE cm.Email = (:email)", CompanyManager.class).
                    setParameter("email", Email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public ErrorsEnum login(String Email, String Password) {
        try {
            CompanyManager cm = getCmByEmail(Email);

            if (cm == null) {
                return ErrorsEnum.CLIENT_NOT_FOUND;
            } else {
                try {
                    if (cm.getPassword().equals(MD5.Encrypt(Password))) {
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
            cm_logger.error("ERROR WHILE LOGGING IN WITH THE FOLLOWING EMAIL: " + Email + " \n", e);
            return ErrorsEnum.ERROR_DATABASE;
        }
    }

    @Override
    public void setCm(String username, String email, String password) {

        String hashedPassword = null;

        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            //Add password bytes to digest
            md.update(password.getBytes());

            //Get the hash's bytes
            byte[] bytes = md.digest();

            //These bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            //Get complete hashed password in hex format
            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        CompanyManager companyManager = new CompanyManager(username, email, hashedPassword);

        entityManager.persist(companyManager);
    }

    public List<Client> GetAllClients() {

        try {
            TypedQuery<Client> clients = entityManager.createQuery("FROM Client c", Client.class);

            return clients.getResultList();
        } catch (NoResultException e) {
            cm_logger.error("Error while fetching every client in getAllClients()", e);
            throw e;
        }
    }

    public List<Client> getAllClients() {
        try {
            return entityManager.createQuery("FROM Client c", Client.class).getResultList();
        } catch (NoResultException e) {
            cm_logger.error("Error while fetching every client in getAllClients()", e);
            throw e;
        }
    }

    public List<Client> getTop5Clients() {
        List<Client> allClients = getAllClients();

        for (Client clnt :
                allClients) {

            TypedQuery<Client_Ticket> ctq = entityManager.createQuery("SELECT ct FROM client_ticket ct WHERE ct.Client_Id = (:id)", Client_Ticket.class).setParameter("id", clnt.getId());
            List<Client_Ticket> cts = ctq.getResultList();

            clnt.setNtrips(cts.size());

        }

        Collections.sort(allClients, Client.comparator);

        List<Client> top5 = new ArrayList<>();
        if (allClients.size() < 6) {
            top5 = allClients.subList(0, allClients.size());
        } else {
            top5 = allClients.subList(0, 5);
        }
        return top5;
    }

    @Override
    public ErrorsEnum createTrip(String dp, String arr, String org, String dst, String cpct, String price) {
        int icpct = Integer.parseInt(cpct);
        float fprice = Float.parseFloat(price);

        Trip tp = new Trip(new ArrayList<Ticket>(), dp, arr, org, dst, fprice, icpct);

        List<Ticket> tcks = new ArrayList<>();
        for (int i = 0; i < icpct; i++) {
            tcks.add(new Ticket(tp, i + 1));
        }

        for (Ticket tck :
                tcks) {
            entityManager.persist(tck);
        }

        entityManager.persist(tp);

        return ErrorsEnum.TRIP_UPDATED_SUCCESSFULLY;
    }


    public List<Trip> getTripsByDepartureTime(String DepartureTime) {
        cm_logger.info("Getting all Trips by Departure Time: " + DepartureTime);
        try {
            //return entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.DepartureTime LIKE concat('%',:departureTime,'%')", Trip.class).
              //      setParameter("departureTime", DepartureTime).getResultList();
            return entityManager.createQuery("SELECT tr FROM Trip tr WHERE tr.DepartureTime > (:departureTime)", Trip.class).
                    setParameter("departureTime", DepartureTime).getResultList();
        } catch (NoResultException e) {
            cm_logger.error("No Results Founds for Departure Time: " + DepartureTime, e);
            throw e;
        }

    }

    @Override
    public float getReveneu(LocalDateTime date) {
        float rev = 0f;

        LocalDate dt = LocalDateTime.parse(date.toString()).toLocalDate();

        //Get current day trips
        List<Trip> trips = getTripsByDepartureTime(dt.toString());

        //Get all tickets sold
        TypedQuery<Client_Ticket> ctq = entityManager.createQuery("SELECT ct FROM client_ticket ct", Client_Ticket.class);
        List<Client_Ticket> cts = ctq.getResultList();

        //Get all tickets ids
        List<String> tIds = new ArrayList<>();
        for (Client_Ticket ct :
                cts) {
            tIds.add(ct.getTickets_Id().toString());
        }

        int ticketsSold = 0;
        for (Trip tp :
                trips) {

            //Get trip price
            Float price = tp.getPrice();

            //Get all tickets for trip
            TypedQuery<Ticket> tq = entityManager.createQuery("SELECT t FROM Ticket t WHERE t.Trip=(:id)", Ticket.class).setParameter("id", tp);
            List<Ticket> tckts = tq.getResultList();

            for (Ticket tck :
                    tckts) {
                //If ticket was sold
                if (tIds.contains(tck.getId().toString())) {
                    //ticketsSold++;
                    rev = rev + price;
                }
            }
            //rev = rev + (price * ticketsSold);
        }
        return rev;
    }
}
