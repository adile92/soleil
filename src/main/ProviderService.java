package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import edu.esiag.isidis.security.provider.MyProvider;

public class ProviderService {

	/**
	 * retourne la liste des algo dispo
	 * 
	 * @return
	 */
	public static String[] algo() {

		List<String> list = new ArrayList<>();
		list.add("MD5");
		list.add("SHA");
		list.add("SHA-1");

		return list.toArray(new String[list.size()]);

	}

	public static String[] typeCles() {
		List<String> list = new ArrayList<>();
		for (TypeCle cle : TypeCle.values()) {
			list.add(cle.name());
		}
		return list.toArray(new String[list.size()]);

	}

	public static String[] cleAlgoGeneration() {

		List<String> list = new ArrayList<>();
		for (SymetricKey cle : SymetricKey.values()) {
			list.add(cle.name());
		}

		return list.toArray(new String[list.size()]);

	}

	public static Task performMessageDigest(final String algo, final File file,
			final String cheminEmpreinte) {
		return new Task() {
			@Override
			protected Object call() throws Exception {
				MyProvider provider = new MyProvider();
				MessageDigest md = null;
				StringBuffer buff = new StringBuffer();
				try {
					FileInputStream fis = new FileInputStream(file);

					int tailleContent = fis.available();

					md = provider.getMessageDigest(algo);

					int content;

					while ((content = fis.read()) != -1) {
						buff.append((char) content);
						updateProgress(buff.length(), tailleContent);
					}

					String empreinte = new String(md.digest(buff.toString()
							.getBytes()));
					FileOutputStream fos = new FileOutputStream(new File(
							cheminEmpreinte));
					fos.write(empreinte.getBytes());

				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}
		};
	}

}
