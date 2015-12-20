package application;

import java.io.File;
import java.io.IOException;

import application.downloader.PDBDownloader;
import application.structure.Model;
import application.view.Controller;


/**
 * Downloads, parses and creates a new protein model in a separate thread.
 * 
 * @author Slav Danchev
 *
 */
public class ProteinMaker implements Runnable {
	
	/** The world. */
	private Xform world;
	
	/** The downloader. */
	private PDBDownloader downloader;
	
	/** The id. */
	private String id;
	
	/** The controller. */
	private Controller controller;
	
	/** The main. */
	private Main main;
	
	/** The file. */
	private File file = null;
	
	/** The random. */
	private boolean random = false; 

	/**
	 * Instantiates a new protein maker.
	 *
	 * @param id PDB id
	 * @param cont the cont
	 * @param main the main
	 */
	public ProteinMaker(String id, Controller cont, Main main) {
		this.id = id;
		controller = cont;
		this.main = main;
	}

	/**
	 * Instantiates a new protein maker.
	 *
	 * @param file local PDB file
	 * @param cont the cont
	 * @param main the main
	 */
	public ProteinMaker(File file, Controller cont, Main main) {
		this.file = file;
		controller = cont;
		this.main = main;
	}
	
	/**
	 * Instantiates a new protein maker.
	 *
	 * @param cont the Controller
	 * @param main the Main class
	 */
	public ProteinMaker(Controller cont, Main main) {
		controller = cont;
		this.main = main;
		random = true; 
	}

	/**
	 * Load model from local PDB file.
	 */
	private void loadFromFile() {
		controller.setProgressStatus("Loading...");

		world = new Xform();

		controller.setProgressStatus("Parsing file...");

		Parser myParse = new Parser(file);

		controller.setProgressStatus("Rendering...");
		Model model = myParse.getModel();

		world.getChildren().add(model.getNode());

		main.setModel(model);

		ProteinScene protScene = new ProteinScene(model);

		controller.setSubScene(protScene);
		controller.setProgressStatus("");
	}

	/**
	 * Retrieve a random protein.
	 */
	public void retrieveRandomProtein() {
		world = new Xform();
		try {
			downloader = new PDBDownloader();
			String id = downloader.downloadRandom();
			
			controller.setProgressStatus("Parsing file...");
			Parser myParse = new Parser(id + ".pdb");

			controller.setProgressStatus("Rendering...");
			Model model = myParse.getModel();

			world.getChildren().add(model.getNode());

			Main.setModel(model);

			ProteinScene protScene = new ProteinScene(model);

			controller.setSubScene(protScene);
			controller.setProgressStatus("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Retrieve protein from id.
	 *
	 * @param id the id
	 */
	private void retrieveProtein(String id) {
		id = id.toUpperCase();

		controller.setProgressStatus("Loading...");

		world = new Xform();
		downloader = new PDBDownloader(id);

		try {
			if (!(new File(id + ".pdb").exists())) {
				// Controller.setProgressStatus("Downloading...");
				downloader.download(id);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (new File(id + ".pdb").exists()) {
			controller.setProgressStatus("Parsing file...");
			Parser myParse = new Parser(id + ".pdb");

			controller.setProgressStatus("Rendering...");
			Model model = myParse.getModel();

			world.getChildren().add(model.getNode());

			Main.setModel(model);

			ProteinScene protScene = new ProteinScene(model);

			controller.setSubScene(protScene);
			controller.setProgressStatus("");
			
			controller.setMetaInformation(model.getMetaInfo());
		}
	}

	
	@Override
	public void run() {
		if (random) {
			retrieveRandomProtein();
		}
		else if (file != null)
			loadFromFile();
		else
			retrieveProtein(this.id);
	}

}
