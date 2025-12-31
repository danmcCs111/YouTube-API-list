package YoutubeApiList;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

public class TestYoutubeResponse 
{
	public static void test2(String arg, YouTube youtubeService)
    		throws GeneralSecurityException, IOException, GoogleJsonResponseException 
    {
    	// Define and execute the API request
    	YouTube.Channels.List request = youtubeService.channels().list(
    			Arrays.asList(new String []{"statistics","snippet", "statistics"}));
    	ChannelListResponse response = request.setForUsername("VICE").execute();
    	System.out.println(response);
    }
    
    public static void test1(String arg, YouTube youtubeService)
    {
      // 1. Load your API key from a properties file or environment variable
      try {
          String apiKey = arg;

          // 2. Initialize the YouTube client
          YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {})
                  .setApplicationName("youtube-cmdline-search-sample")
                  .build();

          // 3. Define the search request
          YouTube.Search.List search = youtube.search().list(Arrays.asList(new String []{"id","snippet"}));
          search.setKey(apiKey);
          search.setQ("Google Developers Live"); // Your search query
//          search.setType("video"); // Restrict search to videos
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
          
          // Define and execute the API request
          YouTube.Channels.List request = youtubeService.channels().list(
        		  Arrays.asList(new String []{"statistics","snippet", "statistics"}));
          ChannelListResponse response = request.setForUsername("GoogleDevelopers").execute();
          System.out.println(response);

		} catch (IOException e) {
		      System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
		} catch (Throwable t) {
		      t.printStackTrace();
		}
    }
}
