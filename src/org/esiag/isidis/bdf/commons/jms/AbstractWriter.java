package org.esiag.isidis.bdf.commons.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.apache.log4j.Logger;

public abstract class AbstractWriter extends AbstractJmsProvider implements ExceptionListener {
  private static final Logger LOGGER = Logger.getLogger(AbstractWriter.class);

  private MessageProducer producer;

  /**
   * Constructeur prenant en parametre le nom de la queue.
   * @param destination le nom de la queue
   * @throws Exception 
   */
  public AbstractWriter(String destination) {
    this(new MessageConfiguration(destination));
  }

  public AbstractWriter(MessageConfiguration configuration) {
    super(configuration);
  }
  
  public AbstractWriter(String failover,MessageConfiguration configuration) {
	    super(failover,configuration);
	  }

  /**
   * Envoi un message sur la file.
   * @param xmlText le message a envoyer
   * @throws Exception
   */
  public synchronized void send(Message message) throws JMSException {
    try {

      if (this.producer == null) {
        this.producer = this.getSession().createProducer(this.getDestination());
      }
      this.producer.send(message);
      LOGGER.info("msg sent");
    } catch (Exception e) {
      LOGGER.error("", e);
      this.close();
    }
  }
}
