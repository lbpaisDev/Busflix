package servlet;

import BeansInterfaces.IClient;
import BeansInterfaces.ITicket;
import BeansInterfaces.ITrip;
import BeansInterfaces.IWallet;
import Utils.ErrorsEnum;
import uc.mei.Entities.*;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/secured/clientProfile")
public class ClientProfileServlet extends HttpServlet {
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
        request.getRequestDispatcher("clientProfile.jsp").forward(request, response);
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        Client client = (Client) session.getAttribute("client");

        String btn = request.getParameter("btn");

        if (btn.equals("vl")) {

            String vls = request.getParameter("chrgValue");

            float fvl = Float.parseFloat(vls);
            if(fvl > 0){
                client.getWallet().setBalance(client.getWallet().getBalance() + fvl);
                clientBean.updateClient(client, 1);
            }


        } else if (btn.equals("ml")) {
            String mls = request.getParameter("newEmail");
            client.setEmail(mls);

            clientBean.updateClient(client, 3);

        } else if (btn.equals("nm")) {

            String nms = request.getParameter("newName");
            client.setName(nms);

            clientBean.updateClient(client, 4);

        } else if (btn.equals("pw")) {

            String pws = request.getParameter("newPassword");
            client.setPassword(pws);

            clientBean.updateClient(client, 5);

        } else if (btn.equals("addrss")) {

            String addrsss = request.getParameter("newAddress");
            client.setAddress(addrsss);

            clientBean.updateClient(client, 2);
        }else if (btn.equals("dl")) {

            if (ErrorsEnum.CLIENT_REMOVED_SUCCESSFULLY == clientBean.removeClientById(client.getId()))
            {
                session.removeAttribute("userEmail");
                session.invalidate();
                response.sendRedirect("http://localhost:8080/web/index.jsp");
            }
        }
        response.sendRedirect("http://localhost:8080/web/secured/clientProfile");
    }

}