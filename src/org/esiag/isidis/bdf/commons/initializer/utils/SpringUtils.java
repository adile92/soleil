package org.esiag.isidis.bdf.commons.initializer.utils;

import org.esiag.isidis.bdf.commons.initializer.exceptions.BdfTechnicalException;
import org.esiag.isidis.bdf.commons.initializer.exceptions.NullSpringFilesExceptions;
import org.esiag.isidis.bdf.commons.initializer.properties.BdfPropertiesRegistery;
import org.esiag.isidis.bdf.commons.initializer.springconf.BdfApplicationContext;

/**
 * Simplification de l'utilisation du chargeur de contexte spring
 * 
 * @author Damien Brochon
 * @version 1.0
 * @see org.esiag.isidis.bdf.commons.initializer.springconf.BdfApplicationContext
 * @see org.esiag.isidis.bdf.commons.initializer.properties.BdfPropertiesRegistery
 */
public final class SpringUtils {

  /**
   * Constructeur de SpringUtils
   */
  private SpringUtils() {
  }

  /**
   * @param propertiesRegisery chargeur du fichier properties
   * @param springXmlFiles chemin des contextes spring
   * @return chargeur de context spring
   * @throws NullSpringFilesExceptions
   * @throws BdfTechnicalException 
   */
  public synchronized static BdfApplicationContext initBdfContext(BdfPropertiesRegistery propertiesRegisery,
      String... springXmlFiles) throws NullSpringFilesExceptions, BdfTechnicalException {
    if (BdfApplicationContext.getInstance() == null) {

      if (springXmlFiles == null) {
        throw new NullSpringFilesExceptions();
      } else {
        BdfApplicationContext.loadProperties(propertiesRegisery, springXmlFiles);
        BdfApplicationContext springApplicationContext = BdfApplicationContext.getInstance();
        return springApplicationContext;
      }
    } else {
      return BdfApplicationContext.getInstance();
    }
  }

  public static BdfApplicationContext initBdfContext(String springXmlFiles, String... propertiesRegisery)
      throws NullSpringFilesExceptions, BdfTechnicalException {
    return SpringUtils.initBdfContext(new BdfPropertiesRegistery(propertiesRegisery), springXmlFiles);
  }

  /**
   * Fonction permettant d'obtenir le premier id rencontré des bean correspondant à une classe
   * 
   * @param className
   * @param context
   * @return
   * @throws BdfTechnicalException
   */
  public static String getFirstSpringBeanName(Class<?> className, BdfApplicationContext context)
      throws BdfTechnicalException {
    String[] beanIdNames = context.getBeanFactory().getBeanNamesForType(className);
    if (beanIdNames.length != 0) {
      return beanIdNames[0];
    } else {
      throw new BdfTechnicalException("No bean referenced for the class : " + className.getName());
    }
  }
}
