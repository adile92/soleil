package org.esiag.isidis.bdf.commons.jms;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.log4j.Logger;
import org.esiag.isidis.bdf.commons.initializer.springconf.BdfApplicationContext;

public class BrokerLauncher implements Runnable {

	static Logger logger = Logger.getLogger(BrokerLauncher.class);
	static BrokerLauncher brokerLauncher;
	
	BrokerService broker;
	boolean isFirst = false;
	Thread self;
	
	public boolean isFirst() {
		return isFirst;
	}

	public BrokerLauncher()  {
		self = new Thread(this);
		self.start();
		
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(! isFirst){
			self.interrupt();
			try {
				broker.stop();
			} catch (Exception e) {
			}
			logger.info("Second participant au tchat.");
		}else{
			logger.info("Premier participant au tchat.");
		}
		
	}
	
	public static BrokerLauncher getBokerLauncher(){
		if(brokerLauncher == null){
			brokerLauncher = new BrokerLauncher();
		}
		return brokerLauncher;
	}
	
	public static void main(String[] args) {

		BrokerLauncher.getBokerLauncher();

	}

	public void stop() throws Exception {
		broker.stop();
	}

	@Override
	public void run() {
		try {
			broker = new BrokerService();

			TransportConnector connector = new TransportConnector();
			String adress = BdfApplicationContext.getInstance().getProperty("BrokerAdress");
			connector.setUri(new URI("tcp://"+ adress));

			broker.addConnector(connector);

			broker.start();
			
			isFirst = true;
			
		}  catch (Exception e) {
			isFirst = false;
		}
	}
}
