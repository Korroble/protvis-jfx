#VisProt FX 1.0 - Java FX GUI Protein Visualiser

##DESCRIPTION
A simple GUI protein visualiser made with Java FX and FX3D. 

##RUNNING THE PROJECT
In order to launch the project from Eclipse you must have Eclipse version 4.4.0 (Luna) or greater with the e(fx)cipse plugin installed.

Use the import existing projects into workspace feature to open the project in Eclipse. 

Important:  In some cases, after importing the project there might be a lot of Java problems and the program will not be able to compile at all. 
In this case right click on the project and choose Build Path > Configure Build Path… There, under the Libraries tab remove the currently present 
JRE System Library and click on Add Library… . Select JRE System Library once prompted and click Next. From the next window select any JRE that 
is above 1.8 and click Finish. 

The main method from which the program is started is located in class Main.

##USER MANUAL
The program is started by clicking on the .jar executable entitled “VisProt.jar”. 
After loading is complete, the user is greeted with the main screen. The size of the window can be configured or even set to full screen. 

To avoid confusion and/or unwanted results with a possibly inconsistent PDB entry it is suggested to try loading 1L2Y first, 
as this is the one protein that has been used most frequently during the testing and development of the program and it is known not to 
have any problems that would cause the application to misbehave.

Camera Controls:
The Protein Visualiser accepts both keyboard and mouse input. Rotate the structure by holding down the right mouse button and dragging it on the screen. 
Zooming in and out is done either by holding down the middle mouse button and moving the cursor up and down or by using the scroll on the mouse. Panning is done by holding down the middle mouse button and dragging. 
The user may hover over on specific parts of the protein structure in order to reveal additional information about the selected part inside a box next to where the cursor is pointing. The scope of selection (e.g. single atom, amino acid, etc.) may be changed in the control panel on the left hand side. 

Control panel:
The control panel on the right-hand side of the application screen provides the tools to configure how the protein structure should be displayed as well as some other settings. The available options are:
*	PDB ID – A text field to enter a PDB ID and press OK to reload the reload a new model. A warning will be given in case the id has been entered incorrectly or it does not exist in the Protein Data Bank. 
*	RND – retrieves a random PDB entry from the Protein Data Bank. 
*	Load from disk – Opens a dialog window with a file system browser that prompts the user to select and load a PDB file from a local directory. 
*	Mode – A drop-down menu to select from Spheres, Ball and Stick, Lines or Cartoon representation mode
*	Colouring – A drop-down menu to choose a colouring scheme based on: 
	-	Elements
	-	Amino Acids
	-	Chains
	-	Secondary Structure
*	Show – A set of checkboxes that hide and show certain elements of the structure. All are true by default on start-up. These are: 
	-	Atoms
	-	Bonds 
	-	Main polypeptide chain
*	Tooltip Scope -  Choose what kind of information is going to be displayed when the cursor is hovered over the 3D model. The options are :
	-	Atom – Displays the serial number, element name, chain ID, residue and residue ID.
	-	Amino Acid – Displays the residue ID, name, chain ID, number of atoms and a list of atoms that belong to it. 
	-	Chain – Displays the chain ID, serial, number of residues, number of atoms and the sequence of residue names that belong to it. 
*	Display Region – Two input fields where the user enters the serial numbers of the start and end amino acids of the specific region of the polypeptide chain he desires to view.  
*	Save Image – A button that takes a snapshot of the current 3D model as it is seen on the display window and saves it and a bitmap image. Triggers the opening of a separate dialog window that prompts the user for a save directory. 

Protein Manipulation: 
The user may move separate polypeptide chains independently by holding down the left mouse button on one and dragging it around the screen.  


##FURTHER NOTES
The protein visualizer will save all downloaded PDB files in the same directory the .jar file is located. 
If a PDB file is already present in the same directory, a second download will not be performed. 

###COPYRIGHT
2015 (C) Slav Danchev
