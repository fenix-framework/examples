package pt.ist.fenixframework.example.tpcw.loadbalance;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class LoadBalance {
	private static int numberOfServers;
	private static String[] serverAddresses;
	private static LoadBalancePolicy policy;
	private static LoadBalanceConfig conf;
	
	static {//static init
		try {
			conf = LoadBalanceConfig.loadConfig((new File("/home/sgarbatov/work/alchemist/tpcw_lb/conf/lb.conf")).toURI().toURL());
			numberOfServers = conf.numberOfReplicas;
			serverAddresses = conf.replicaAddresses;
			policy = conf.getPolicyInstance();
			System.out.println("LoadBalanceConfig is = "+conf);
			//policy = new LDAPolicy(serverAddresses);
			//policy = new ProfilePolicy(serverAddresses);
			System.out.println("\n\nLoadBalancer initialized!\n\n\n");
		} catch (MalformedURLException murlex) {
			System.out.println("LoadBalancer::we got an exception at init");
			murlex.printStackTrace();
		}
	}
	
	public LoadBalance() {}
	
	public static final URL getUrl(HttpServletRequest req) throws IOException {
		return policy.getUrl(req);
	}
}

