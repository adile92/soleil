package org.esiag.isidis.bdf.commons.jms;

import java.util.HashMap;
import java.util.Map;

public class MessageFilter {
  private Map<String, String> selectorDatas = new HashMap<String, String>();
  
  public Map<String, String> getSelectorDatas() {
    return selectorDatas;
  }
  
  public void addFilterEntry(String propertyKey, String propertyCondition) {
    this.selectorDatas.put(propertyKey, propertyCondition);
  }
}
