import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class YoutubeApiList 
{
    private static final Collection<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");
    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    private static final int []
    	DEFAULT_TIMESPAN = new int [] {Calendar.MONTH, -6};
    private static final String [] 
    		OPERATION_OPTIONS = new String [] {
    				"showResult", "test1", "test2"
    		};
    
    private static YouTube getService(String secretsFileLocation) throws GeneralSecurityException, IOException 
    {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport, secretsFileLocation);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
    
    private static Credential authorize(final NetHttpTransport httpTransport, String secretsFileLocation) throws IOException 
    {
    	File f = new File(secretsFileLocation);
    	String str = PathUtility.readFileToString(f);
        // Load client secrets.
        InputStream in = new ByteArrayInputStream(str.getBytes());
        GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        		httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }
    
    private static int [] getTimespanScan(long timestamp)
    {
    	Date d = new Date(timestamp);
    	Date current = Calendar.getInstance().getTime();
    	Calendar cal = Calendar.getInstance();
    	cal.add(DEFAULT_TIMESPAN[0], DEFAULT_TIMESPAN[1]);
    	Date currentMinusDefault = cal.getTime();
    	
    	int [] retTimeAdjust = new int [2];
    	if (timestamp == -1 || currentMinusDefault.after(d))
    	{
    		System.out.println("default: " + d.toString());
    		return DEFAULT_TIMESPAN;
    	}
    	else
    	{
    		long timeDiff = current.getTime() - d.getTime();
    		long days = (timeDiff / 1000 / 60 / 60 / 24);//from Milliseconds to days.
    		int second = (int) (timeDiff / 1000);//from Milliseconds to days.
    		System.out.println("days lapsed: " + days);
    		retTimeAdjust[0] = Calendar.SECOND;
    		retTimeAdjust[1] = -second + 1; // add one second to not include current.
    	}
    	
    	return retTimeAdjust;
    }
    
    public static void main(String[] args) 
    		throws GoogleJsonResponseException, GeneralSecurityException, IOException
    {
    	if(args.length != 6)
    	{
    		System.out.println(
    				"Enter: \n" + 
    				"Operation, \n" + 
    				"API Key, \n" +
    				"Parent Primary Key, \n" +
    				"Channel Name, \n" +
    				"Last Timestamp \n" + 
    				"Absolute File Output Path For Insert \n"
    		);
    		return;
    	}
    	String operation = args[0];
    	String apiKey = args[1];
    	int parentId = Integer.valueOf(args[2]);
    	String handleName = args[3];
    	long lastTimestamp = Long.valueOf(args[4]);
    	String absoluteFileLocationInsert = args[5];
    	
    	if(operation.equals(OPERATION_OPTIONS[0]))
    	{
    		YoutubeChannelVideosCollector ycvc = new YoutubeChannelVideosCollector();
    		int timeSpan[] = getTimespanScan(lastTimestamp);
    		ArrayList<YoutubeChannelVideo> ycvs = ycvc.collectYoutubeChannelVideos(
    				parentId, apiKey, handleName, timeSpan[0], timeSpan[1]);
    		
    		String sql = SqlConvert.convertYoutubeChannelVideos(ycvs);
    		System.out.println(sql);
    		File f = new File(absoluteFileLocationInsert);
    		PathUtility.writeStringToFile(f, sql);
    		
    		for(YoutubeChannelVideo ycv : ycvs)
    		{
    			System.out.println(
    					"Date Time: " + ycv.getUploadDate() + 
    					" | Video Title: " + ycv.getFilteredTitle() + 
    					" | Video ID: " + ycv.getVideoUrl() + 
    					" | Thumbnail URL: " + ycv.getImageUrl());
    		}
    	}
    	else if(operation.equals(OPERATION_OPTIONS[1]))
    	{
    		int timeSpan[] = getTimespanScan(lastTimestamp);
    		Calendar cal = Calendar.getInstance();
    		cal.add(timeSpan[0], timeSpan[1]);
    		System.out.println(cal.getTime().toString());
    	}
    	else if(operation.equals(OPERATION_OPTIONS[2]))
    	{
//    		TestYoutubeResponse.test2(args[1], getService(args[2]));
    	}
    }
    
}