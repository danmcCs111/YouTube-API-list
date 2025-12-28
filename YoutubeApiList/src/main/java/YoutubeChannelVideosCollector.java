import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
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
    	
    	// Use the channels.list method with the 'contentDetails' part
    	YouTube.Channels.List channelRequest = youtube.channels().list(
    			Arrays.asList(new String []{"contentDetails"}));
    	channelRequest.setForHandle(youtubeHandleName);
    	channelRequest.setKey(apiKey);

    	ChannelListResponse channelResponse = channelRequest.execute();
    	String uploadsPlaylistId = channelResponse.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads();
    	
    	// Use the playlistItems.list method with the 'snippet' and 'contentDetails' parts
    	YouTube.PlaylistItems.List playlistItemRequest = youtube.playlistItems().list(
    			Arrays.asList(new String []{"snippet", "contentDetails"}));
    	playlistItemRequest.setPlaylistId(uploadsPlaylistId);
    	playlistItemRequest.setMaxResults(50L); // Max results per page (50 is max)
    	playlistItemRequest.setKey(apiKey);

    	List<PlaylistItem> allVideos = new ArrayList<>();
    	String nextPageToken = "";

    	while (nextPageToken != null) 
    	{
    		playlistItemRequest.setPageToken(nextPageToken);
    	    PlaylistItemListResponse playlistItemResponse = playlistItemRequest.execute();

    	    allVideos.addAll(playlistItemResponse.getItems());
    	    nextPageToken = playlistItemResponse.getNextPageToken(); // Get the next page token
    	}

    	// Now 'allVideos' list contains all videos from the channel
    	for (PlaylistItem item : allVideos) 
    	{
    	    String videoTitle = item.getSnippet().getTitle();
    	    Thumbnail thumb = item.getSnippet().getThumbnails().getMedium();
    	    String thumbUrl = thumb.getUrl();
    	    String videoId = item.getContentDetails().getVideoId();
    	    retVideos.add(new YoutubeChannelVideo(videoTitle, thumbUrl, videoId));
    	    
    	    System.out.println("Video Title: " + videoTitle + " | Video ID: " + videoId + " | Thumbnail URL: " + thumbUrl);
    	}
    	
    	return retVideos;
    }
}
