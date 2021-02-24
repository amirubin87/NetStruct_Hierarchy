package netStruct_Hierarchy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
public class Common {
	
	public static void writeToFile(String fileName, String str) 
			  throws IOException {	
				File file = new File(fileName);
				file.getParentFile().mkdirs();
				FileWriter writer = new FileWriter(file);			    
			    writer.write(str);			     
			    writer.close();
	}
	
	public static String greatestCommonPrefix(String a, String b) {
	    int minLength = Math.min(a.length(), b.length());
	    for (int i = 0; i < minLength; i++) {
	        if (a.charAt(i) != b.charAt(i)) {
	            return a.substring(0, i);
	        }
	    }
	    return a.substring(0, minLength);
	}

	public static String getDate(){
		Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm dd-MM-y");
        return dateFormatter.format(now);	
	}
	
	public static void writeToLog(String pathToLog, String msg, boolean debug) throws IOException{		
		String msgWithTime  = getDate() + ": " + msg;
	    System.out.println(msgWithTime);
	    if (!debug){   
			Path path = Paths.get(pathToLog);
		    Files.write(path, Arrays.asList(msgWithTime), StandardCharsets.UTF_8,
		        Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
	    }
	}
	
	public static void renameFile(String pathToFile) throws Exception {
		String newName = pathToFile + "renamed.txt";
		// Note that as we add "renamed.txt" each time we fail, we will have no more than 21 attempts
		if(newName.length()>260)
		{
			throw new Exception("File path is too long, max is 260, got " + newName.length() + ". Path: " + newName);
		}
		if(!(new File(pathToFile)).renameTo(new File(newName))){
			renameFile(newName);	
			renameFile(pathToFile);
		}
	}
	
	public static int numOfLinesInFile(String pathToFile) throws IOException {
		File f = new File(pathToFile);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		int lines = 0;
		while (reader.readLine() != null) {
			lines++;			
		}
		reader.close();
		return lines;
	}
	
}
