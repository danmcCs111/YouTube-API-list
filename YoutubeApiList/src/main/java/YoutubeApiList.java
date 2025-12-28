import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class YoutubeApiList 
{
    // Global instance of the YouTube object.
    private static YouTube youtube;
    private static String CLIENT_SECRETS = "";
    private static final Collection<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");

    private static final String APPLICATION_NAME = "API code samples";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    
    public static YouTube getService() throws GeneralSecurityException, IOException 
    {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
    
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException 
    {
    	File f = new File(CLIENT_SECRETS);
    	String str = readFileToString(f);
        // Load client secrets.
        InputStream in = new ByteArrayInputStream(str.getBytes());
        GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        		httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .build();
        Credential credential =
            new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }
    
    public static void main(String[] args) 
    		throws GoogleJsonResponseException, GeneralSecurityException, IOException
    {
    	if(args[0].equals("test1"))
    		test1(args[1]);
    	else if(args[0].equals("test2"))
    		test2(args[1]);
    }

    
    public static void test2(String arg)
    		throws GeneralSecurityException, IOException, GoogleJsonResponseException 
    {
    	CLIENT_SECRETS = arg;
    	YouTube youtubeService = getService();
    	// Define and execute the API request
    	YouTube.Channels.List request = youtubeService.channels()
    			.list("snippet,contentDetails,statistics");
    	ChannelListResponse response = request.setForUsername("VICE").execute();
    	System.out.println(response);
    }
    
    public static void test1(String arg)
    {
      // 1. Load your API key from a properties file or environment variable
      try {
          String apiKey = arg;

          // 2. Initialize the YouTube client
          youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {})
                  .setApplicationName("youtube-cmdline-search-sample")
                  .build();

          // 3. Define the search request
          YouTube.Search.List search = youtube.search().list("id,snippet");
          search.setKey(apiKey);
          search.setQ("Google Developers Live"); // Your search query
          search.setType("video"); // Restrict search to videos
          search.setMaxResults(5L); // Retrieve max 5 results

          // 4. Execute the request and process the response
          SearchListResponse searchResponse = search.execute();
          List<SearchResult> searchResultList = searchResponse.getItems();

          if (searchResultList != null) 
          {
              System.out.println("Videos found:");
              for (SearchResult singleVideo : searchResultList) 
              {
                  System.out.println("- Title: " + singleVideo.getSnippet().getTitle() +
                                     " (Video ID: " + singleVideo.getId().getVideoId() + ")");
              }
          }
          
          YouTube youtubeService = getService();
          // Define and execute the API request
          YouTube.Channels.List request = youtubeService.channels()
              .list("snippet,contentDetails,statistics");
          ChannelListResponse response = request.setForUsername("GoogleDevelopers").execute();
          System.out.println(response);

		} catch (IOException e) {
		      System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
		      t.printStackTrace();
		}
    }
    
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