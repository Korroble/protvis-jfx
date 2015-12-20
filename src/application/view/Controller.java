package application.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import application.Main;
import application.ProteinMaker;
import application.ProteinScene;
import application.ScopeType;
import application.structure.ColorMode;
import application.structure.VisMode;

/**
 * The controller class of the application.
 * 
 * @author Slav Danchev
 *
 */

public class Controller implements Initializable {

	/** The main app. */
	private Main mainApp;

	/** The protein scene. */
	private ProteinScene proteinScene;

	/** The anchor pane. */
	@FXML
	AnchorPane anchorPane;

	/** The pdb id. */
	@FXML
	TextField pdbId;

	/** The mode select. */
	@FXML
	ComboBox<String> modeSelect;

	/** The colour select. */
	@FXML
	ComboBox<String> colourSelect;

	/** The save image. */
	@FXML
	Button saveImage;

	/** The o k. */
	@FXML
	Button oK;

	/** The load file. */
	@FXML
	Button loadFile;

	/** The random. */
	@FXML
	Button random;

	/** The load bar. */
	@FXML
	ProgressIndicator loadBar;

	/** The progress status. */
	@FXML
	Text progressStatus;

	/** The radio atom. */
	@FXML
	RadioButton radioAtom;

	/** The radio aminoacid. */
	@FXML
	RadioButton radioAminoacid;

	/** The radio chain. */
	@FXML
	RadioButton radioChain;

	/** The meta text. */
	@FXML
	Text metaText;

	/** The atoms box. */
	@FXML
	CheckBox atomsBox;

	/** The bonds box. */
	@FXML
	CheckBox bondsBox;

	/** The backbone box. */
	@FXML
	CheckBox backboneBox;

	@FXML
	CheckBox hetAtmBox;

	/**
	 * Static variables for view elements that need to be accessed from outside.
	 * (e.g. loading indicators) This is done as a workaround for a not yet
	 * fixed bug in javafx where static gui elements are not initialized.
	 */
	static ProgressIndicator loadBarS;

	/** The anchor pane s. */
	static AnchorPane anchorPaneS;

	/** The progress status s. */
	static Text progressStatusS;

	/** The meta text s. */
	static Text metaTextS;

	/** The scope radio group. */
	private ToggleGroup scopeRadioGroup;

	/**
	 * Called automatically once the controller has been loaded. Initializes all
	 * the event handlers for the main view screen.
	 *
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadBarS = loadBar;
		anchorPaneS = anchorPane;
		progressStatusS = progressStatus;
		metaTextS = metaText;

		scopeRadioGroup = new ToggleGroup();
		radioAtom.setToggleGroup(scopeRadioGroup);
		radioAtom.setSelected(true);
		radioAminoacid.setToggleGroup(scopeRadioGroup);
		radioChain.setToggleGroup(scopeRadioGroup);

		modeSelect.getItems().addAll("Spheres", "Ball and Stick", "Sticks",
				"Secondary", "Backbone");
		colourSelect.getItems().addAll("Atoms", "Aminoacids",
				"Secondary Structre", "Chains");

		modeSelect.setOnAction((event) -> {
			int selection = modeSelect.getSelectionModel().getSelectedIndex();

			switch (selection) {
			case 0:
				mainApp.getModel().setVisMode(VisMode.SPHERES);
				break;

			case 1:
				mainApp.getModel().setVisMode(VisMode.BALLANDSTICK);
				break;

			case 2:
				mainApp.getModel().setVisMode(VisMode.STICKS);
				break;

			case 3:
				mainApp.getModel().setVisMode(VisMode.SECONDARY);
				break;

			case 4:
				mainApp.getModel().setVisMode(VisMode.BACKBONE);
				break;
			}

		});

		colourSelect
				.setOnAction((event) -> {
					int selection = colourSelect.getSelectionModel()
							.getSelectedIndex();

					switch (selection) {
					case 0:
						mainApp.getModel().setColorMode(ColorMode.ATOMS);
						break;

					case 1:
						mainApp.getModel().setColorMode(ColorMode.AMINOACIDS);
						break;

					case 2:
						mainApp.getModel().setColorMode(ColorMode.SECONDARY);
						break;

					case 3:
						mainApp.getModel().setColorMode(ColorMode.CHAINS);
						break;
					}
				});

		saveImage.setOnAction((event) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Image");
			File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

			proteinScene.takeScreenshot(file);
		});

		oK.setOnAction((event) -> {
			progressStatus.setText("Loading...");

			Platform.runLater(new ProteinMaker(pdbId.getText(), this, mainApp));
		});

		random.setOnAction((event) -> {
			progressStatus.setText("Loading...");
			Platform.runLater(new ProteinMaker(this, mainApp));
		});

		loadFile.setOnAction((event) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Image");
			File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
			progressStatus.setText("Loading...");
			Platform.runLater(new ProteinMaker(file, this, mainApp));
		});

		radioAtom.setOnAction((event) -> {
			mainApp.getModel().setCursorScope(ScopeType.ATOM);
		});

		radioAminoacid.setOnAction((event) -> {
			mainApp.getModel().setCursorScope(ScopeType.AMINOACID);
		});

		atomsBox.setOnAction((event) -> {
			if (atomsBox.isSelected()) {
				mainApp.getModel().setAtomsVisibility(true);
			} else {
				mainApp.getModel().setAtomsVisibility(false);
			}
		});

		bondsBox.setOnAction((event) -> {
			if (bondsBox.isSelected()) {
				mainApp.getModel().setBondsVisibility(true);
			} else {
				mainApp.getModel().setBondsVisibility(false);
			}
		});

		backboneBox.setOnAction((event) -> {
			if (backboneBox.isSelected()) {
				mainApp.getModel().setMainChainVisibility(true);
			} else {
				mainApp.getModel().setMainChainVisibility(false);
			}
		});

		hetAtmBox.setOnAction((event) -> {
			if (hetAtmBox.isSelected()) {
				mainApp.getModel().setHetAtomVisibility(true);
			} else {
				mainApp.getModel().setHetAtomVisibility(false);
			}
		});

		radioChain.setOnAction((event) -> {
			mainApp.getModel().setCursorScope(ScopeType.CHAIN);
		});
	}

	/**
	 * Sets the sub scene.
	 *
	 * @param subSc
	 *            the new sub scene
	 */
	public void setSubScene(ProteinScene subSc) {
		subSc.heightProperty().bind(anchorPaneS.heightProperty());
		subSc.widthProperty().bind(anchorPaneS.widthProperty());

		anchorPaneS.getChildren().add(subSc);

		proteinScene = subSc;
	}

	/**
	 * Sets the progress status of the protein downloading and rendering
	 * process.
	 *
	 * @param status
	 *            to display
	 */
	public void setProgressStatus(String status) {
		progressStatus.setText(status);
	}

	/**
	 * Sets the meta information about the protein that is displayed in the
	 * bottom panel.
	 *
	 * @param meta
	 *            The information.
	 */
	public void setMetaInformation(String meta) {
		metaText.setText(meta);
	}

	/**
	 * Show pop up containing additional information about the hovered part of
	 * the protein.
	 *
	 * @param text
	 *            the text to display
	 */
	public static void showPopUp(String text) {
		final Stage myDialog = new Stage();
		myDialog.initModality(Modality.WINDOW_MODAL);

		Button okButton = new Button("CLOSE");
		okButton.setOnAction((event) -> {
			myDialog.close();
		});

		Scene myDialogScene = new Scene(VBoxBuilder.create()
				.children(new Text(text), okButton).alignment(Pos.CENTER)
				.padding(new Insets(10)).build());

		myDialog.setScene(myDialogScene);
		myDialog.show();
	}

	/**
	 * Binds the Main class with the Controller.
	 *
	 */
	public void setMainApp(Main app) {
		mainApp = app;
	}
}
