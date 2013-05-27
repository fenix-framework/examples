package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class RoundRobinOneWriterPolicy extends RoundRobinPolicy {

    private static final Set<String> KNOWN_WRITE_REQUESTS = new HashSet<String>();

    static {
        // KNOWN_WRITE_REQUESTS.add("/TPCW_admin_request_servlet");
        KNOWN_WRITE_REQUESTS.add("/TPCW_admin_response_servlet");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_best_sellers_servlet");
        KNOWN_WRITE_REQUESTS.add("/TPCW_buy_confirm_servlet");
        KNOWN_WRITE_REQUESTS.add("/TPCW_buy_request_servlet");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_customer_registration_servlet");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_execute_search");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_home_interaction");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_new_products_servlet");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_order_display_servlet");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_order_inquiry_servlet");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_product_detail_servlet");
        // KNOWN_WRITE_REQUESTS.add("/TPCW_search_request_servlet");
        KNOWN_WRITE_REQUESTS.add("/TPCW_shopping_cart_interaction");
    }
    

    private int selector = 0;
    private int numberOfReadOnlyServers;
    private int writeServerIndex;
	
    public RoundRobinOneWriterPolicy(String[] serverAddresses) {
        super(serverAddresses);
        if (serverAddresses.length == 0) {
            throw new RuntimeException("We need at least one server to serve requests!!");
        }
        numberOfReadOnlyServers = serverAddresses.length - 1;
        writeServerIndex = serverAddresses.length - 1;
    }

    // picks the same fixed server (last) if running a write transaction. roundrobin otherwise
    private String getNextServerAddress(String servletPath) {
        if (numberOfReadOnlyServers == 0) {  // there is only one server anyway... :-(
            return serverAddresses[0];
        }

        // there is more than one server. return the last for write requests and round robin the other for read requests
        if (isWriteRequest(servletPath)) {
            return serverAddresses[writeServerIndex];
        } else {
            int serverIndex = selector++ % numberOfReadOnlyServers;
            return serverAddresses[serverIndex];
        }
    }

    private boolean isWriteRequest(String servletPath) {
        boolean result = KNOWN_WRITE_REQUESTS.contains(servletPath);
        System.out.println("REQUEST: " + servletPath + " is a " + (result?" write tx" : " read tx"));
        return result;
    }

	
    public URL getUrl(HttpServletRequest req) throws IOException {
        String servletPath = req.getServletPath();

        String selectedServerFullPath = getNextServerAddress(servletPath);

        String queryString = req.getQueryString();
        String newUrl = "";
        HttpSession session = req.getSession(false);

        newUrl = selectedServerFullPath + servletPath;

        if (req.getRequestedSessionId() != null) newUrl = newUrl + ";jsessionid=" + req.getRequestedSessionId();

        if (queryString != null) newUrl = newUrl + "?" + queryString;

        //if (session != null) newUrl = newUrl + ";jsessionid=" + session.getId();

        return new URL(newUrl);
    }
	
}
