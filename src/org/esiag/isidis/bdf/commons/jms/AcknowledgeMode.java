package org.esiag.isidis.bdf.commons.jms;

public enum AcknowledgeMode {

  AUTO((int)1), 
  CLIENT((int)2), 
  DUPS_OK((int)3),
  SESSION_TRANSACTED((int)0);
  
  private AcknowledgeMode(int value) {
    this.value = value;
  }

  private int value;
  
  public int value(){
    return this.value;
  }
  
  public static AcknowledgeMode get(byte value){
    if(value == AUTO.value()){
      return AUTO;
    }else if(value == CLIENT.value()){
      return CLIENT;
    }else if(value == DUPS_OK.value()){
      return DUPS_OK;
    }else if(value == SESSION_TRANSACTED.value()){
      return SESSION_TRANSACTED;
    }
    return null;
  }
}