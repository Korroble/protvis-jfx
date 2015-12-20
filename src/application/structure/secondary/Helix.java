package application.structure.secondary;

import application.structure.primary.Aminoacid;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;


/**
 * Represents a single alpha helix in the protein structure. 
 */

public class Helix {
	
	private Aminoacid start, end;
	
	private Point3D startP, endP; 

	private Cylinder shape; 

	private int serial, initSeqNum, endSeqNum; 

	private String startChainID, endChainID;
	

	/**
	 * Instantiates a new helix.
	 *
	 * @param start the start residue
	 */
	public Helix(Aminoacid start, Aminoacid end) {
		this.start = start; 
		this.end = end; 
		
		startP = start.getAtom("CA").getPosition();
		endP = end.getAtom("CA").getPosition();
		
		shape = makeCylinder(startP, endP); 
	}
	
	/**
	 * Instantiates a new helix.
	 *
	 * @param initSeqNum the init seq num
	 * @param endSeqNum the end seq num
	 * @param startChainID the start chain id
	 * @param endChainID the end chain id
	 */
	public Helix(int initSeqNum, int endSeqNum, String startChainID, String endChainID) {
		this.startChainID = startChainID; 
		this.endChainID = endChainID; 
		this.initSeqNum = initSeqNum; 
		this.endSeqNum = endSeqNum; 	
	}
	
	/**
	 * Make a cylinder to represent the helix.
	 *
	 * @param starting point 
	 * @param terminal point
	 * @return 3D cylinder shape 
	 */
	private Cylinder makeCylinder(Point3D start, Point3D end) {
		Point3D diff = new Point3D(end.getX() - start.getX(), end.getY()
				- start.getY(), end.getZ() - start.getZ());

		diff = diff.normalize();

		Point3D midpoint = start.midpoint(end);

		final Point3D YAXIS = new Point3D(0, 1, 0);

		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.WHITE);
		material.setSpecularColor(Color.WHITE);

		double length = start.distance(end);
		Cylinder cyl = new Cylinder(1f, length, 10);
		cyl.setMaterial(material);

		Point3D crossVec = YAXIS.crossProduct(diff);

		double ac = (double) Math.acos(YAXIS.dotProduct(diff));

		cyl.getTransforms()
				.add(new Translate(midpoint.getX(), midpoint.getY(), midpoint
						.getZ()));
		cyl.getTransforms().add(new Rotate(Math.toDegrees(ac), crossVec));

		cyl.setVisible(true);
		return cyl;
	}
	
	
	/**
	 * setters and getters.
	 *
	 * @param b the new visible
	 */
	
	public void setVisible(boolean b) {
		shape.setVisible(b);
	}

	/**
	 * setters and getters 
	 */
	public Cylinder getNode() {
		return shape;
	}

	public void setNode(Cylinder shape) {
		this.shape = shape;
	}

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public int getInitSeqNum() {
		return initSeqNum;
	}

	public void setInitSeqNum(int initSeqNum) {
		this.initSeqNum = initSeqNum;
	}

	public int getEndSeqNum() {
		return endSeqNum;
	}

	public void setEndSeqNum(int endSeqNum) {
		this.endSeqNum = endSeqNum;
	}
	
	public void setAcids(Aminoacid start, Aminoacid end) { 
		this.start = start; 
		this.end = end; 
		
		if (start != null && end != null) {
			startP = start.getAtom("CA").getPosition();
			endP = end.getAtom("CA").getPosition();
		
			shape = makeCylinder(startP, endP); 
		} else {
			shape = makeCylinder(new Point3D(0, 0, 0), new Point3D(0, 0, 0)); 
		}
		
	}

	public String getStartChainID() {
		return startChainID;
	}

	public void setStartChainID(String startChainID) {
		this.startChainID = startChainID;
	}

	public String getEndChainID() {
		return endChainID;
	}

	public void setEndChainID(String endChainID) {
		this.endChainID = endChainID;
	}
	
}
