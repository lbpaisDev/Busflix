package servlet;

import BeansInterfaces.*;
import Utils.ErrorsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uc.mei.Entities.*;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/secured/homeCm")
public class HomeCmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private ICm cmBean;

    private final Logger revenue = LoggerFactory.getLogger(Client.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        CompanyManager cm = (CompanyManager) session.getAttribute("cm");
        session.setAttribute("client", cm);


        List<Client> clnts = cmBean.getTop5Clients();
        session.setAttribute("clnts", clnts);

        request.getRequestDispatcher("/secured/homeCm.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        String revBtn = request.getParameter("rev");

        System.out.println("\n\n rev-"+revBtn);
        if(revBtn != ""){
            float rev = cmBean.getReveneu(LocalDateTime.now());
            session.setAttribute("rev", rev);
        }

        if(LocalDateTime.now().getHour() == LocalDateTime.parse("2021-12-30T23:59:59").getHour()){
            float rev = cmBean.getReveneu(LocalDateTime.now());
            session.setAttribute("rev", rev);
            revenue.info("Profit for " + LocalDate.now() + "is" + rev);
        }

        request.getRequestDispatcher("/secured/homeCm.jsp").forward(request, response);
    }
}

