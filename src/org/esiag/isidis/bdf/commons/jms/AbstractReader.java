package org.esiag.isidis.bdf.commons.jms;

import org.esiag.isidis.bdf.commons.initializer.springconf.BdfApplicationContext;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicSession;

import org.apache.log4j.Logger;

public abstract class AbstractReader extends AbstractJmsProvider implements MessageListener {

  private static final Logger LOGGER = Logger.getLogger(AbstractReader.class);

  private static BdfApplicationContext context = BdfApplicationContext.getInstance();
  private final ExecutorService messageService;
  private final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(Integer.parseInt(context
      .getProperty("AbstractReader.QueueSize")));

  private MessageConsumer consumer;
  private String messageSelector;

  private MessageFilter filter;

  /**
   * Constructeur prenant en parametre la classe du listener de message et le nom de la queue.
   * @param listenerClass la classe du listener de message
   * @param destination le nom de la queue
   * @throws Exception
   */

  public AbstractReader(MessageConfiguration configuration) {
    this(configuration, null, null);
  }

  public AbstractReader(String destination) {
    this(new MessageConfiguration(destination), null, null);
  }

  public AbstractReader(String destination, String filtersValue) {
    this(new MessageConfiguration(destination), null, filtersValue);
  }

  public AbstractReader(MessageConfiguration configuration, MessageFilter filter) {
    this(configuration, filter, null);
  }
  
  public AbstractReader(String failover,MessageConfiguration configuration, MessageFilter filter) {
	    this(failover,configuration, filter, null);
	  }

  public AbstractReader(MessageConfiguration configuration, String filtersValue) {
    this(configuration, null, filtersValue);
  }

  private AbstractReader(MessageConfiguration configuration, MessageFilter filter, String filtersValue) {
    super(configuration);
    this.filter = filter;
    this.messageSelector = filtersValue;
    this.messageService =
        new ThreadPoolExecutor(Integer.parseInt(context.getProperty("AbstractReader.ServicePoolSize")),
            Integer.parseInt(context.getProperty("AbstractReader.ServiceMaxPoolSize")), Integer.parseInt(context
                .getProperty("AbstractReader.KeepAliveTime")), TimeUnit.MILLISECONDS, this.queue,
            configuration.getMessageServicePolicy());

    try {
      initializeFilter(configuration, filter);
    } catch (JMSException e) {
      LOGGER.error("", e);
      return;
    }
  }
  
  private AbstractReader(String failover,MessageConfiguration configuration, MessageFilter filter, String filtersValue) {
	    super(failover,configuration);
	    this.filter = filter;
	    this.messageSelector = filtersValue;
	    this.messageService =
	        new ThreadPoolExecutor(Integer.parseInt(context.getProperty("AbstractReader.ServicePoolSize")),
	            Integer.parseInt(context.getProperty("AbstractReader.ServiceMaxPoolSize")), Integer.parseInt(context
	                .getProperty("AbstractReader.KeepAliveTime")), TimeUnit.MILLISECONDS, this.queue,
	            configuration.getMessageServicePolicy());

	    try {
	      initializeFilter(configuration, filter);
	    } catch (JMSException e) {
	      LOGGER.error("", e);
	      return;
	    }
	  }

  public MessageFilter getFilter() {
    return filter;
  }

  private void initializeFilter(MessageConfiguration configuration, MessageFilter filter) throws JMSException {

    if (filter != null) {
      this.messageSelector = initializeSelector(filter);
    }

    switch (configuration.getMode()) {
      case QUEUE:
        this.consumer = this.getSession().createConsumer(this.getDestination(), this.messageSelector);
        break;
      case TOPIC:
        this.consumer =
            ((TopicSession) this.getSession()).createSubscriber((Topic) this.getDestination(), this.messageSelector,
                false);
        break;
      default:
        break;
    }
    this.consumer.setMessageListener(this);
  }

  private String initializeSelector(MessageFilter filter) {

    Map<String, String> map = filter.getSelectorDatas();

    if (map == null) {
      return "";
    }
    String selector = "";
    Set<Entry<String, String>> set = map.entrySet();
    Iterator<Entry<String, String>> i = set.iterator();

    do {
      Entry<String, String> me = i.next();
      selector += me.getKey() + " " + me.getValue();
      if (i.hasNext()) {
        selector += " AND ";
      }
    } while (i.hasNext());
    LOGGER.info(selector);
    return selector;
  }

  @Override
  public void onMessage(Message message) {
    MessageHandler handler = new MessageHandler(message);
    this.messageService.execute(handler);
  }

  public abstract void handleMessage(Message message);

  private class MessageHandler extends Thread {

    private Message message;

    public MessageHandler(Message message) {
      this.message = message;
    }

    @Override
    public void run() {
      LOGGER.info("Schedule new message.");
      handleMessage(this.message);
    }
  }

}
