package servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/secured/*")
public class SecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("Accessing the filter...");
        HttpServletRequest httpReq = (HttpServletRequest) servletRequest;
        HttpSession session = httpReq.getSession(false);

        if (session != null && session.getAttribute("userEmail") != null)
        {
            System.out.println("Verified authentication token...");
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else
        {
            servletRequest.getRequestDispatcher("/unauthenticated.jsp").forward(servletRequest, servletResponse);

        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
