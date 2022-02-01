package servlet;

import BeansInterfaces.*;
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

@WebServlet("/secured/createTrip")
public class CreateTripServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    @EJB
    private ICm cmBean;


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        request.getRequestDispatcher("createTrip.jsp").forward(request, response);
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        String btn = request.getParameter("btn");

        System.out.println("\n\n\n\n btval: " + btn);

        if(btn == ""){
            String dpt = request.getParameter("dpt");
            String arrt = request.getParameter("arrt");
            String orgn = request.getParameter("orgn");
            String dest = request.getParameter("dest");
            String cpct = request.getParameter("cpct");
            String price = request.getParameter("price");

            cmBean.createTrip(dpt, arrt, orgn, dest, cpct, price);

        }

        response.sendRedirect("http://localhost:8080/web/secured/createTrip");


    }

}