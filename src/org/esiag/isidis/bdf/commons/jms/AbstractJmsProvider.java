package org.esiag.isidis.bdf.commons.jms;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public abstract class AbstractJmsProvider implements ExceptionListener {

  private static final Logger LOGGER = Logger.getLogger(AbstractJmsProvider.class);
  private ConnectionFactory connectionFactory;
  private Connection connection;
  private Session session;
  private Destination destination;

  private MessageConfiguration configuration;

  protected ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  protected Connection getConnection() {
    return connection;
  }

  protected Session getSession() {
    return session;
  }

  protected Destination getDestination() {
    return destination;
  }

  protected MessageConfiguration getConfiguration() {
    return configuration;
  }
  
  public AbstractJmsProvider(String failover,MessageConfiguration configuration) {
	  connectionFactory = new ActiveMQConnectionFactory(failover);
	  

	    try {
	      if (configuration == null) {
	        return;
	      }
	      this.configuration = configuration;

	      switch (configuration.getMode()) {
	        case QUEUE:
	          initQueue(configuration,true);
	          break;
	        case TOPIC:
	          initTopic(configuration,true);
	          break;
	        default:
	          break;
	      }
	    } catch (NamingException e) {
	      LOGGER.error("", e);
	      try {
	        if (this.session != null) {
	          this.session.close();
	        }
	        if (this.connection != null) {
	          this.connection.close();
	        }
	      } catch (JMSException e1) {
	        LOGGER.error("", e1);
	      }
	      System.exit(-1);
	    } catch (JMSException e) {
	      LOGGER.error("", e);
	      try {
	        if (this.session != null) {
	          this.session.close();
	        }
	        if (this.connection != null) {
	          this.connection.close();
	        }
	      } catch (JMSException e1) {
	        LOGGER.error("", e1);
	      }
	      System.exit(-1);
	    }
	  
	    
	    
  }

public AbstractJmsProvider(MessageConfiguration configuration) {
    try {
      if (configuration == null) {
        return;
      }
      this.configuration = configuration;

      switch (configuration.getMode()) {
        case QUEUE:
          initQueue(configuration,false);
          break;
        case TOPIC:
          initTopic(configuration,false);
          break;
        default:
          break;
      }
    } catch (NamingException e) {
      LOGGER.error("", e);
      try {
        if (this.session != null) {
          this.session.close();
        }
        if (this.connection != null) {
          this.connection.close();
        }
      } catch (JMSException e1) {
        LOGGER.error("", e1);
      }
      System.exit(-1);
    } catch (JMSException e) {
      LOGGER.error("", e);
      try {
        if (this.session != null) {
          this.session.close();
        }
        if (this.connection != null) {
          this.connection.close();
        }
      } catch (JMSException e1) {
        LOGGER.error("", e1);
      }
      System.exit(-1);
    }
  }

  public Message createTextMessage(String str) throws JMSException {
    return this.session.createTextMessage(str);
  }

  public BytesMessage createBytesMessage() throws JMSException {
    return this.session.createBytesMessage();
  }

  public StreamMessage createStreamMessage() throws JMSException {
    return this.session.createStreamMessage();
  }

  public Message createObjectMessage(Serializable serializable) throws JMSException {
    return this.session.createObjectMessage(serializable);
  }

  private void initTopic(MessageConfiguration configuration,boolean failover) throws JMSException, NamingException {
    Context ctx = new InitialContext();
    
    if(!failover)
    	this.connectionFactory = (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");
    
    this.connection = connectionFactory.createConnection();
    this.connection.start();
    this.session =
        this.connection.createSession(configuration.isTransactional(), configuration.getAcknowledgeMode().value());
    this.destination = (Topic) ctx.lookup(configuration.getDestination());
  }

  private void initQueue(MessageConfiguration configuration,boolean failover) throws JMSException, NamingException {
    Context ctx = new InitialContext();
    
    if(!failover)
    	this.connectionFactory = (QueueConnectionFactory) ctx.lookup("QueueConnectionFactory");
    
    this.connection = connectionFactory.createConnection();
    this.connection.start();
    this.session =
        this.connection.createSession(configuration.isTransactional(), configuration.getAcknowledgeMode().value());
    this.destination = (Queue) ctx.lookup(configuration.getDestination());
  }

  public void close() throws JMSException {
    if (this.session != null) {
      this.session.close();
    }
    if (this.connection != null) {
      this.connection.close();
    }
  }
}
