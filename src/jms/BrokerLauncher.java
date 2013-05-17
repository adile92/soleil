package jms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;

public class BrokerLauncher {
	static BrokerService broker1;
	static String msgExpected = "Message";
	static String msgReceived;
	static Logger logger = Logger.getLogger(BrokerLauncher.class);

	public static void main(String[] args) {
		try {
			setUp();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setUp() throws URISyntaxException, Exception {
		broker1 = BrokerFactory.createBroker(new URI(
				"xbean:activemq-static-network-broker1.xml"));
		TimeUnit.SECONDS.sleep(2);
	}


	public void tearDown() throws Exception {
		broker1.stop();
	}
}
