package org.esiag.isidis.bdf.commons.initializer.exceptions;

/**
 * Classe d'exception du fichier context spring inexistant.
 * @author Damien Brochon
 * @version 1.0
 */
public class NullSpringFilesExceptions extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 7407706692205113994L;

  /**
   * Constructeur de NullSpringFilesExceptions
   */
  public NullSpringFilesExceptions() {
    super();
  }

  /**
   * Constructeur de NullSpringFilesExceptions
   * @param arg0 message
   * @param arg1 exception
   */
  public NullSpringFilesExceptions(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * Constructeur de NullSpringFilesExceptions
   * @param arg0 message
   */
  public NullSpringFilesExceptions(String arg0) {
    super(arg0);
  }

  /**
   * Constructeur de NullSpringFilesExceptions
   * @param arg0 exception
   */
  public NullSpringFilesExceptions(Throwable arg0) {
    super(arg0);
  }

}
