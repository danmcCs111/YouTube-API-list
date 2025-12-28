import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PathUtility 
{
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
}
