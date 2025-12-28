import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class YoutubeApiList 
{
    // Global instance of the YouTube object.
    private static YouTube youtube;

    public static void main(String[] args) 
    {
        // 1. Load your API key from a properties file or environment variable
        Properties properties = new Properties();
        try {
            properties.load(YoutubeApiList.class.getResourceAsStream("/youtube.properties"));
            String apiKey = properties.getProperty("youtube.apikey");

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

        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}