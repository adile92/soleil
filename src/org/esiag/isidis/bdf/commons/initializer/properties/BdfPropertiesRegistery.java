package org.esiag.isidis.bdf.commons.initializer.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chargement des fichiers properties dans l'application
 * 
 * @author Damien Brochon
 * @version 1.0
 */
public class BdfPropertiesRegistery {
  /**
   * logger de la classe
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(BdfPropertiesRegistery.class);

  /**
   * Properties
   */
  private Properties properties = new Properties();
  /**
   * Chemin du fichier properties
   */
  private String[] fileNamesProperties;

  /**
   * Constructeur de BdfPropertiesRegistery
   */
  @SuppressWarnings("unused")
  private BdfPropertiesRegistery() {

  }

  /**
   * Constructeur de BdfPropertiesRegistery
   * @param fileNameProperty chemin du fichier properties
   */
  public BdfPropertiesRegistery(String... fileNameProperty) {
    this.fileNamesProperties = fileNameProperty;
    this.loadProperties();
  }

  /**
   * Chargement du fichier properties
   */
  private void loadProperties() {
    for (String fileName : fileNamesProperties) {
      try {
        this.properties.load(this.getClass().getClassLoader().getResourceAsStream(fileName));
      } catch (FileNotFoundException e) {
        LOGGER.error("The property file=[{}] was not find ", new Object[]{fileName});
      } catch (IOException e) {
        LOGGER.error("The property file=[{}] was not load because of IOException {} ", new Object[]{fileName, e.getMessage()});

      }

    }
  }

  public String getProperty(String name) {
    return properties.getProperty(name);

  }

  /**
   * @return les properties
   */
  public Properties getProperties() {
    return this.properties;
  }
}
