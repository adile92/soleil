package main;

//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import controller.MainController;

import appender.GuiAppender;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jms.BrokerLauncher;

public class App extends Application {
	
	private static Logger logger = Logger.getLogger(App.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(App.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
        	
        	BrokerLauncher.getBokerLauncher();
            
            AnchorPane page = (AnchorPane) FXMLLoader.load(App.class.getResource("IssueTrackingLite.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Esiag CSP - Camara/Idoumajoud");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.ERROR, null, ex);
        }
    }
}

