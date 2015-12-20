package application.downloader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import application.view.Controller;

// TODO: Auto-generated Javadoc
/**
 * The Class PDBDownloader.
 */
public class PDBDownloader implements Runnable{

	/** The code. */
	private String code; 
	
	/**
	 * Instantiates a new PDB downloader.
	 *
	 * @param code the code
	 */
	public PDBDownloader(String code){
		this.code = code; 
	}
	
	/**
	 * Instantiates a new PDB downloader.
	 */
	public PDBDownloader(){
		super();
	}

	/**
	 * Downloads a PDB file based on input ID.
	 *
	 * @param code the code
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void download(String code) throws IOException {
		if (code.length() != 4) {
			System.out.println("Invalid PDB code. Must be 4 characters long.");
			Controller.showPopUp("Invalid PDB code. Must be 4 characters long.");
			return;
		}

		URL url = new URL("http://www.rcsb.org/pdb/files/" + code + ".pdb");
		String filename = code.toLowerCase() + ".pdb";
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		int response = conn.getResponseCode();

		if (response == HttpURLConnection.HTTP_OK) {
			InputStream input = conn.getInputStream();

			FileOutputStream outputFile = new FileOutputStream(filename);

			int bytesRead = -1;
			byte[] buffer = new byte[4096];
			while ((bytesRead = input.read(buffer)) != -1) {
				outputFile.write(buffer, 0, bytesRead);
			}

			outputFile.close();
			input.close();

			System.out.println("File successfully downloaded!");
		} else if (response == HttpURLConnection.HTTP_NOT_FOUND) {
			System.out.println("Specified ID does not exist in database.");
			Controller.showPopUp("ID not in database.");
		} else {
			System.out.println("Connection Error!");
			Controller.showPopUp("Connection Error!");
		}
	}
	
	/**
	 * Download random PDB ID.
	 *
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String downloadRandom() throws IOException {
		String characters = "abcdefghijklmnopqrstuvwxyz1234567890";
		String code = "", filename = "";
		Random rd = new Random();
		int response = -1;
		HttpURLConnection conn = null;
		
		while (response != HttpURLConnection.HTTP_OK) {  
		code = "";
		//generate 4 letter alphanumeric string
		for (int i = 0 ; i < 4 ; i++) {
			code += characters.charAt(rd.nextInt(characters.length()));
		}
		
		System.out.println("Trying " + code);
		URL url = new URL("http://www.rcsb.org/pdb/files/" + code + ".pdb");
		filename = code.toLowerCase() + ".pdb";
		conn = (HttpURLConnection) url.openConnection();
		response = conn.getResponseCode();
		}
		
		System.out.println(code + " exists!");
		InputStream input = conn.getInputStream();

		FileOutputStream outputFile = new FileOutputStream(filename);

		int bytesRead = -1;
		byte[] buffer = new byte[4096];
		while ((bytesRead = input.read(buffer)) != -1) {
			outputFile.write(buffer, 0, bytesRead);
		}

		outputFile.close();
		input.close();
		
		return code; 
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String... args) {
		try {
			new PDBDownloader().downloadRandom();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try {
			download(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
