package servlet;

import BeansInterfaces.IClient;
import BeansInterfaces.ITicket;
import BeansInterfaces.ITrip;
import com.sun.xml.bind.v2.schemagen.xmlschema.TypeDefParticle;
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


@WebServlet("/secured/searchTrips")
public class SearchTripsServlet extends HttpServlet {
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

        List<Trip> trips = tripBean.getAllTrips();
        session.setAttribute("trips", trips);

        String button = request.getParameter("button");

        if( button != null){
            String dp = request.getParameter("departure");
            String arr = request.getParameter("arrival");

             if(dp == "" && arr != ""){
                trips = tripBean.getTripsByArrivalTime(arr);
            }else if(dp != "" && arr == ""){
                trips = tripBean.getTripsByDepartureTime(dp);
            }else if(dp != "" && arr != ""){
                trips = tripBean.getAvailableTrips(dp, arr);
            }

            session.setAttribute("trips", trips);
        }

        request.getRequestDispatcher("/secured/searchTrips.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        String tripId = request.getParameter("tripId");

        session.setAttribute("tripId", tripId);

        response.sendRedirect("http://localhost:8080/web/secured/buyTickets");
    }
}
