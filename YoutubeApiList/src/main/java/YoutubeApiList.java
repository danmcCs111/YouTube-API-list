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
import java.util.Collection;

public class YoutubeApiList 
{
    private static final Collection<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");
    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    public static YouTube getService(String secretsFileLocation) throws GeneralSecurityException, IOException 
    {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport, secretsFileLocation);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
    
    public static Credential authorize(final NetHttpTransport httpTransport, String secretsFileLocation) throws IOException 
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
    
    public static void main(String[] args) 
    		throws GoogleJsonResponseException, GeneralSecurityException, IOException
    {
    	if(args[0].equals("test1"))
    		TestYoutubeResponse.test1(args[1], getService(args[2]));
    	else if(args[0].equals("test2"))
    		TestYoutubeResponse.test2(args[1], getService(args[2]));
    	else if(args[0].equals("test3"))
    	{
    		YoutubeChannelVideosCollector ycvc = new YoutubeChannelVideosCollector();
    		ArrayList<YoutubeChannelVideo> ycvs = ycvc.collectYoutubeChannelVideos(args[1], args[2]);
    		
    		for(YoutubeChannelVideo ycv : ycvs)
    		{
    			System.out.println(
					"Date Time: " + ycv.getUploadDate() + 
					" | Video Title: " + ycv.getFilteredTitle() + 
					" | Video ID: " + "www.youtube.com/watch?v=" + ycv.getVideoId() + 
					" | Thumbnail URL: " + ycv.getImageUrl());
    		}
    	}
    	
    }
    
}