package YoutubeApiList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PathUtility 
{
	public static String [] 
		htmlEscapes = new String [] {
			"&#39;", "&amp;", "&quot;", "&copy;", "&gt;", "&lt;", "&trade;", "&apos;"
	};
	public static String readFileToString(File locationFile)
   	{
   		Scanner sc;
   		String fileContents = "";
   		
   		if(!locationFile.exists())
   		{
   			return null;
   		}
   		try {
   			sc = new Scanner(locationFile);
   			while (sc.hasNextLine()) {
   				fileContents += sc.nextLine() + "\n";
   			}
   		} catch (FileNotFoundException e) {
   			e.printStackTrace();
   		}
   		return fileContents;
   	}
	
	public static void writeStringToFile(File locationFile, String contents)
	{
		try {
			FileWriter myWriter = new FileWriter(locationFile);
			myWriter.write(contents);	
			myWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String filterTitle(String title)
	{
		title = title.replaceAll(" ", "_");
		for(String htmlEscape : htmlEscapes)
		{
			title = title.replaceAll(htmlEscape, "");
		}
		title = title.replaceAll("[^a-zA-Z0-9_]*", "");
		
		return title;
	}
}
