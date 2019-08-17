import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

/**
 * @author Daniel Redfern (redentity.io)
 * @version 1.4
 * @changes Included the remove of unwanted Log files
 * @changes Included 'master' folder function
 */
public class copyFolderContents extends SimpleFileVisitor<Path> {

	static boolean folderFound = false;
	private static Path sourceDir;
	private static Path targetDir;
	static Integer numberOfFile;
	private static String originalFolderPath;
	private static String folderName;
	private final static String[] masterOptions = {"master","Master","MASTER"};
	 
	
	public static void main(String[] args) throws IOException {		
		System.out.println("Current location to start searching for file : " + System.getProperty("user.dir"));
		
		isarrayLengthOK(args.length);
		folderName = args[0];
		numberOfFile = Integer.parseInt(args[1]);

		
		if(Arrays.asList(masterOptions).contains(args[0])){
            System.out.println("Master entered!!!");
            //TODO find in current directory folders that start with gemJava8_Mode
            //TODO for every folder, iterate through and copy contents            
            File f = new File("."); 
            File[] files = f.listFiles();
    			for (File file : files) {
    				if (file.isDirectory()) {
    					if(file.getName().toLowerCase().startsWith("gemjava8_mode")) {
    						originalFolderPath = file.getCanonicalPath();
    						for(int y=1;y<numberOfFile+1;++y) { 
            					sourceDir = Paths.get(originalFolderPath);
            					targetDir = Paths.get(originalFolderPath+"_"+String.format("%02d", y)); // Ensures the number starts with at least 2 digits
            					System.out.println("Attempting to create folder:"+originalFolderPath+"_"+String.format("%02d", y));
            					Files.walkFileTree(sourceDir, new copyFolderContents(sourceDir, targetDir));						
            				}
    						
    						for(int y=1;y<Integer.parseInt(args[1])+1;++y) {
    	        					String[] items = new String(originalFolderPath+"_"+ String.format("%02d", y)).replace("\\","\\\\").split("\\\\");
    	        					cleanUpLogFiles(new File(originalFolderPath+"_"+ String.format("%02d", y)),items[items.length-1]);
    	        				}
    						
    					}
    				}
    			}
            
        }else {
        		// Uses the current location then attempts to locate the folder path
        		// If located, it will update the originalFolderPath variable
        		displayDirectoryContentsWithFolderName(new File("."), folderName);
        		// Makes sure the path was located
        		if(originalFolderPath!=null) {			

        			//Ensures the value passed is a number
        			if(numberOfFile instanceof Integer) { 

        				for(int y=1;y<numberOfFile+1;++y) { 
        					sourceDir = Paths.get(originalFolderPath);
        					targetDir = Paths.get(originalFolderPath+"_"+String.format("%02d", y)); // Ensures the number starts with at least 2 digits
        					System.out.println("Attempting to create folder:"+originalFolderPath+"_"+String.format("%02d", y));
        					Files.walkFileTree(sourceDir, new copyFolderContents(sourceDir, targetDir));						
        				}
        			}
        			
        			for(int y=1;y<Integer.parseInt(args[1])+1;++y) {
        				String[] items = new String(originalFolderPath+"_"+ String.format("%02d", y)).replace("\\","\\\\").split("\\\\");
        				cleanUpLogFiles(new File(originalFolderPath+"_"+ String.format("%02d", y)),items[items.length-1]);
        			}
        			
        		}else {
        			System.out.println("Unable to locate the folder : "+folderName);
        		}
        }	
	}
	
	
	/*
	 * Check the arguments passed
	 */
	private static void isarrayLengthOK(int length) {
		if(length != 2)
	    {
	        System.out.println("Oops, looks like there isn't EXACTLY 2 values passed");
	        System.out.println("It should look like <folderName> <Number>");
	        System.out.println("Note: If the folder name has a space, ensure it's encapsulated with double quote");
	        System.out.println("When executing the command, pass the two arguments like this --> \"Folder name\" 12");	        
	        System.exit(0);
	    }		
	}


	public copyFolderContents(Path sourceDir, Path targetDir) {
        copyFolderContents.sourceDir = sourceDir;
        copyFolderContents.targetDir = targetDir;
    }
	

	public static void displayDirectoryContents(File dir) throws IOException {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				displayDirectoryContents(file);				
				if(folderFound) {
					break;
				}
			}
		}
	}
	
	

	/**
	 * @param dir The current directory to review for files with LOG+args[0]
	 * @description Used to cleanup the copied log files based on the passed variable
	 */
	//public static void cleanUpLogFiles(File dir, String folderName, Integer originalNumber) throws IOException {
	public static void cleanUpLogFiles(File dir,String parentFolder) throws IOException {
		File[] files = dir.listFiles();
		
		
		for (File file : files) {
			if(file.isFile()) {
				String fileName = file.getName().toString();				
				
				final String regex = "(.*Log([1-9]*).*)";
				
				if (fileName.matches(regex)) {
					
					String[] parts = stripExtension(fileName).split("Log");
					String[] folderParts = parentFolder.split("_");
					
					String x1 = new String(folderParts[folderParts.length-1].toString());
					String x2 = new String("0"+parts[parts.length-1].toString());
					String x3 = new String(parts[parts.length-1].toString());
					
					if(!(x1.equals(x2)||x1.equals(x3))) {
						new File(dir.getAbsolutePath()+"/"+fileName).delete();
					}
				}
			} else if (file.isDirectory()) {
				cleanUpLogFiles(file,parentFolder);
			}
		}		
	}
	
	public static void findMasterFolders(File dir, String folderName) throws IOException {
		
	}
	
	public static void displayDirectoryContentsWithFolderName(File dir, String folderName) throws IOException {
		File[] files = dir.listFiles();
		for (File file : files) {
			
			if(folderFound) 
				break;
			
			if (file.isDirectory()) {
				displayDirectoryContentsWithFolderName(file,folderName);
				
				if(folderName.equals(file.getName())) {
					originalFolderPath = file.getCanonicalPath();
				}
			}
		}		
	}
	
	
	@Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
   
        try {
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            Files.copy(file, targetFile);
        } catch (IOException ex) {
            System.err.println(ex);
        }
 
        return FileVisitResult.CONTINUE;
    }
 
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attributes) {
        try {
            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
            Files.createDirectory(newDir);
        } catch (IOException ex) {
            System.err.println(ex);
        }
 
        return FileVisitResult.CONTINUE;
    }
    
    static String stripExtension (String str) {
        if (str == null) 
        	return null;
        
        int pos = str.lastIndexOf(".");
        if (pos == -1) 
        	return str;

        return str.substring(0, pos);
    }

}
