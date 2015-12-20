package application.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import application.structure.primary.Aminoacid;
import application.structure.primary.Atom;
import application.structure.primary.Bond;

/**
 * A single polypeptide chain.
 * 
 * Composed of a list of Aminoacid, Atom and Bond objects. Also has a chainID,
 * serial and a Group node.
 * 
 * @author Slav Danchev
 * 
 */
public class Chain {
	private int serial;
	private String chainID;
	private List<Aminoacid> aminoacids;
	private List<Atom> atoms, mainChainAtoms;
	private List<Bond> mainChainBonds, bonds;
	private Group node, chainNode;
	private Map<Integer, Aminoacid> acidMap;
	Point3D position;
	
	private Tooltip tooltip;

	double orgSceneX, orgSceneY;
	double orgTranslateX, orgTranslateY;

	public Chain(ArrayList<Aminoacid> acids) {
		aminoacids = new ArrayList<Aminoacid>();
		atoms = new ArrayList<Atom>();
		node = new Group();
		chainNode = new Group();
		mainChainAtoms = new ArrayList<Atom>();
		mainChainBonds = new ArrayList<Bond>();
		bonds = new ArrayList<Bond>();
		acidMap = new HashMap<Integer, Aminoacid>();

		aminoacids.addAll(acids);
		chainID = acids.get(0).getChainID();

		// get the list of atoms from the Aminoacid objects in the list
		for (Aminoacid a : acids) {
			atoms.addAll(a.getAtoms());
			node.getChildren().add(a.getNode());
			bonds.addAll(a.getBonds());
			acidMap.put(a.getResID(), a);
		}

		makeMainChain();

		// add main chain bonds as children to the group node
		for (Bond b : mainChainBonds) {
			chainNode.getChildren().add(b.getNode());
		}

		node.setCursor(Cursor.HAND);
		node.setOnMousePressed(circleOnMousePressedEventHandler);
		node.setOnMouseDragged(circleOnMouseDraggedEventHandler);
	}

	/**
	 * Creates the bonds in the main C-CA-N-C backbone chain of the polypeptide.
	 * Because in the PDB file format the amino acids are always in the correct
	 * order and parsing is sequential, it is possible to achieve that by
	 * putting all the N, C Alpha and C atoms in a second list. Then iterate
	 * over that, creating a Bond between each n and n + 1 atom in the list.
	 * 
	 * Each Atom is then used to create a Bond list and colour it green, to
	 * distinguish it from the rest of the structure.
	 */
	private void makeMainChain() {
		for (Atom b : atoms) {
			String name = b.getName();
			if (name.equals("N") || name.equals("CA") || name.equals("C"))
				mainChainAtoms.add(b);
		}

		for (int i = 0; i < mainChainAtoms.size() - 1; i++) {
			// use a different coloring for the main chain bonds
			float dist = mainChainAtoms.get(i).distance(
					mainChainAtoms.get(i + 1));
			if (dist < 2f) {
				mainChainBonds.add(new Bond(mainChainAtoms.get(i),
						mainChainAtoms.get(i + 1), Color.WHITE));
			}
		}
	}

	/**
	 * A method that translates the entire chain to a specified position.
	 */
	public void moveTo(Point3D position) {
		node.setTranslateX(position.getX());
		node.setTranslateY(position.getY());
		node.setTranslateZ(position.getZ());

		// for (Bond b : mainChainBonds) {
		// node.getChildren().add(b.getNode());
		// }
	}

	public void colorMainChain(Color color) {
		for (Bond b : mainChainBonds) {
			b.setColor(color);
		}
	}

	public void colorBasedOnSerial() {
		for (Aminoacid aa : aminoacids) {
			double hue = chainID.charAt(0);
			aa.setColor(Color.hsb(hue * 50, 1, 0.5));
		}
	}

	public void resetColor() {
		for (Atom a : atoms) {
			a.resetColor();
		}

		for (Bond b : mainChainBonds) {
			b.resetColor();
		}
	}

	public void setVisible(boolean b) {
		node.setVisible(b);
	}
	
	public void toggleMainChain(boolean visible) {
		chainNode.setVisible(visible);
	}

	/**
	 * Setters and getters.
	 * 
	 */
	public ArrayList<Aminoacid> getAminoacids() {
		return (ArrayList<Aminoacid>) aminoacids;
	}

	public void setAminoacids(ArrayList<Aminoacid> aminoacids) {
		this.aminoacids = aminoacids;
	}

	public void appendAminoacids(ArrayList<Aminoacid> aminoacids) {
		this.aminoacids.addAll(aminoacids);
	}

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public String getChainID() {
		return chainID;
	}

	public void setChainID(String chainID) {
		this.chainID = chainID;
	}

	public Group getNode() {
		return node;
	}

	public ArrayList<Atom> getAtoms() {
		return (ArrayList<Atom>) atoms;
	}

	public List<Bond> getBonds() {
		return bonds;
	}

	public List<Bond> getMainChainBonds() {
		return mainChainBonds;
	}
	
	public Group getMainChainNode() {
		return chainNode;
	}

	public Aminoacid getAcid(int serial) {
		return acidMap.get(serial);
	}

	EventHandler<MouseEvent> circleOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			if (t.isPrimaryButtonDown()) {
				orgSceneX = t.getSceneX();
				orgSceneY = t.getSceneY();
				orgTranslateX = ((Group) (t.getSource())).getTranslateX();
				orgTranslateY = ((Group) (t.getSource())).getTranslateY();
			}
		}
	};
	
	

	EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			if (t.isPrimaryButtonDown()) {
				double offsetX = t.getSceneX() - orgSceneX;
				double offsetY = t.getSceneY() - orgSceneY;
				double newTranslateX = orgTranslateX + offsetX;
				double newTranslateY = orgTranslateY + offsetY;

				((Group) (t.getSource())).setTranslateX(newTranslateX);
				((Group) (t.getSource())).setTranslateY(newTranslateY);

				position = new Point3D(node.getTranslateX(),
						node.getTranslateY(), node.getTranslateZ());
			}
		}
	};
	
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

	
	@Override
	public String toString() {
		String text = "";
		
		text += "+++CHAIN+++";
		text += "\n";
		text += "Serial : " + serial;
		text += "\n";
		text += "ID : " + chainID;
		text += "\n";
		text += "Atoms : " + atoms.size();
		text += "\n";
		text += "Residues : " + aminoacids.size();
		text += "\n";
		
		for(int i = 0 ; i < aminoacids.size(); i++) {
			text += aminoacids.get(i).getName() + " ";
			
			if (i % 10 == 0 && i != 0) text += "\n";
		}
		
		return text; 
	}
}
