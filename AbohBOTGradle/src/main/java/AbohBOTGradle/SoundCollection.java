package AbohBOTGradle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * sound collection class to store every sound and handle file management of sound files
 */



public class SoundCollection {
	
	ArrayList<Sound> soundc = new ArrayList<Sound>();

	
	public SoundCollection() throws IOException {
				
	checkDirectory();
	checkFile();
	
	try {
		updateFile();
	} catch (IOException e) {
		e.printStackTrace();
	}
	    updateCollection();		
	}
	
	
	public ArrayList<Sound> getYtsounds() {
		return soundc;
	}

	
	/**
	 *was used for debugging purposes only
	 * prints collection to the console
	 * 
	 */
	
	public void printCollection() {
		
		for(int i = 0; i < soundc.size(); i++) {
			System.out.println(soundc.get(i).getName() + " " + soundc.get(i).getPath());
		}
	
		
	}

	
	/**
	 * method to update sound collection object 
	 * (filling the arrayList with sounds)
	 * checks the soundlist.txt where sound name and sound path is specified and 
	 * creates Sounds from it and adds them to the collection
	 * 
	 */
	
	public void updateCollection() {
		
		//clear the collection before updating
		// this way no need to check which sound files already exists
		soundc.clear();
		
		
		//scan the sound list
		try {
		Scanner scan = new Scanner(new FileInputStream((System.getProperty("user.dir"))+ "/soundlist.txt"));
			
		scan.useDelimiter("\n");
		
		
		// create sounds from entries and add them to the collection
		while (scan.hasNext()) {
			
			String name = scan.next();
			String path = ((System.getProperty(("user.dir")) +"/Sounds/" + name +".mp3"));
			
			soundc.add((new Sound(name, path)));
			

		}
		
		scan.close();
		
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}
	
	/**
	 * checks if soundlist.txt file exists
	 * if not create it
	 * 
	 */
	
	public void checkFile() {

		File exist_check = new File((System.getProperty("user.dir"))+"/soundlist.txt");
		if(!exist_check.exists()) {
			try {
				exist_check.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * checks if sounds directory exists
	 * if not create it
	 * 
	 */
	
	public void checkDirectory() throws IOException {

		File exist_check = new File((System.getProperty("user.dir"))+"/Sounds");
		if(!exist_check.exists()) {
			exist_check.mkdir();
		}
	}
	
	/**
	 * update method for soundlist.txt
	 * get every file and its name and path in the 'Sounds' directory 
	 * update soundlist.txt accordingly
	 */
	
	public void updateFile() throws IOException {
		
		//get read and write ressources on the soundlist.txt		
		FileWriter write = new FileWriter((System.getProperty("user.dir")+"/soundlist.txt"));
		Scanner scan = new Scanner(new FileInputStream((System.getProperty("user.dir"))+ "/soundlist.txt"));
	
	
		
		ArrayList<String> soundlist = new ArrayList<String>();
		scan.useDelimiter("\n");
		
		while (scan.hasNextLine()) {
			soundlist.add(scan.next());
		}
		
		
		Path sounddir = Paths.get((System.getProperty("user.dir") +"/Sounds"));
		
		//loop through files in the sound directory
		//if a file does not have an entry in the soundlist.txt yet - add it 
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(sounddir)) {
		for (Path file : stream) {
		    if (!soundlist.contains(file.getFileName().toString().substring(0, file.getFileName().toString().length()-4).toString())) {
		        write.append(file.getFileName().toString().substring(0, file.getFileName().toString().length()-4).toString() +"\n");
		       
		    }
		
	
	}		
		  scan.close();
		  write.close();
		 
		 
		 
 }catch (Exception e) {
	 e.printStackTrace();
 }
		
	
		

}
	/**
	 * create a String that is list of sounds
	 * used for !soundlist command (users can get an overview which sounds exist)
	 * 
	 */
	
	public String createSoundlist() {
		
		String soundlist = "";
		
		
		for (Sound sound : this.getYtsounds()) {
			soundlist = soundlist + sound.getName() +"\n";
			
		}
		
		
		
		
		return soundlist;
	}
}