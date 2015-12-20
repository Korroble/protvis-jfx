package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.Point3D;
import application.structure.Chain;
import application.structure.Model;
import application.structure.primary.Aminoacid;
import application.structure.primary.Atom;
import application.structure.primary.Bond;
import application.structure.secondary.Helix;
import application.structure.secondary.Sheet;

/**
 * 
 * A parser for the PDB flat-file format.
 * 
 * Takes as an argument the name of the file that it should process (as long as
 * it's present- otherwise throw exception). Looks at the first 6 characters to
 * determine the type of record (i.e. ATOM, SEQRES, etc.) and then calls the
 * appropriate method for to parser the remainder of the line.
 * 
 * Currently not all types of records are parsed, mostly because they contain
 * irrelevant information for the parser at it's current stage of development.
 * However, there are stubs created for some of the more important PDB record
 * types.
 * 
 * Official documentation for the file format was used, available at :
 * http://www.wwpdb.org/documentation/format33/v3.3.html
 * 
 * 
 * @author Slav Danchev
 *
 */
public class Parser {
	private String filename = null, meta = "";
	private boolean inModel, multiModel;
	private int modelCount, lineCount = 0;
	private List<Atom> atomList, hetatmList;
	private List<Bond> links;
	private List<Chain> chains;
	private List<Sheet> sheets;
	private Model model;
	private List<Helix> helices;
	private File file;

	public Parser(String filename) {
		this.filename = filename;
		inModel = true;
		modelCount = 0;
		atomList = new ArrayList<Atom>();
		hetatmList = new ArrayList<Atom>();
		links = new ArrayList<Bond>();
		chains = new ArrayList<Chain>();
		sheets = new ArrayList<Sheet>();
		helices = new ArrayList<Helix>();

		try {
			readFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		buildModel();
		model.setHelices(helices);
		model.setSheets(sheets);
	}

	public Parser(File file) {
		this.file = file;
		inModel = true;
		modelCount = 0;
		atomList = new ArrayList<Atom>();
		chains = new ArrayList<Chain>();
		sheets = new ArrayList<Sheet>();
		helices = new ArrayList<Helix>();

		try {
			readFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		buildModel();
		model.setHelices(helices);
		model.setSheets(sheets);
		model.setExplicitLinks(links);
	}

	private void readFile() throws IOException {
		BufferedReader br;

		if (filename != null)
			br = new BufferedReader(new FileReader(filename));
		else
			br = new BufferedReader(new FileReader(file));

		String line;
		while ((line = br.readLine()) != null) {
			lineCount++;

			// separate first word from line
			String rectype = line.substring(0, 6);
			rectype = rectype.replaceAll("\\s+", "");

			// depending on the record type read in the beginning of the line,
			// pass the line string to
			// an appropriate parser method
			switch (rectype) {
			
			case "HEADER":
				parseHEADER(line);
				break;
			
			case "TITLE":
				parseTITLE(line);
				break;
				
			case "REMARK":
				parseREMARK(line);
				break;

			case "LINK":
				parseLINK(line);
				break;

			// determine opening of MODEL section
			case "MODEL":
				inModel = true;
				multiModel = true;
				break;

			case "SEQRES":
				parseSEQRES(line);
				break;

			case "HELIX":
				parseHELIX(line);
				break;

			case "SHEET":
				parseSHEET(line);
				break;

			case "HETATM":
				parseHETATM(line);
				break;

			case "ATOM":
				// always pick the first model that appears in the list
				if (modelCount == 0)
					parseATOM(line);
				break;

			case "CONECT":
				parseCONECT(line);
				break;

			// determine closing of MODEL section
			case "ENDMDL":
				modelCount++;
				inModel = false;
				break;

			case "TER":
				// inModel = false;
				break;
			}

		}

		br.close();
		System.out.println("Models: " + modelCount);
	}
	
	private void parseHEADER(String s) {
		String header = s.substring(10, 50);
		
		meta += header;
		meta += "\n";
	}
	
	private void parseTITLE(String s) {
		String title = s.substring(10, 80);
		
		meta += title;
		meta += "\n";
	}
	
	private void parseREMARK(String s) {
		String remark = s.substring(11, 79);
		
		meta += remark;
		meta += "\n";
	}

	private void parseLINK(String s) {
		String atom1 = s.substring(12, 16).replaceAll("\\s+", "");
		String altLoc1 = s.substring(16, 17).replaceAll("\\s+", "");
		String resName1 = s.substring(17, 20).replaceAll("\\s+", "");
		String chainID1 = s.substring(21, 22).replaceAll("\\s+", "");
		int resSeq1 = Integer.parseInt(s.substring(22, 26).replaceAll("\\s+",
				""));

		String atom2 = s.substring(42, 46).replaceAll("\\s+", "");
		String altLoc2 = s.substring(46, 47).replaceAll("\\s+", "");
		String resName2 = s.substring(47, 50).replaceAll("\\s+", "");
		String chainID2 = s.substring(51, 52).replaceAll("\\s+", "");
		int resSeq2 = Integer.parseInt(s.substring(52, 56).replaceAll("\\s+",
				""));

		// double length = Double.parseDouble(s.substring(73, 78).replaceAll(
		// "\\s+", ""));

	}

	private void parseCONECT(String s) {
		int serial = Integer
				.parseInt(s.substring(6, 11).replaceAll("\\s+", ""));

		if (!s.substring(11, 16).replaceAll("\\s+", "").equals("")) {
			int bondedSerial1 = Integer.parseInt(s.substring(11, 16)
					.replaceAll("\\s+", ""));

			links.add(new Bond(serial, bondedSerial1));
		}

		if (!s.substring(16, 21).replaceAll("\\s+", "").equals("")) {
			int bondedSerial2 = Integer.parseInt(s.substring(16, 21)
					.replaceAll("\\s+", ""));

			links.add(new Bond(serial, bondedSerial2));
		}

		if (!s.substring(21, 26).replaceAll("\\s+", "").equals("")) {
			int bondedSerial3 = Integer.parseInt(s.substring(21, 26)
					.replaceAll("\\s+", ""));

			links.add(new Bond(serial, bondedSerial3));
		}

		if (!s.substring(26, 31).replaceAll("\\s+", "").equals("")) {
			int bondedSerial4 = Integer.parseInt(s.substring(26, 31)
					.replaceAll("\\s+", ""));

			links.add(new Bond(serial, bondedSerial4));
		}

	}

	private void parseSEQRES(String s) {
		int serial = Integer
				.parseInt(s.substring(7, 10).replaceAll("\\s+", ""));
		String ID = s.substring(11, 12).replaceAll("\\s+", "");
		// Number of residues in the chain.
		int numRes = Integer
				.parseInt(s.substring(7, 10).replaceAll("\\s+", ""));

		String[] aminoAcidsOnLine = s.substring(19, 70).split("\\s+");

		// make an arraylist of the aminoacid strings
		ArrayList<String> aminoacids = new ArrayList<String>(
				Arrays.asList(aminoAcidsOnLine));

		// check if the list of chains is empty and create new chain object of
		// it is
		// or if the parser indicates the start of a new chain by the change of
		// the ID String
		// This is done by comparint the chainID of the current line with the
		// chainID of the previous chain
		// if (chains.isEmpty()
		// || !ID.equals(chains.get(chains.size() - 1).getChainID())) {
		// chains.add(new Chain(serial, ID));
		// chains.get(chains.size() - 1).setAminoacids(aminoacids);
		// } else {
		// // if we're still in the same chain sequence, simply append the
		// // aminoacids
		// // to the already existing list
		//
		// // get last element in chain list
		// chains.get(chains.size() - 1).appendAminoacids(aminoacids);
		// }

	}

	private void parseHELIX(String line) {
		int serial = Integer.parseInt(line.substring(6, 11).replaceAll("\\s+",
				""));

		String helixID = line.substring(11, 14);

		String initResName = line.substring(15, 18).replaceAll("\\s+", "");
		String initChainID = line.substring(19, 20).replaceAll("\\s+", "");
		int initSeqNum = Integer.parseInt(line.substring(21, 25).replaceAll(
				"\\s+", ""));
		String initICode = line.substring(25, 26);

		String endResName = line.substring(27, 30).replaceAll("\\s+", "");
		String endChainID = line.substring(31, 32).replaceAll("\\s+", "");
		int endSeqNum = Integer.parseInt(line.substring(33, 37).replaceAll(
				"\\s+", ""));
		String endICode = line.substring(37, 38);
		int helixClass = Integer.parseInt(line.substring(21, 25).replaceAll(
				"\\s+", ""));

		int length = Integer.parseInt(line.substring(71, 76).replaceAll("\\s+",
				""));

		helices.add(new Helix(initSeqNum, endSeqNum, initChainID, endChainID));
	}

	private void parseSHEET(String line) {
		int strand = Integer.parseInt(line.substring(7, 10).replaceAll("\\s+",
				""));
		String sheetID = line.substring(11, 14).replaceAll("\\s+", "");
		int numStrands = Integer.parseInt(line.substring(14, 16).replaceAll(
				"\\s+", ""));

		String initResName = line.substring(17, 20).replaceAll("\\s+", "");
		String initChainID = line.substring(21, 22).replaceAll("\\s+", "");
		int initSeqNum = Integer.parseInt(line.substring(22, 26).replaceAll(
				"\\s+", ""));

		String endResName = line.substring(28, 31).replaceAll("\\s+", "");
		String endChainID = line.substring(32, 33).replaceAll("\\s+", "");
		int endSeqNum = Integer.parseInt(line.substring(33, 37).replaceAll(
				"\\s+", ""));

		int sense = Integer.parseInt(line.substring(38, 40).replaceAll("\\s+",
				""));

		sheets.add(new Sheet(sheetID, initSeqNum, endSeqNum, initChainID,
				endChainID));
	}

	private void parseHETATM(String s) {
		int serial = Integer
				.parseInt(s.substring(6, 11).replaceAll("\\s+", ""));
		String name = s.substring(12, 16).replaceAll("\\s+", "");
		String altLoc = s.substring(16, 17);
		String resName = s.substring(17, 20).replaceAll("\\s+", "");
		String chainID = s.substring(21, 22);
		int resSeq = Integer.parseInt(s.substring(22, 26)
				.replaceAll("\\s+", ""));
		String iCode = s.substring(26, 27);
		double x = Double.parseDouble(s.substring(30, 38)
				.replaceAll("\\s+", ""));
		double y = Double.parseDouble(s.substring(38, 46)
				.replaceAll("\\s+", ""));
		double z = Double.parseDouble(s.substring(46, 54)
				.replaceAll("\\s+", ""));
		double occupancy = Double.parseDouble(s.substring(54, 60).replaceAll(
				"\\s+", ""));
		double tempFactor = Double.parseDouble(s.substring(60, 66).replaceAll(
				"\\s+", ""));
		String element_right = s.substring(76, 78);
		String charge = s.substring(77, 80);

		hetatmList.add(new Atom(new Point3D(x, y, z), name, element_right,
				serial, resName, resSeq, chainID));
	}

	private void parseATOM(String s) {
		// data gathering stage
		int serial = Integer
				.parseInt(s.substring(6, 11).replaceAll("\\s+", ""));
		String name = s.substring(12, 16).replaceAll("\\s+", "");
		String residue = s.substring(17, 20).replaceAll("\\s+", "");
		String chainID = s.substring(21, 22).replaceAll("\\s+", "");
		int resID = Integer
				.parseInt(s.substring(22, 26).replaceAll("\\s+", ""));
		double x = Double.parseDouble(s.substring(30, 38)
				.replaceAll("\\s+", ""));
		double y = Double.parseDouble(s.substring(38, 46)
				.replaceAll("\\s+", ""));
		double z = Double.parseDouble(s.substring(46, 54)
				.replaceAll("\\s+", ""));

		String element = s.substring(76, 78).replaceAll("\\s+", "");

		// data exploitation stage
		Point3D position = new Point3D(x, y, z);

		atomList.add(new Atom(position, name, element, serial, residue, resID,
				chainID));
	}

	private void buildModel() {
		List<Aminoacid> acids = new ArrayList<Aminoacid>();
		List<Chain> chains = new ArrayList<Chain>();
		ArrayList<Atom> curAA = new ArrayList<Atom>();

		String resName = "";

		for (int i = 0; i < atomList.size(); i++) {
			if (i == 0) {
				curAA.add(atomList.get(i));
			} else if (i == atomList.size() - 1) {
				acids.add(new Aminoacid(resName, curAA));
				curAA.clear();
			} else if (atomList.get(i).getResID() == atomList.get(i - 1)
					.getResID()) {
				curAA.add(atomList.get(i));
				resName = atomList.get(i).getResidue();
			} else {
				acids.add(new Aminoacid(resName, curAA));
				curAA.clear();
				curAA.add(atomList.get(i));
			}
		}

		String chainID = "";
		ArrayList<Aminoacid> curChain = new ArrayList<Aminoacid>();

		/**
		 * Detection and separation of individual polypeptide chains is to in a
		 * way analogous to the method for amino acids described above.
		 */
		for (int i = 0; i < acids.size(); i++) {
			if (i == 0) {
				curChain.add(acids.get(i));
			} else if (i == acids.size() - 1) {
				chains.add(new Chain(curChain));
				curChain.clear();
			} else if (acids.get(i).getChainID()
					.equals(acids.get(i - 1).getChainID())) {
				curChain.add(acids.get(i));
				chainID = acids.get(i).getChainID();
			} else {
				chains.add(new Chain(curChain));
				curChain.clear();
				curChain.add(acids.get(i));
			}
		}

		model = new Model((ArrayList<Chain>) chains);
		model.setHetAtoms(hetatmList);
		
		model.setMetaInfo(meta);
		}

	/*
	 * getters
	 */
	public ArrayList<Atom> getAtomList() {
		return (ArrayList<Atom>) atomList;
	}

	public ArrayList<Chain> getChains() {
		return (ArrayList<Chain>) chains;
	}

	public Model getModel() {
		return model;
	}

	public List<Helix> getHelices() {
		return helices;
	}

}