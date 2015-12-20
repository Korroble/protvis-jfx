package application;

import java.io.File;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;

import application.structure.Chain;
import application.structure.Model;
/**
 * Creates a JavaFX SubScene, adds the rendered protein Model to itself and 
 * then configures the camera and mouse/keyboard input detection. 
 * * Online tutorial used as a reference for initial setup of the 3D scene, camera
 * and input handling:
 * https://docs.oracle.com/javase/8/javafx/graphics-tutorial/
 * sampleapp3d.htm#CJAHFAF
 * 
 * The class Xform in this project is taken from the same web resource. It is an
 * extension of Group and provides a set of handy 3D transformation functions.
 * @author Slav
 *
 */
public class ProteinScene extends SubScene {
	/*
	 * various constants used when creatign the camera 
	 */
	private static final double CAMERA_INITIAL_DISTANCE = -50;
	private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
	private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
	private static final double CAMERA_NEAR_CLIP = 0.1;
	private static final double CAMERA_FAR_CLIP = 10000.0;
	private static final double CONTROL_MULTIPLIER = 0.1;
	private static final double SHIFT_MULTIPLIER = 10.0;
	private static final double MOUSE_SPEED = 0.1;
	private static final double ROTATION_SPEED = 2.0;
	private static final double TRACK_SPEED = 0.3;

	double mousePosX;
	double mousePosY;
	double mouseOldX;
	double mouseOldY;
	double mouseDeltaX;
	double mouseDeltaY;

	final Group root = new Group();
	static Xform world;
	final PerspectiveCamera camera = new PerspectiveCamera(true);
	final Xform cameraXform = new Xform();
	final Xform cameraXform2 = new Xform();
	final Xform cameraXform3 = new Xform();
	private List<Chain> chains;
	private Model model;

	public ProteinScene(Model model) {
		super(world = new Xform(model), 100, 100, true,
				SceneAntialiasing.BALANCED);

		this.model = model;

		buildCamera();

		handleKeyboard(this, world);
		handleMouse(this, world);

		setFill(Color.BLACK);
		autosize();
		setCamera(camera);
	}

	/**
	 * Takes a snapshot of the subscene at a moment in time 
	 * and saves it to the specified file. 
	 * @param file
	 */
	public void takeScreenshot(File file) {
		WritableImage wim = new WritableImage(1080, 720);
		SnapshotParameters param = new SnapshotParameters(); 
		param.setDepthBuffer(true);
        param.setFill(Color.CORNSILK);


		this.snapshot(param, wim);
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
		} catch (Exception s) {
		}
	}

	/*
	 * Keyboard input detection. 
	 */
	private void handleKeyboard(SubScene scene, final Node root) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case Z:
					cameraXform2.setTranslateX(0.0);
					cameraXform2.setTranslateY(0.0);
					camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
					cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
					cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
					break;
				
				}
			}
		});
	}

	/**
	 * Mouse input detection.
	 */
	private void handleMouse(SubScene scene, final Node root) {
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);

				double modifier = 1.0;

				if (me.isControlDown()) {
					modifier = CONTROL_MULTIPLIER;
				}
				if (me.isPrimaryButtonDown() && me.isShiftDown()) {
					// cameraXform.ry.setAngle(cameraXform.ry.getAngle()
					// - mouseDeltaX * MOUSE_SPEED * modifier
					// * ROTATION_SPEED);
					// cameraXform.rx.setAngle(cameraXform.rx.getAngle()
					// - mouseDeltaY * MOUSE_SPEED * modifier
					// * ROTATION_SPEED);
				} else if (me.isSecondaryButtonDown()) {
					// double z = camera.getTranslateZ();
					// double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
					// camera.setTranslateZ(newZ);
					cameraXform.ry.setAngle(cameraXform.ry.getAngle()
							- mouseDeltaX * MOUSE_SPEED * modifier
							* ROTATION_SPEED);
					cameraXform.rx.setAngle(cameraXform.rx.getAngle()
							- mouseDeltaY * MOUSE_SPEED * modifier
							* ROTATION_SPEED);
				} else if (me.isMiddleButtonDown()) {
					cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX
							* MOUSE_SPEED * modifier * TRACK_SPEED);
					cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY
							* MOUSE_SPEED * modifier * TRACK_SPEED);
				}
			}
		});

		scene.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent se) {
				double tmp = camera.getTranslateZ() + (int) se.getDeltaY() / 40;
				camera.setTranslateZ(tmp);
			}
		});
	}

	/**
	 * Create, place and configure camera.
	 */
	private void buildCamera() {
		root.getChildren().add(cameraXform);
		cameraXform.getChildren().add(cameraXform2);
		cameraXform2.getChildren().add(cameraXform3);
		cameraXform3.getChildren().add(camera);

		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
		cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
		cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
	}

	public Model getModel() {
		return model;
	}
}
