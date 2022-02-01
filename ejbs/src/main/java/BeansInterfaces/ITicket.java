package BeansInterfaces;

import uc.mei.Entities.*;
import Utils.ErrorsEnum;

import java.util.List;

public interface ITicket {

    Ticket addTicket(Integer SeatNumber, Trip Trip);
    List<Ticket> getAllTickets();
    Ticket getTicketById(java.util.UUID Id);
    boolean removeTicketById(java.util.UUID Id);
    ErrorsEnum reAssignTicket(Ticket ticket);
    ErrorsEnum buyTicket(java.util.UUID ClientId, java.util.UUID TicketId);
    public List<Ticket> getAvailableTripTickets(java.util.UUID tripId);
}
