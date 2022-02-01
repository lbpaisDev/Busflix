package servlet;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import BeansImplementation.ClientBean;
import BeansInterfaces.IClient;
import BeansInterfaces.IWallet;
import Utils.ErrorsEnum;
import uc.mei.Entities.Client;

@WebServlet("register")
public class RegisterServlet extends HttpServlet {

    @EJB
    private IClient clientBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String address = request.getParameter("address");

        String errorMsg;

        ErrorsEnum register = clientBean.register(name, email, password, address);

        if (register == ErrorsEnum.CLIENT_ADDED_SUCCESSFULLY)
        { //CLIENT REGISTERED SUCCESSFULLY
            HttpSession session = request.getSession();
            session.setAttribute("userEmail", email);

            request.setAttribute("errorMsg", "false");

            response.sendRedirect("http://localhost:8080/web/login.jsp");
        }
        else if(register == ErrorsEnum.CLIENT_ALREADY_EXISTS)
        {
            //EMAIL ALREADY IN USE
            errorMsg = "Email already in use";
            request.setAttribute("errorMsg", errorMsg);
            response.sendRedirect(request.getContextPath() + "/register");
        }
        else
        {
            //SOMETHING WENT WRONG
            request.removeAttribute("errorMsg");
            response.sendRedirect(request.getContextPath() + "/register");
        }
    }
}
