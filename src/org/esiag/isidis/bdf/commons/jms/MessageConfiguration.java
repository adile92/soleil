package org.esiag.isidis.bdf.commons.jms;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class MessageConfiguration {
  private String destination;
  private AcknowledgeMode acknowledgeMode = AcknowledgeMode.AUTO;
  private ConnexionMode mode = ConnexionMode.QUEUE;
  private boolean transactional = false;
  private RejectedExecutionHandler messageServicePolicy = new ThreadPoolExecutor.AbortPolicy();

  public MessageConfiguration(String destination) {
    this.destination = destination;
  }
  public String getDestination() {
    return destination;
  }
  public void setDestination(String destination) {
    this.destination = destination;
  }
  public AcknowledgeMode getAcknowledgeMode() {
    return acknowledgeMode;
  }
  public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
    this.acknowledgeMode = acknowledgeMode;
  }
  public boolean isTransactional() {
    return transactional;
  }
  public void setTransactional(boolean transactional) {
    this.transactional = transactional;
  }
  public ConnexionMode getMode() {
    return mode;
  }
  public void setMode(ConnexionMode mode) {
    this.mode = mode;
  }
  public RejectedExecutionHandler getMessageServicePolicy() {
    return messageServicePolicy;
  }
  public void setMessageServicePolicy(RejectedExecutionHandler messageServicePolicy) {
    this.messageServicePolicy = messageServicePolicy;
  }
 
}
