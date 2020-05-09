package main.java;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "EmployeeFilter", urlPatterns = "/*")
public class EmployeeFilter implements Filter {
    private final ArrayList<String> disallowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("EmployeeFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in
        if (!this.isUrlNotAllowedWithoutEmployeeStatus(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        // Redirect to main page if user is not employee
        User user = (User) httpRequest.getSession().getAttribute("user");
        if (!user.isEmployee()) {
            httpResponse.sendRedirect("/Fabflix/index.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlNotAllowedWithoutEmployeeStatus(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return disallowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        disallowedURIs.add("_dashboard.html");
    }

    public void destroy() {
        // ignored.
    }
}