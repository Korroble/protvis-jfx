package application.structure.primary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

/**
 * 
 * A single amino acid within a protein structure. Contains a list of Bond and
 * Atom objects that are part of a specific amino acid, a hash map of the atoms,
 * a group node, its type and the ID of the chain it is in.
 * 
 * @author Slav Danchev
 *
 */
public class Aminoacid {
	
	private int resID;
	
	private List<Bond> bonds;
	
	private List<Atom> atoms;

	private HashMap<String, Atom> linkage;

	private Group node;

	private String type, chainID;

	private Tooltip tooltip;

	/**
	 * Instantiates a new aminoacid.
	 *
	 * @param type Name of residue
	 * @param atoms list of atoms that compose the residue
	 */
	public Aminoacid(String type, List<Atom> atoms) {
		this.type = type;
		bonds = new ArrayList<Bond>();
		this.atoms = new ArrayList<Atom>();

		linkage = new HashMap<String, Atom>();
		node = new Group();

		chainID = atoms.get(0).getChainID();
		resID = atoms.get(0).getResID();

		// atoms are stored in a hashmap with their names as keys
		// to make it easier and more computationally efficient
		// to refer to them when building bonds
		for (Atom a : atoms) {
			this.atoms.add(a);
			node.getChildren().add(a.getNode());
			linkage.put(a.getName(), a);
		}

		makeLinks();

		// add bonds as children to the group node
		for (Bond b : bonds) {
			node.getChildren().add(b.getNode());
		}
	}

	/**
	 * Color based on type.
	 */
	public void colorBasedonType() {
		// color based on aminoacid type
		switch (type) {
		case "ASN":
			setColor(Color.WHITE);
			break;

		case "LEU":
			setColor(Color.BLUE);
			break;

		case "TYR":
			setColor(Color.RED);
			break;

		case "ILE":
			setColor(Color.ORANGE);
			break;

		case "GLN":
			setColor(Color.PINK);
			break;

		case "TRP":
			setColor(Color.GREEN);
			break;

		case "LYS":
			setColor(Color.YELLOW);
			break;

		case "GLY":
			setColor(Color.CYAN);
			break;

		case "SER":
			setColor(Color.LIGHTGRAY);
			break;

		case "ARG":
			setColor(Color.BLANCHEDALMOND);
			break;

		case "PRO":
			setColor(Color.BLUEVIOLET);
			break;

		case "ALA":
			setColor(Color.GREENYELLOW);
			break;

		case "CYS":
			setColor(Color.BEIGE);
			break;

		case "GLU":
			setColor(Color.ORANGE);
			break;

		case "HIS":
			setColor(Color.PINK);
			break;

		case "MET":
			setColor(Color.GREEN);
			break;

		case "PHE":
			setColor(Color.YELLOWGREEN);
			break;

		case "THR":
			setColor(Color.BURLYWOOD);
			break;

		case "VAL":
			setColor(Color.CHOCOLATE);
			break;
		}

	}

	/**
	 * Resets to the original color of the residue.
	 */
	public void resetColor() {
		for (Atom a : atoms) {
			a.resetColor();
		}

		for (Bond b : bonds) {
			b.resetColor();
		}
	}

	/**
	 * Sets the color of the residue.
	 *
	 * @param color the new color
	 */
	public void setColor(Color color) {
		for (Atom a : atoms) {
			a.setColor(color);
		}

		for (Bond b : bonds) {
			b.setColor(color);
		}
	}

	/**
	 * Sets whether the residue is visible or not .
	 *
	 * @param b the new visible
	 */
	public void setVisible(boolean b) {
		node.setVisible(b);
	}

	/**
	 * Create bonds between atoms based on aminoacid name.
	 *
	 * Using the RCSB ligand expo website as a reference :
	 * 
	 * http://ligand-expo.rcsb.org/ld-search.html
	 *
	 * Create Bond objects with the Atom objects as arguments, as returned by
	 * the hash map upon entry of atom name (based on above reference).
	 * 
	 * Some bonds are coloured for development and testing purposes.
	 */
	public void makeLinks() {
		bonds.add(new Bond(linkage.get("C"), linkage.get("O")));
		// make bonds that are valid for every amino acid except GLY and PRO
		if (!type.equals("GLY")) {
			bonds.add(new Bond(linkage.get("CA"), linkage.get("HA")));
			bonds.add(new Bond(linkage.get("CA"), linkage.get("CB")));
		}

		if (!type.equals("PRO")) {
			try {
				Atom a = linkage.get("N");
				Atom b = linkage.get("H");
				bonds.add(new Bond(a, b));
			} catch (NullPointerException e) {
				Atom a = linkage.get("N");
				Atom b = linkage.get("H1");
				bonds.add(new Bond(linkage.get("N"), linkage.get("H2")));
				bonds.add(new Bond(linkage.get("N"), linkage.get("H3")));
				bonds.add(new Bond(a, b));
			}
		}

		if (linkage.get("OXT") != null) {
			Atom a = linkage.get("OXT");
			bonds.add(new Bond(a, linkage.get("C")));
		}

		switch (type) {
		case "ASN":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("ND2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("OD1")));

			bonds.add(new Bond(linkage.get("ND2"), linkage.get("HD22")));
			bonds.add(new Bond(linkage.get("ND2"), linkage.get("HD21")));
			break;

		case "TYR":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD1")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD2")));

			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD1")));
			bonds.add(new Bond(linkage.get("CD1"), linkage.get("CE1")));

			bonds.add(new Bond(linkage.get("CD2"), linkage.get("HD2")));
			bonds.add(new Bond(linkage.get("CD2"), linkage.get("CE2")));

			bonds.add(new Bond(linkage.get("CE1"), linkage.get("HE1")));
			bonds.add(new Bond(linkage.get("CE1"), linkage.get("CZ")));

			bonds.add(new Bond(linkage.get("CE2"), linkage.get("HE2")));
			bonds.add(new Bond(linkage.get("CE2"), linkage.get("CZ")));

			bonds.add(new Bond(linkage.get("CZ"), linkage.get("OH")));

			bonds.add(new Bond(linkage.get("OH"), linkage.get("HH")));
			break;

		case "LEU":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD1")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG")));

			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD11")));
			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD12")));
			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD13")));

			bonds.add(new Bond(linkage.get("CD2"), linkage.get("HD21")));
			bonds.add(new Bond(linkage.get("CD2"), linkage.get("HD22")));
			bonds.add(new Bond(linkage.get("CD2"), linkage.get("HD23")));
			break;

		case "ILE":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG1")));

			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG21")));
			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG22")));
			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG23")));

			bonds.add(new Bond(linkage.get("CG1"), linkage.get("HG12")));
			bonds.add(new Bond(linkage.get("CG1"), linkage.get("HG13")));
			bonds.add(new Bond(linkage.get("CG1"), linkage.get("CD1")));

			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD11")));
			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD12")));
			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD13")));
			break;

		case "GLN":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG3")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD")));

			bonds.add(new Bond(linkage.get("CD"), linkage.get("OE1")));
			bonds.add(new Bond(linkage.get("CD"), linkage.get("NE2")));

			bonds.add(new Bond(linkage.get("NE2"), linkage.get("HE21")));
			bonds.add(new Bond(linkage.get("NE2"), linkage.get("HE22")));
			break;

		case "TRP":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD1")));

			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD1")));
			bonds.add(new Bond(linkage.get("CD1"), linkage.get("NE1")));

			bonds.add(new Bond(linkage.get("NE1"), linkage.get("HE1")));
			bonds.add(new Bond(linkage.get("NE1"), linkage.get("CE2")));

			bonds.add(new Bond(linkage.get("CE2"), linkage.get("CZ2")));
			bonds.add(new Bond(linkage.get("CE2"), linkage.get("CD2")));

			bonds.add(new Bond(linkage.get("CZ2"), linkage.get("HZ2")));
			bonds.add(new Bond(linkage.get("CZ2"), linkage.get("CH2")));

			bonds.add(new Bond(linkage.get("CH2"), linkage.get("HH2")));
			bonds.add(new Bond(linkage.get("CH2"), linkage.get("CZ3")));

			bonds.add(new Bond(linkage.get("CZ3"), linkage.get("HZ3")));
			bonds.add(new Bond(linkage.get("CZ3"), linkage.get("CE3")));

			bonds.add(new Bond(linkage.get("CE3"), linkage.get("HE3")));
			bonds.add(new Bond(linkage.get("CE3"), linkage.get("CD2")));
			break;

		case "LYS":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG3")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD")));

			bonds.add(new Bond(linkage.get("CD"), linkage.get("HD2")));
			bonds.add(new Bond(linkage.get("CD"), linkage.get("HD3")));
			bonds.add(new Bond(linkage.get("CD"), linkage.get("CE")));

			bonds.add(new Bond(linkage.get("CE"), linkage.get("HE2")));
			bonds.add(new Bond(linkage.get("CE"), linkage.get("HE3")));
			bonds.add(new Bond(linkage.get("CE"), linkage.get("NZ")));

			bonds.add(new Bond(linkage.get("NZ"), linkage.get("HZ1")));
			bonds.add(new Bond(linkage.get("NZ"), linkage.get("HZ2")));
			bonds.add(new Bond(linkage.get("NZ"), linkage.get("HZ3")));
			break;

		case "ASP":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("OD1")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("OD2")));
			break;

		case "GLY":
			bonds.add(new Bond(linkage.get("CA"), linkage.get("HA2")));
			bonds.add(new Bond(linkage.get("CA"), linkage.get("HA3")));
			break;

		case "PRO":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG3")));

			bonds.add(new Bond(linkage.get("CD"), linkage.get("N")));
			bonds.add(new Bond(linkage.get("CD"), linkage.get("HD2")));
			bonds.add(new Bond(linkage.get("CD"), linkage.get("HD3")));
			break;

		case "SER":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("OG")));
			bonds.add(new Bond(linkage.get("OG"), linkage.get("HG")));
			break;

		case "ARG":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG3")));

			bonds.add(new Bond(linkage.get("CD"), linkage.get("HD2")));
			bonds.add(new Bond(linkage.get("CD"), linkage.get("HD3")));
			bonds.add(new Bond(linkage.get("CD"), linkage.get("NE")));

			bonds.add(new Bond(linkage.get("NE"), linkage.get("HE")));
			bonds.add(new Bond(linkage.get("NE"), linkage.get("CZ")));

			bonds.add(new Bond(linkage.get("CZ"), linkage.get("NH1")));
			bonds.add(new Bond(linkage.get("CZ"), linkage.get("NH2")));

			bonds.add(new Bond(linkage.get("NH2"), linkage.get("HH21")));
			bonds.add(new Bond(linkage.get("NH2"), linkage.get("HH22")));

			bonds.add(new Bond(linkage.get("NH1"), linkage.get("HH11")));
			bonds.add(new Bond(linkage.get("NH1"), linkage.get("HH12")));
			break;

		case "ALA":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB1")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			break;

		case "CYS":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("SG")));

			bonds.add(new Bond(linkage.get("SG"), linkage.get("HG")));
			break;

		case "GLU":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG3")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD")));

			bonds.add(new Bond(linkage.get("CD"), linkage.get("OE1")));
			bonds.add(new Bond(linkage.get("CD"), linkage.get("OE2")));

			bonds.add(new Bond(linkage.get("OE2"), linkage.get("HE2")));
			break;

		case "HIS":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("ND1")));

			bonds.add(new Bond(linkage.get("CD2"), linkage.get("HD2")));
			bonds.add(new Bond(linkage.get("CD2"), linkage.get("NE2")));

			bonds.add(new Bond(linkage.get("NE2"), linkage.get("HE2")));
			bonds.add(new Bond(linkage.get("NE2"), linkage.get("CE1")));

			bonds.add(new Bond(linkage.get("CE1"), linkage.get("HE1")));
			bonds.add(new Bond(linkage.get("CE1"), linkage.get("ND1")));

			bonds.add(new Bond(linkage.get("ND1"), linkage.get("HD1")));
			break;

		case "MET":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG3")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("SD")));

			bonds.add(new Bond(linkage.get("SD"), linkage.get("CE")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG2")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("HG3")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("SD")));

			bonds.add(new Bond(linkage.get("CE"), linkage.get("HE1")));
			bonds.add(new Bond(linkage.get("CE"), linkage.get("HE2")));
			bonds.add(new Bond(linkage.get("CE"), linkage.get("HE3")));
			break;

		case "PHE":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB2")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB3")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG")));

			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD1")));
			bonds.add(new Bond(linkage.get("CG"), linkage.get("CD2")));

			bonds.add(new Bond(linkage.get("CD1"), linkage.get("HD1")));
			bonds.add(new Bond(linkage.get("CD1"), linkage.get("CE1")));

			bonds.add(new Bond(linkage.get("CE1"), linkage.get("HE1")));
			bonds.add(new Bond(linkage.get("CE1"), linkage.get("CZ")));

			bonds.add(new Bond(linkage.get("CZ"), linkage.get("HZ")));
			bonds.add(new Bond(linkage.get("CZ"), linkage.get("CE2")));

			bonds.add(new Bond(linkage.get("CE2"), linkage.get("HE2")));
			bonds.add(new Bond(linkage.get("CE2"), linkage.get("CD2")));

			bonds.add(new Bond(linkage.get("CD2"), linkage.get("HD2")));
			break;

		case "THR":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("OG1")));
			bonds.add(new Bond(linkage.get("CB"), linkage.get("CG2")));

			bonds.add(new Bond(linkage.get("OG1"), linkage.get("HG1")));

			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG21")));
			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG22")));
			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG23")));
			break;

		case "VAL":
			bonds.add(new Bond(linkage.get("CB"), linkage.get("HB")));

			bonds.add(new Bond(linkage.get("CG1"), linkage.get("HG11")));
			bonds.add(new Bond(linkage.get("CG1"), linkage.get("HG12")));
			bonds.add(new Bond(linkage.get("CG1"), linkage.get("HG13")));

			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG21")));
			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG22")));
			bonds.add(new Bond(linkage.get("CG2"), linkage.get("HG23")));
			break;
		}
	}

	/**
	 * Sets the selection scope.
	 *
	 * @param selected the new scope
	 */
	public void setScope(boolean selected) {
		if (selected) {
			node.setOnMouseMoved((event) -> {
				tooltip = new Tooltip();
				tooltip.setText(toString());

				Tooltip.install(node, tooltip);
				
				
			});
			

		} else {
			node.setOnMouseMoved(null);
			Tooltip.uninstall(node, tooltip);
		}
	}


	public ArrayList<Bond> getBonds() {
		return (ArrayList<Bond>) bonds;
	}


	public ArrayList<Atom> getAtoms() {
		return (ArrayList<Atom>) atoms;
	}


	public Atom getAtom(String name) {
		return linkage.get(name);
	}


	public HashMap<String, Atom> getLinkage() {
		return linkage;
	}


	public Group getNode() {
		return node;
	}


	public String getChainID() {
		return chainID;
	}


	public int getResID() {
		return resID;
	}
	

	public String getName(){
		return type;
	}

	/*
	 * 
	 * toString() method for this class
	 */
	@Override
	public String toString() {
		String text = "";

		text += "+++AMINO ACID+++";
		text += "\n";
		text += "resID : " + resID;
		text += "\n";
		text += "Name : " + type;
		text += "\n";
		text += "Chain : " + chainID;
		text += "\n";
		text += "Atoms : " + atoms.size();
		text += "\n";
		
		for(int i = 0 ; i < atoms.size(); i++) {
			text += atoms.get(i).getName() + " ";
			
			if (i % 10 == 0 && i != 0) text += "\n";
		}

		return text;
	}
}
