package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import application.downloader.PDBDownloader;
import application.structure.Model;
import application.view.Controller;

/**
 * 
 * This is the main class from which the application is started. It loads the view from 
 * an fxml file and passes it on to the controller. 
 * 
 
 * 
 * @author Slav Danchev
 * 
 */

public class Main extends Application {

	private static Model model;

	private Pane rootLayout;
	private Stage primaryStage;
	private Controller s1c;

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;		

			FXMLLoader loader = new FXMLLoader(
					Main.class.getResource("view/view.fxml"));
			rootLayout = loader.load();

			s1c = loader.getController();
			s1c.setMainApp(this);

			Scene scene = new Scene(rootLayout);

			primaryStage.setTitle("ProtVis");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
			launch(args);
		}

	public Model getModel() {
		return model;
	}
	
	public static void setModel(Model mod) {
		model = mod;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
}
