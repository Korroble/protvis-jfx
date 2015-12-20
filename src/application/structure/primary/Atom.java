package application.structure.primary;

import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

/**
 * A single atom in the 3D structure. Each Atom has its own position,
 * represented as a 3D vector (Point3D in JavaFX), size, colour, serial number,
 * indication of amino acid or chain it belongs to.
 * 
 * 
 * @author Slav Danchev
 *
 */
public class Atom implements EventHandler {
	private Point3D position;
	private Color color;
	private String name, chainID;
	private String element; 
	private int serial, resID;
	private String residue;
	private float size;
	private Sphere ball;
	private PhongMaterial material;
	private List<Bond> bonds;
	private Group node;
	private Tooltip tooltip;  

	double orgSceneX, orgSceneY;
	double orgTranslateX, orgTranslateY;

	public Atom(Point3D position, String name, String element, int serial, String residue,
			int resID, String chainID) {
		this.position = position;
		this.name = name;
		this.element = element;
		this.serial = serial;
		this.residue = residue;
		this.resID = resID;
		this.chainID = chainID;
		
		node = new Group();
		
		bonds = new ArrayList<Bond>();

		// Color atoms according to CPK convention
		//sizes: http://chemwiki.ucdavis.edu/@api/deki/files/13626/PTradii.png
		switch (element) {
		case "H":
			size = 0.37f / 2;
			color = Color.WHITE;
			break;

		case "C":
			size = 0.77f / 2;
			color = Color.GRAY;
			break;

		case "O":
			size = 0.73f / 2;
			color = Color.RED;
			break;

		case "N":
			size = 0.74f / 2;
			color = Color.BLUE;
			break;

		case "S":
			size = 1.03f / 2;
			color = Color.YELLOW;
			break;
			
		case "FE":
			size = 1.32f / 2;
			color = Color.BROWN;
			break;
			
		case "F":
			size = 0.57f / 2;
			color = Color.GREEN;
			break;
			
		case "CL":
			size = 1.03f / 2;
			color = Color.GREEN;
			break;

		default:
			size = 0.37f / 2;
			color = Color.WHITE;
			break;
		}

		// create, set appearance and material
		material = new PhongMaterial();
		material.setDiffuseColor(color);
		material.setSpecularPower(0.1);
		material.setSpecularColor(color);

		// create the ball, set size and appearance
		// and place at set position
		ball = new Sphere(size, 20);
		ball.setMaterial(material);
		ball.setTranslateX(position.getX());
		ball.setTranslateY(position.getY());
		ball.setTranslateZ(position.getZ());
		
		ball.setVisible(true);

		node.getChildren().add(ball);
		// System.out.println("Create atom: " + name + "| Serial: " + serial);
	}

	/**
	 * Measure the distance between this Atom and the one in the argument.
	 */
	public float distance(Atom a) {
		float dist = (float) position.distance(a.getPosition());
		return dist;
	}

	/**
	 * Resets the atom to its original CPK color. 
	 */
	public void resetColor() {
		material = new PhongMaterial();
		material.setDiffuseColor(color);
		material.setSpecularPower(0.1);
		material.setSpecularColor(color);
		
		ball.setMaterial(material);
	}
	
	/**
	 * If true sets the cursor to display 
	 * information about Atoms. 
	 * 
	 */
	public void setScope(boolean selected) {
		if (selected) {
			node.setOnMouseMoved((event) -> {
				tooltip = new Tooltip();
				tooltip.setStyle("-fx-background: lightblue;");
				tooltip.setText(toString());

				Tooltip.install(node, tooltip);
			});

		} else {
			node.setOnMouseMoved(null);
			Tooltip.uninstall(node, tooltip);
		}
	}

	
	/**
	 * setters and getters 
	 */
	
	public void setColor(Color color) {	
		material = new PhongMaterial();
		material.setDiffuseColor(color);
		material.setSpecularPower(0.1);
		material.setSpecularColor(color);
		
		ball.setMaterial(material);
		
	}
	
	public void setSize(float size) {
		ball.setRadius(size);
	}
	
	public void setVisible(boolean b) {
		ball.setVisible(b);
	}
	
	public Group getNode() {
		return node;
	}

	public Point3D getPosition() {
		return position;
	}

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public String getResidue() {
		return residue;
	}

	public void setResidue(String chainID) {
		this.residue = chainID;
	}

	public String getName() {
		return name;
	}

	public int getResID() {
		return resID;
	}

	public String getChainID() {
		return chainID;
	}

	public void setChainID(String chainID) {
		this.chainID = chainID;
	}

	public void addBond(Bond b) {
		bonds.add(b);
	}

	public ArrayList<Bond> getBonds() {
		return (ArrayList<Bond>) bonds;
	}
	
	public float getSize() {
		return size;
	}
	
	public Color getColor() { 
		return this.color; 
	}
	
	@Override
	public String toString() {
		String text = "";

		text += "+++ATOM+++";
		text += "\n";
		text += "serial : " + serial;
		text += "\n";
		text += "Name : " + name;
		text += "\n";
		text += "Chain : " + chainID;
		text += "\n";
		text += "Residue : " + residue;
		text += "\n";
		text += "resID : " + resID;
		text += "\n";

		return text;
	}

	@Override
	public void handle(Event arg0) {
	}

}
