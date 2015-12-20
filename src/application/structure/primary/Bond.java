package application.structure.primary;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * 
 * A single atomic bond between two atoms in the structure. Represented visually
 * as a cylinder, defined by the positions of two atoms. 
 * 
 * @author Slav Danchev
 * 
 */
public class Bond{
	private Atom atomA, atomB;
	private Point3D posA, posB;
	private Cylinder line3D;
	private float length;
	private PhongMaterial material;
	private int serialA, serialB;

	public Bond(Atom a, Atom b) {
		atomA = a;
		atomB = b;

		// used for cases where H atoms are omitted to avoid
		// NullPointerExceptions
		// simply create an invisible cylinder with radius 0 and height 0
		if (a == null || b == null) {
			line3D = new Cylinder(0, 0);
		} else {
			posA = atomA.getPosition();
			posB = atomB.getPosition();

			length = atomA.distance(atomB);

			line3D = makeCylinder(posA, posB);
			
			a.addBond(this);
			b.addBond(this);
		}
	}

	public Bond(Atom a, Atom b, Color color) {
		atomA = a;
		atomB = b;

		if (a == null || b == null) {
			line3D = new Cylinder(0, 0);
		} else {
			posA = atomA.getPosition();
			posB = atomB.getPosition();

			length = atomA.distance(atomB);

			// line = makeLine(posA, posB, color);
			line3D = makeCylinder(posA, posB, color);
			
			a.addBond(this);
			b.addBond(this);
		}
	}
	
	public Bond(int serialA, int serialB) {
		this.serialA = serialA;
		this.serialB = serialB;
	}

	public void resetColor() {
		material = new PhongMaterial();
		material.setDiffuseColor(Color.WHITE);
		material.setSpecularPower(0.1);
		material.setSpecularColor(Color.WHITE);
		
		line3D.setMaterial(material);
	}
	
	public void setColor(Color color) {
		material = new PhongMaterial();
		material.setDiffuseColor(color);
		material.setSpecularPower(0.1);
		material.setSpecularColor(color);
		
		line3D.setMaterial(material);
	}
	
	/**
	 * Creates a 3D cylinder with given start and end point. 
	 * 
	 * Calculates the distance between the two given atoms 
	 * and uses it as the height value to create a cylinder. 
	 * 
	 * Then calculates the midpoint between two atoms and translates it 
	 * there. Rotates it around an axis that is the cross-product vector 
	 * between the Y axis and the difference vector of the two atoms. 
	 * 
	 */
	public Cylinder makeCylinder(Point3D start, Point3D end) {
		Point3D diff = new Point3D(end.getX() - start.getX(), end.getY()
				- start.getY(), end.getZ() - start.getZ());

		diff = diff.normalize();

		Point3D midpoint = start.midpoint(end);

		final Point3D YAXIS = new Point3D(0, 1, 0);

		material = new PhongMaterial();
		material.setDiffuseColor(Color.WHITE);
		material.setSpecularColor(Color.WHITE);

		Cylinder cyl = new Cylinder(0.05f, length, 10);
		cyl.setMaterial(material);

		Point3D crossVec = YAXIS.crossProduct(diff);

		// cyl.setRotationAxis(crossVec);
		// cyl.setRotate((float) Math.acos(YAXIS.dotProduct(diff)));

		double ac = (double) Math.acos(YAXIS.dotProduct(diff));

		cyl.getTransforms()
				.add(new Translate(midpoint.getX(), midpoint.getY(), midpoint
						.getZ()));
		cyl.getTransforms().add(new Rotate(Math.toDegrees(ac), crossVec));

		cyl.setVisible(true);
		return cyl;
	}

	/**
	 * Same as makeCylinder(Point3D start, Point3D end) but accepts 
	 * a Color as argument to change the appearance of the 3D model. 
	 */
	public Cylinder makeCylinder(Point3D start, Point3D end, Color color) {
		Point3D diff = new Point3D(end.getX() - start.getX(), end.getY()
				- start.getY(), end.getZ() - start.getZ());

		diff = diff.normalize();

		Point3D midpoint = start.midpoint(end);

		final Point3D YAXIS = new Point3D(0, 1, 0);

		material = new PhongMaterial();
		material.setDiffuseColor(color);
		material.setSpecularColor(color);
		material.setSpecularPower(0.01);

		Cylinder cyl = new Cylinder(0.1f, length, 10);
		cyl.setMaterial(material);

		Point3D crossVec = YAXIS.crossProduct(diff);

		// cyl.setRotationAxis(crossVec);
		// cyl.setRotate((float) Math.acos(YAXIS.dotProduct(diff)));

		double ac = (double) Math.acos(YAXIS.dotProduct(diff));

		cyl.getTransforms()
				.add(new Translate(midpoint.getX(), midpoint.getY(), midpoint
						.getZ()));
		cyl.getTransforms().add(new Rotate(Math.toDegrees(ac), crossVec));

		cyl.setVisible(true);
		return cyl;
	}
	
	public void makeCylinder(Color color) {
		line3D = makeCylinder(posA, posB, color);
	}
	
	public void setVisible(boolean b) {
		line3D.setVisible(b);
	}

	/**
	 * Setters and getters.
	 */
	public Atom getAtomA() {
		return atomA;
	}

	public void setAtomA(Atom atomA) {
		this.atomA = atomA;
	}

	public Atom getAtomB() {
		return atomB;
	}

	public void setAtomB(Atom atomB) {
		this.atomB = atomB;
	}

	public Point3D getPosA() {
		return posA;
	}

	public void setPosA(Point3D posA) {
		this.posA = posA;
	}

	public Point3D getPosB() {
		return posB;
	}

	public void setPosB(Point3D posB) {
		this.posB = posB;
	}

	public Cylinder getNode() {
		return line3D;
	}
	
	public void setNewShape(Point3D a, Point3D b) {
		Cylinder cy = makeCylinder(a,b);
		double h = cy.getHeight();
		double e = line3D.getHeight();
		line3D.setHeight(cy.getHeight());
	}

	public float getLength() {
		return length;
	}

	public int getSerialA() {
		return serialA;
	}

	public int getSerialB() {
		return serialB;
	}
}
