package org.esiag.isidis.bdf.commons.jms;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Classe permettant de recuperer les adresses IP (locale ou publique).
 * @author Mahamadou Camara
 * @version 1.0
 */
public final class IpAdresse {

  /**
   * Constructeur prive empechant de creer une instance de IpAdresse.
   */
  private IpAdresse() {
  }

  /**
   * Renvoi l'adresse IP locale.
   * @return Un String representant l'adresse IP locale
   * @throws UnknownHostException
   */
  public static String getIpAdresse() throws UnknownHostException {
    String hostname = InetAddress.getLocalHost().toString();
    StringTokenizer st = new StringTokenizer(hostname, "/");
    st.nextToken();
    String ip = st.nextToken();
    return ip;
  }

  /**
   * Renvoi l'adresse IP publique.
   * @return Un String representant l'adresse IP publique
   * @throws Exception
   */
  public static String getIpAdressePublic() throws Exception {
    String site = "http://votreip.free.fr/";
    String prefixe = "<title>IP : ";
    String suffixe = "</title>";
    Scanner sc = new Scanner(new URL(site).openStream());

    while (sc.hasNextLine()) {
      String line = sc.nextLine();

      int a = line.indexOf(prefixe);
      if (a != -1) {
        int b = line.indexOf(suffixe, a);
        if (b != -1) {
          sc.close();
          return line.substring(a + prefixe.length(), b);
        }
      }
    }

    sc.close();
    return null;
  }
}
