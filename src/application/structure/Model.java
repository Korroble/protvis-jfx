package application.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import application.ScopeType;
import application.structure.primary.Aminoacid;
import application.structure.primary.Atom;
import application.structure.primary.Bond;
import application.structure.secondary.Helix;
import application.structure.secondary.Sheet;

/**
 * A single protein structure.
 * 
 * Defined by a list of Chain objects and a list of other atoms, external to
 * polypeptide chains. Calculates the most external atoms to the structure for
 * both sides of each axis.
 * 
 * @author Slav Danchev
 *
 */

public class Model {
	

	private List<Helix> helices;

	private List<Sheet> sheets;

	private List<Chain> chains;

	private List<Atom> hetatoms, atoms;

	private List<Aminoacid> acids;

	private List<Bond> bonds, mainChain, explicit;

	private Group node;

	private Map<Integer, Atom> atomMap;

	private Map<Integer, Aminoacid> acidMap;

	private Map<String, Chain> chainMap;

	private String metaInfo; 

	/**
	 * Instantiates a new model.
	 *
	 * @param chains list of chains that compose the structure 
	 */
	public Model(ArrayList<Chain> chains) {
		this.chains = chains;
		node = new Group();

		acids = new ArrayList<Aminoacid>();
		atoms = new ArrayList<Atom>();
		bonds = new ArrayList<Bond>();
		mainChain = new ArrayList<Bond>();

		chainMap = new HashMap<String, Chain>();

		for (Chain c : chains) {
			node.getChildren().addAll(c.getNode());
			node.getChildren().addAll(c.getMainChainNode());

			chainMap.put(c.getChainID(), c);
			acids.addAll(c.getAminoacids());
			atoms.addAll(c.getAtoms());
			bonds.addAll(c.getBonds());
			mainChain.addAll(c.getMainChainBonds());
		}

		// map atoms and amino acids to their serial numbers
		atomMap = new HashMap<Integer, Atom>();
		acidMap = new HashMap<Integer, Aminoacid>();

		for (Aminoacid aa : acids) {
			acidMap.put(aa.getResID(), aa);
		}

		for (Atom a : atoms) {
			atomMap.put(a.getSerial(), a);
		}

		// finally position structure around center
		center();

		// set the cursor scope setting to be ATOM by default
		setCursorScope(ScopeType.ATOM);
	}

	/**
	 * Calculated the distance vector between the zero vector of the 3D scene
	 * and the atom that's the furthest away from the center. Then moves the
	 * entire structure halfway on that vector in order to make it visible on
	 * the screen.
	 */
	private void center() {
		Point3D furthest = null;
		double maxDistance = 0;

		for (Atom a : atoms) {
			double distance = a.getPosition().distance(Point3D.ZERO);

			if (distance > maxDistance) {
				maxDistance = distance;
				furthest = a.getPosition();
			}
		}

		Point3D move = Point3D.ZERO.midpoint(furthest);
		moveTo(move);
	}

	/**
	 * Move to.
	 *
	 * @param position the position
	 */
	public void moveTo(Point3D position) {
		node.setTranslateX(0 - position.getX());
		node.setTranslateY(0 - position.getY());
		node.setTranslateZ(0 - position.getZ());
	}

	/**
	 * Render helices.
	 */
	private void renderHelices() {
		for (int i = 0; i < helices.size(); i++) {
			Aminoacid aas = chainMap.get(helices.get(i).getStartChainID())
					.getAcid(helices.get(i).getInitSeqNum());
			Aminoacid aae = chainMap.get(helices.get(i).getEndChainID())
					.getAcid(helices.get(i).getEndSeqNum());

			helices.get(i).setAcids(aas, aae);
			helices.get(i).setVisible(false);
			node.getChildren().addAll(helices.get(i).getNode());
		}
	}

	/**
	 * Render sheets.
	 */
	private void renderSheets() {
		for (int i = 0; i < sheets.size(); i++) {
			Aminoacid aas = chainMap.get(sheets.get(i).getInitChainID())
					.getAcid(sheets.get(i).getInitSeqNum());
			Aminoacid aae = chainMap.get(sheets.get(i).getEndChainID())
					.getAcid(sheets.get(i).getEndSeqNum());

			sheets.get(i).setAcids(aas, aae);
			sheets.get(i).setVisible(false);
			node.getChildren().addAll(sheets.get(i).getNode());
		}
	}

	/**
	 * Render explicitly stated links from LINK and CONNECT records.
	 */
	private void renderLinks() {
		for (int i = 0; i < explicit.size(); i++) {
			int serA = explicit.get(i).getSerialA();
			int serB = explicit.get(i).getSerialB();

			explicit.get(i).setAtomA(atomMap.get(serA));
			explicit.get(i).setAtomB(atomMap.get(serB));

			explicit.get(i).makeCylinder(Color.PURPLE);
			explicit.get(i).setVisible(true);
		}
	}

	/**
	 * Sets the visualisation mode for the structure.
	 *
	 * @param mode the new vis mode
	 */
	public void setVisMode(VisMode mode) {
		switch (mode) {
		case SPHERES:
			
			for (Helix h : helices)
				h.setVisible(false);
			
			for (Sheet s : sheets)
				s.setVisible(false);
			
			for (Bond b : bonds)
				b.setVisible(false);

			for (Atom a : atoms) {
				a.setVisible(true);
				a.setSize(1f);
			}
			break;

		case BALLANDSTICK:
			
			for (Helix h : helices)
				h.setVisible(false);
			
			for (Sheet s : sheets)
				s.setVisible(false);
			
			for (Bond b : bonds)
				b.setVisible(true);

			for (Atom a : atoms) {
				a.setSize(a.getSize());
				a.setVisible(true);
			}
			break;

		case STICKS:
			for (Helix h : helices)
				h.setVisible(false);
			
			for (Sheet s : sheets)
				s.setVisible(false);
			
			for (Bond b : bonds)
				b.setVisible(true);

			for (Atom a : atoms)
				a.setVisible(false);
			break;

		case SECONDARY:
			for (Bond b : bonds)
				b.setVisible(false);

			for (Atom a : atoms)
				a.setVisible(false);

			for (Helix h : helices) {
				h.setVisible(true);
				int start = acidMap.get(h.getInitSeqNum()).getAtom("CA")
						.getSerial();
				int end = acidMap.get(h.getEndSeqNum()).getAtom("CA")
						.getSerial();

			}

			for (Sheet s : sheets) {
				s.setVisible(true);

			}

			break;
		case BACKBONE:
			for (Bond b : bonds)
				b.setVisible(false);

			for (Atom a : atoms)
				a.setVisible(false);
			
			for (Helix h : helices)
				h.setVisible(false);
			
			for (Sheet s : sheets)
				s.setVisible(false);
			
			for (Chain c : chains)
				c.toggleMainChain(true);
			break;
		}
	}
	
	/**
	 * Sets the coloring mode for the structure.
	 *
	 * @param mode the new color mode
	 */

	public void setColorMode(ColorMode mode) {
		switch (mode) {
		case AMINOACIDS:
			for (Aminoacid aa : acids) {
				aa.colorBasedonType();
			}
			break;

		case ATOMS:
			for (Atom a : atoms) {
				a.resetColor();
			}
			
			for (Bond b : bonds) {
				b.setColor(Color.WHITE);
			}

			break;

		case CHAINS:
			for (Chain c : chains) {
				c.colorBasedOnSerial();
			}
			break;

		case SECONDARY:
			for (Atom a : atoms) {
				a.setColor(Color.WHITE);
			}

			for (Helix h : helices) {
				int start = h.getInitSeqNum();
				int end = h.getEndSeqNum();

				for (int i = start; i < end; i++) {
					chainMap.get(h.getStartChainID()).getAcid(i)
							.setColor(Color.RED);
				}

			}

			for (Sheet s : sheets) {
				int start = s.getInitSeqNum();
				int end = s.getEndSeqNum();

				for (int i = start; i < end; i++) {
					chainMap.get(s.getInitChainID()).getAcid(i)
							.setColor(Color.BLUE);
				}
			}
			break;
		}
	}
	
	/**
	 * Sets the cursor tooltip scope of the structure .
	 *
	 * @param type the new cursor scope
	 */

	public void setCursorScope(ScopeType type) {
		switch (type) {
		case ATOM:
			for (Aminoacid a : acids)
				a.setScope(false);
			for (Chain c : chains)
				c.setScope(false);

			for (Atom a : atoms)
				a.setScope(true);
			break;

		case AMINOACID:
			for (Atom a : atoms)
				a.setScope(false);
			for (Chain c : chains)
				c.setScope(false);

			for (Aminoacid a : acids)
				a.setScope(true);
			break;

		case CHAIN:
			for (Atom a : atoms)
				a.setScope(false);
			for (Aminoacid a : acids)
				a.setScope(false);

			for (Chain c : chains)
				c.setScope(true);
			break;

		}
	}

	/**
	 * Toggles whether certain amino acids within the given range 
	 * are visible or not. 
	 *
	 * @param start the start
	 * @param end the end
	 * @param b the b
	 */
	public void toggleAcidVisibility(int start, int end, boolean b) {
		for (int i = start; i < end; i++) {
			acidMap.get(i).setVisible(b);
		}
	}
	
	/**
	 * Sets the atoms visibility.
	 *
	 */
	public void setAtomsVisibility(boolean b) {
		for (Atom a : atoms) a.setVisible(b);
	}
	
	/**
	 * Sets the bonds visibility.
	 *
	 */
	public void setBondsVisibility(boolean b) {
		for (Bond bo : bonds) bo.setVisible(b);
	}

	/**
	 * Sets the main chain visibility.
	 *
	 */
	public void setMainChainVisibility(boolean b) {
		for (Bond bo : mainChain) bo.setVisible(b);
	}
	
	/**
	 * Sets the hetatoms visibility.
	 *
	 */
	public void setHetAtomVisibility(boolean b) {
		for (Atom a : hetatoms) a.setVisible(b);
	}
	

	/**
	 * Hide all.
	 */
	public void hideAll() {
		for (Helix h : helices)
			h.setVisible(false);

		for (Sheet s : sheets)
			s.setVisible(false);

		for (Aminoacid aa : acids)
			aa.setVisible(false);

		for (Chain c : chains)
			c.toggleMainChain(false);
	}

	/*
	 * setters and getters
	 */
	
	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public Group getNode() {
		return node;
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public Point3D getPosition() {
		return new Point3D(node.getTranslateX(), node.getTranslateY(),
				node.getTranslateZ());
	}

	/**
	 * Gets the meta info.
	 *
	 * @return the meta info
	 */
	public String getMetaInfo() {
		return metaInfo;
	}

	/**
	 * Sets the meta info.
	 *
	 * @param metaInfo the new meta info
	 */
	public void setMetaInfo(String metaInfo) {
		this.metaInfo = metaInfo;
	}

	/**
	 * Sets the helices.
	 *
	 * @param helices the new helices
	 */
	public void setHelices(List<Helix> helices) {
		this.helices = helices;
		renderHelices();
	}

	/**
	 * Sets the sheets.
	 *
	 * @param sheets the new sheets
	 */
	public void setSheets(List<Sheet> sheets) {
		this.sheets = sheets;
		renderSheets();
	}

	/**
	 * Gets the acids.
	 *
	 * @return the acids
	 */
	public List<Aminoacid> getAcids() {
		return this.acids;
	}

	/**
	 * Sets the het atoms.
	 *
	 * @param hetatoms the new het atoms
	 */
	public void setHetAtoms(List<Atom> hetatoms) {
		this.hetatoms = hetatoms;
		for (Atom a : hetatoms) {
			node.getChildren().add(a.getNode());
			atomMap.put(a.getSerial(), a);
		}
	}

	/**
	 * Sets the explicit links.
	 *
	 * @param links the new explicit links
	 */
	public void setExplicitLinks(List<Bond> links) {
		this.explicit = links;
		renderLinks();
	}
}
