package servlet;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import BeansInterfaces.IClient;
import Utils.ErrorsEnum;
import uc.mei.Entities.Client;

@WebServlet("login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private IClient clientBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String errorMsg;

        ErrorsEnum loginResult = clientBean.login(email, password);

        if (loginResult == ErrorsEnum.LOGIN_SUCCESSFUL)
        {

            HttpSession session = request.getSession();

            Client client = clientBean.getClientByEmail(email);

            session.setAttribute("userEmail", email);
            session.setAttribute("client", client);

            response.sendRedirect(request.getContextPath() + "/secured/home");
            request.removeAttribute("errorMsg");

        }
        else if(loginResult == ErrorsEnum.LOGIN_WRONG_CREDENTIALS)
        {

            errorMsg = "Wrong password";
            request.setAttribute("errorMsg", errorMsg);
            response.sendRedirect(request.getContextPath() + "/login.jsp");

        }else{

            errorMsg = "The email isn't associated with an account";
            request.setAttribute("errorMsg", errorMsg);
            response.sendRedirect(request.getContextPath() + "/login.jsp");

        }
    }
}