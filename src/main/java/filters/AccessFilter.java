package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * Created by Pavel on 24.11.2017.
 * Site access filter
 */

@WebFilter("/")
public class AccessFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) req).getRequestURI();
        if (path.startsWith("/index")) {
            chain.doFilter(req, resp);
            return;
        }
        if (path.startsWith("/auth")) {
            chain.doFilter(req, resp);
            return;
        }
        if (path.equals("/favicon.ico")) {
            chain.doFilter(req, resp);
            return;
        }
        if (path.equals("/img/icons.png")) {
            chain.doFilter(req, resp);
            return;
        }
        if (path.startsWith("/resources")) {
            chain.doFilter(req, resp);
            return;
        }

        if (path.equals("/account")) {
            if (req.getParameter("code") == null) {
                req.setAttribute("error", "You aren't authorized!");
            } else {
                chain.doFilter(req, resp);
                return;
            }
        }
        req.getRequestDispatcher("/index").forward(req, resp);
    }

    @Override
    public void destroy() {
    }
}