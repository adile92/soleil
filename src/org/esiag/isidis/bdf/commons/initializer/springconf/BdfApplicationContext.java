package org.esiag.isidis.bdf.commons.initializer.springconf;

import org.esiag.isidis.bdf.commons.initializer.exceptions.BdfTechnicalException;
import org.esiag.isidis.bdf.commons.initializer.properties.BdfPropertiesRegistery;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Chargement du contexte de plusieurs contexte spring de l'application
 * 
 * @author Damien Brochon
 * @version 1.0
 */
public class BdfApplicationContext extends ClassPathXmlApplicationContext {
  public static BdfApplicationContext instance;
  public static boolean isLoad = false;
  private BdfPropertiesRegistery properties;

  /**
   * @param startProperties le fichier properties
   * @param springFiles les contextes spring
   */
  private BdfApplicationContext(BdfPropertiesRegistery startProperties, String... springFiles) {
    super(springFiles, false);
    this.properties = startProperties;
    PropertyPlaceholderConfigurer propertyPlaceholderConfig = new PropertyPlaceholderConfigurer();
    propertyPlaceholderConfig.setIgnoreUnresolvablePlaceholders(true);
    propertyPlaceholderConfig.setProperties(startProperties.getProperties());

    addBeanFactoryPostProcessor(propertyPlaceholderConfig);

    refresh();
  }

  /**
   * This method
   * @param startProperties
   * @param springFiles
   * @throws BdfTechnicalException 
   */
  public static void loadProperties(BdfPropertiesRegistery startProperties, String... springFiles)
      throws BdfTechnicalException {
    if (instance == null)
      instance = new BdfApplicationContext(startProperties, springFiles);

  }

  public static BdfApplicationContext getInstance() {
    if (instance != null)
      return instance;
    else
      return null;
  }

  public String getProperty(String propertyName) {
    return this.properties.getProperty(propertyName);
  }
}
