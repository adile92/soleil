package org.esiag.isidis.bdf.commons.jms;

public enum ConnexionMode {
  QUEUE((int)0), 
  TOPIC((int)1);
  
  private ConnexionMode(int value) {
    this.value = value;
  }

  private int value;
  
  public int value(){
    return this.value;
  }
  
  public static ConnexionMode get(byte value){
    if(value == QUEUE.value()){
      return QUEUE;
    }else if(value == TOPIC.value()){
      return TOPIC;
    }
    return null;
  }
}
