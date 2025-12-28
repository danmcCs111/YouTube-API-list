import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

public class YoutubeChannelVideosCollector 
{
	private static YouTube youtube;
	private ArrayList<YoutubeChannelVideo> youtubeChannelVideos;
	
	public ArrayList<YoutubeChannelVideo> getYoutubeChannelVideos()
	{
		return this.youtubeChannelVideos;
	}
	
	public ArrayList<YoutubeChannelVideo> collectYoutubeChannelVideos(String apiKey, String youtubeHandleName) throws IOException
    {
    	ArrayList<YoutubeChannelVideo> retVideos = new ArrayList<YoutubeChannelVideo>();
    	
    	youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {})
                .setApplicationName("youtube-cmdline-search-sample")
                .build();
    	
    	YouTube.Channels.List channelRequest = youtube.channels().list(
    			Arrays.asList(new String []{"contentDetails"}));
    	channelRequest.setForHandle(youtubeHandleName);
    	channelRequest.setKey(apiKey);

    	ChannelListResponse channelResponse = channelRequest.execute();
    	String uploadsPlaylistId = channelResponse.getItems().getFirst().getId();
    	System.out.println(uploadsPlaylistId);
    	
    	YouTube.Search.List searchItemRequest = youtube.search().list(
    			Arrays.asList(new String []{"id", "snippet"}));
    	searchItemRequest.setChannelId(uploadsPlaylistId);
    	String formatDateR = getRfc3339String(-6);
    	System.out.println(formatDateR);
    	
    	searchItemRequest.setPublishedAfter(formatDateR);
    	searchItemRequest.setMaxResults(50L); // Max results per page (50 is max)
    	searchItemRequest.setKey(apiKey);

    	List<SearchResult> allVideos = new ArrayList<>();
    	String nextPageToken = "";

    	while (nextPageToken != null) 
    	{
    		searchItemRequest.setPageToken(nextPageToken);
    	    SearchListResponse playlistItemResponse = searchItemRequest.execute();

    	    allVideos.addAll(playlistItemResponse.getItems());
    	    nextPageToken = playlistItemResponse.getNextPageToken(); // Get the next page token
    	}

    	// Now 'allVideos' list contains all videos from the channel
    	for (SearchResult item : allVideos) 
    	{
    		String channelTitle = item.getSnippet().getChannelTitle();
    		
    	    String videoTitle = item.getSnippet().getTitle();
    	    Thumbnail thumb = item.getSnippet().getThumbnails().getMedium();
    	    String thumbUrl = thumb.getUrl();
    	    String videoId = item.getId().getVideoId();
    	    DateTime dt = item.getSnippet().getPublishedAt();
    	    
    	    retVideos.add(new YoutubeChannelVideo(videoTitle, thumbUrl, videoId, dt));
    	    
    	    System.out.println(
    	    		"Date Time: " + dt.toStringRfc3339() + 
    	    		" | Video Title: " + videoTitle + 
    	    		" | Video ID: " + "www.youtube.com/watch?v=" + videoId + 
    	    		" | Thumbnail URL: " + thumbUrl);
    	}
    	
    	return retVideos;
    }
	
	public static String getRfc3339String(int monthsOffset)
	{
		Calendar cal = Calendar.getInstance();
    	cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    	cal.add(Calendar.MONTH, monthsOffset);
    	Date d = cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	SimpleDateFormat sdf2 = new SimpleDateFormat("HH:MM:ss");
    	String formatDate = sdf.format(d);
    	String formatDate2 = sdf2.format(d);
    	String formatDateR = formatDate + "T" + formatDate2 + "Z";
    	
    	return formatDateR;
	}
}
