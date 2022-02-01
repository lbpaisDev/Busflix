package servlet;

import BeansInterfaces.IClient;
import BeansInterfaces.ITicket;
import BeansInterfaces.ITrip;
import BeansInterfaces.IWallet;
import Utils.ErrorsEnum;
import uc.mei.Entities.*;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/secured/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private IClient clientBean;

    @EJB
    private ITicket ticketBean;

    @EJB
    private ITrip tripBean;

    @EJB
    private IWallet walletBean;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        Client client = (Client) session.getAttribute("client");
        session.setAttribute("client", client);

        List<java.util.UUID> ticketIds = clientBean.getClientTickets(client.getId());

        List<Ticket> clientTickets = new ArrayList<>();

        for (java.util.UUID tckId:
                ticketIds) {

            clientTickets.add(ticketBean.getTicketById(tckId));

        }

        session.setAttribute("cTickets", clientTickets);
        request.getRequestDispatcher("/secured/home.jsp").forward(request, response);

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String refundId = request.getParameter("refundId");

        Client client = (Client) session.getAttribute("client");
        session.setAttribute("client", client);

        walletBean.makeReimbursement(client.getId(), java.util.UUID.fromString(refundId));

        response.sendRedirect("http://localhost:8080/web/secured/home");
    }
}

