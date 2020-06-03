package AbohBOTGradle;


/**
 * very simple sound class to create sound objects
 * sound objects have a name 
 * and a path (to a file directory)
 * 
 * 
 *
 */

public class Sound {
	
	String name;
	String path;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String url) {
		this.path = url;
	}
	


public Sound(String name, String path) {
	setName(name);
	setPath(path);
}
	
	
}
