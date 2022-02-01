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
import BeansInterfaces.ICm;
import Utils.ErrorsEnum;
import uc.mei.Entities.Client;
import uc.mei.Entities.CompanyManager;

@WebServlet("loginCm")
public class LoginCmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private ICm cmBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("loginCm.jsp").forward(request, response);

        //cmBean.setCm("admin", "admin@gmail.com", "admin");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String errorMsg;

        String path = "loginCm";

        ErrorsEnum loginResult = cmBean.login(email, password);

        if (loginResult == ErrorsEnum.LOGIN_SUCCESSFUL)
        {

            HttpSession session = request.getSession();

            CompanyManager cm = cmBean.getCmByEmail(email);

            session.setAttribute("userEmail", email);
            session.setAttribute("cm", cm);

            path = "secured/homeCm";
            request.removeAttribute("errorMsg");

        }
        else if(loginResult == ErrorsEnum.LOGIN_WRONG_CREDENTIALS)
        {
            errorMsg = "Wrong password";
            request.setAttribute("errorMsg", errorMsg);
            response.sendRedirect(request.getContextPath() + "/loginCm.jsp");
            path="loginCm";

        }else{

            errorMsg = "The email isn't associated with an account";
            request.setAttribute("errorMsg", errorMsg);
            path = "loginCm";
        }

        response.sendRedirect("http://localhost:8080/web/" + path);
    }
}