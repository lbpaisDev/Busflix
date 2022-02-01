package servlet;

import BeansInterfaces.IClient;
import BeansInterfaces.ITicket;
import BeansInterfaces.ITrip;
import uc.mei.Entities.Client;
import uc.mei.Entities.Ticket;
import uc.mei.Entities.Trip;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@WebServlet("/secured/buyTickets")
public class BuyTicketsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private ITrip tripBean;

    @EJB
    private ITicket ticketBean;

    @EJB
    private IClient clientBean;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");

        List<Ticket> availableTcks = ticketBean.getAvailableTripTickets(java.util.UUID.fromString((String)session.getAttribute("tripId")));

        session.setAttribute("availableTcks", availableTcks);
        request.getRequestDispatcher("/secured/buyTickets.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");

        String tckIdBuy = request.getParameter("tckId");

        ticketBean.buyTicket(client.getId(), java.util.UUID.fromString(tckIdBuy));

        response.sendRedirect("http://localhost:8080/web/secured/home");
    }
}
