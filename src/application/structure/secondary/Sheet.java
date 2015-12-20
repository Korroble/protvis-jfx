package application.structure.secondary;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import application.structure.primary.Aminoacid;


/**
 * Represents a single beta strand in the protein structure. 
 */
public class Sheet {
	
	private Aminoacid start, end;

	private Point3D startP, endP;

	private Box shape;

	private int initSeqNum, endSeqNum;

	private String id, initChainID, endChainID;

	/**
	 * Instantiates a new sheet.
	 *
	 * @param starting residue
	 * @param terminal residue 
	 */
	public Sheet(Aminoacid start, Aminoacid end) {
		this.start = start;
		this.end = end;

		startP = start.getAtom("CA").getPosition();
		endP = end.getAtom("CA").getPosition();

		shape = makeSheet(startP, endP);
	}

	/**
	 * Instantiates a new sheet.
	 *
	 * @param id id of the sheet
	 * @param initSeqNum the initial sequence number 
	 * @param endSeqNum the terminal sequence number
	 * @param initChainID the initial chain id
	 * @param endChainID the terminal chain id
	 */
	public Sheet(String id, int initSeqNum, int endSeqNum, String initChainID,
			String endChainID) {
		this.initChainID = initChainID;
		this.endChainID = endChainID;
		this.id = id;
		this.initSeqNum = initSeqNum;
		this.endSeqNum = endSeqNum;
	}

	/**
	 * Make a flattened 3D box to represent the strand.
	 *
	 * @param start starting point
	 * @param end terminal point 
	 * @return 3D Box object 
	 */
	private Box makeSheet(Point3D start, Point3D end) {
		Point3D diff = new Point3D(end.getX() - start.getX(), end.getY()
				- start.getY(), end.getZ() - start.getZ());

		diff = diff.normalize();

		Point3D midpoint = start.midpoint(end);

		final Point3D YAXIS = new Point3D(0, 1, 0);

		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.ORANGE);
		material.setSpecularColor(Color.ORANGE);

		double length = start.distance(end);
		Box box = new Box(1.5d, length, 0.1d);
		box.setMaterial(material);

		Point3D crossVec = YAXIS.crossProduct(diff);

		double ac = (double) Math.acos(YAXIS.dotProduct(diff));

		box.getTransforms()
				.add(new Translate(midpoint.getX(), midpoint.getY(), midpoint
						.getZ()));
		box.getTransforms().add(new Rotate(Math.toDegrees(ac), crossVec));

		box.setVisible(true);
		return box;
	}

	/**
	 * Sets the visible.
	 *
	 * @param b the new visible
	 */
	public void setVisible(boolean b) {
		shape.setVisible(b);
	}

	
	/**
	 * setters and getters.
	 *
	 * @return the inits the seq num
	 */
	public int getInitSeqNum() {
		return initSeqNum;
	}

	/**
	 * Gets the end seq num.
	 *
	 * @return the end seq num
	 */
	public int getEndSeqNum() {
		return endSeqNum;
	}

	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public Box getNode() {
		return shape;
	}

	/**
	 * Sets the acids.
	 *
	 * @param start the start
	 * @param end the end
	 */
	public void setAcids(Aminoacid start, Aminoacid end) {
		this.start = start;
		this.end = end;

		if (start != null && end != null) {
			startP = start.getAtom("CA").getPosition();
			endP = end.getAtom("CA").getPosition();

			shape = makeSheet(startP, endP);
		} else {
			shape = makeSheet(new Point3D(0, 0, 0), new Point3D(0, 0, 0));

		}

	}

	/**
	 * Gets the inits the chain id.
	 *
	 * @return the inits the chain id
	 */
	public String getInitChainID() {
		return initChainID;
	}

	/**
	 * Sets the inits the chain id.
	 *
	 * @param initChainID the new inits the chain id
	 */
	public void setInitChainID(String initChainID) {
		this.initChainID = initChainID;
	}

	/**
	 * Gets the end chain id.
	 *
	 * @return the end chain id
	 */
	public String getEndChainID() {
		return endChainID;
	}

	/**
	 * Sets the end chain id.
	 *
	 * @param endChainID the new end chain id
	 */
	public void setEndChainID(String endChainID) {
		this.endChainID = endChainID;
	}
}
