package BeansInterfaces;

import uc.mei.Entities.Client;
import uc.mei.Entities.Trip;
import Utils.ErrorsEnum;

import java.util.List;
import java.util.UUID;

public interface ITrip {

    List<Trip> getAllTrips();
    Trip getTripById(java.util.UUID Id);
    List<Trip> getTripsByDestination(String Destination);
    List<Trip> getTripsByOrigin(String Origin);
    List<Trip> getTripsByDepartureTime(String DepartureTime);
    List<Trip> getTripsByArrivalTime(String ArrivalTime);
    ErrorsEnum removeTrip(java.util.UUID TripId);
    ErrorsEnum updateTrip(Trip trip);
    List<Trip> getAvailableTrips(String DepartureTime, String ArrivalTime);
    List<Trip> getTripsByClientId(java.util.UUID ClientId);
    public List<Client> getTripPassengers(UUID TripId);
}
