package io.redentity.christiansanchez.foldercreations;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class copyFolderContents extends SimpleFileVisitor<Path> {

	static boolean folderFound = false;
	private static Path sourceDir;
	private static Path targetDir;
	static Integer numberOfFile;
	private static String originalFolderPath;
	private static String folderName;
	public static void main(String[] args) throws IOException {
		
		if(args.length != 2)
	    {
	        System.out.println("Oops, looks like there isn't EXACTLY 2 values passed");
	        System.out.println("It should look like <folderName> <Number>");
	        System.out.println("Note: If the folder name has a space, ensure it's encapsulated with double quote");
	        System.out.println("Example: \"Folder name\" 12");
	        System.exit(0);
	    }
		
		folderName = args[0];
		numberOfFile = Integer.parseInt(args[1]);
	
		// Uses the current location then attempts to locate the folder path
		displayDirectoryContentsWithFolderName(new File("."), folderName); 
		
		// Makes sure the path was located
		if(originalFolderPath!=null) {			
			
			System.out.println("Original Folder:"+originalFolderPath);
			//Ensures the value passed is a number
			if(numberOfFile instanceof Integer) { 

				for(int y=1;y<numberOfFile+1;++y) { 
					sourceDir = Paths.get(originalFolderPath);
					targetDir = Paths.get(originalFolderPath+"_"+String.format("%02d", y)); // Ensures the number starts with at least 2 digits
					System.out.println("Attempting to create folder:"+originalFolderPath+"_"+String.format("%02d", y));
					Files.walkFileTree(sourceDir, new copyFolderContents(sourceDir, targetDir));						
				}
			}
		}else {
			System.out.println("Unable to locate the folder:"+folderName);
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
    
	
	
}
