package YoutubeApiList;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
	private static YouTube 
		youtube;
	private static int
		maxResultsPerDay = 5;
	private static long
		maxResults = 500L;//set through setMaxResults
	
	
	public ArrayList<YoutubeChannelVideo> collectYoutubeChannelVideos(
			int parentId, String apiKey, String youtubeHandleName, 
			int calendarFieldOffsetBegin, int calendarOffsetValueBegin,
			int calendarFieldOffsetEnd, int calendarOffsetValueEnd) 
			throws IOException
    {
		setMaxResults(calendarFieldOffsetBegin, calendarOffsetValueBegin,
				calendarFieldOffsetEnd, calendarOffsetValueEnd);
		String formatDateR = getRfc3339String(calendarFieldOffsetBegin, calendarOffsetValueBegin);
		return collectYoutubeChannelVideos(parentId, apiKey, youtubeHandleName, formatDateR);
    }
	
	public ArrayList<YoutubeChannelVideo> collectYoutubeChannelVideos(
			int parentId, String apiKey, String youtubeHandleName, int calendarFieldOffset, int calendarOffsetValue) 
			throws IOException
	{
		setMaxResults(calendarFieldOffset, calendarOffsetValue);
		String formatDateR = getRfc3339String(calendarFieldOffset, calendarOffsetValue);
		return collectYoutubeChannelVideos(parentId, apiKey, youtubeHandleName, formatDateR);
	}
	
	public ArrayList<YoutubeChannelVideo> collectYoutubeChannelVideos(
			int parentId, String apiKey, String youtubeHandleName, String formattedDateRfc3339) 
			throws IOException
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
    	
    	System.out.println(formattedDateRfc3339);
    	
    	searchItemRequest.setPublishedAfter(formattedDateRfc3339);
    	searchItemRequest.setMaxResults(maxResults); // Max results per page 
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

    	for (SearchResult item : allVideos) 
    	{
    		String channelTitle = item.getSnippet().getChannelTitle();
    		
    	    String videoTitle = item.getSnippet().getTitle();
    	    Thumbnail thumb = item.getSnippet().getThumbnails().getMedium();
    	    String thumbUrl = thumb.getUrl();
    	    String videoId = item.getId().getVideoId();
    	    DateTime dt = item.getSnippet().getPublishedAt();
    	    
    	    if(videoId != null)
    	    {
    	    	retVideos.add(new YoutubeChannelVideo(parentId, videoTitle, thumbUrl, videoId, dt));
    	    }
    	    
    	}
    	
    	Collections.sort(retVideos, new YoutubeChannelVideo());
    	
    	return retVideos;
    }
	
	private static void setMaxResults(int fieldOffset, int offsetValue)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(fieldOffset, offsetValue);
		Date offsetDate = cal.getTime();
		
		Calendar calNow = Calendar.getInstance();
		Date nowDate = calNow.getTime();
		
		long timeDiff = nowDate.getTime() - offsetDate.getTime();
		long days = (timeDiff / 1000 / 60 / 60 / 24);//from Milliseconds to days.
		System.out.println("days lapsed: " + days);
		
		YoutubeChannelVideosCollector.maxResults = YoutubeChannelVideosCollector.maxResultsPerDay * days;
	}
	
	private static void setMaxResults(int fieldOffsetBegin, int offsetValueBegin, 
			int fieldOffsetEnd, int offsetValueEnd)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(fieldOffsetBegin, offsetValueBegin);
		Date beginDate = cal.getTime();
		
		Calendar cal2 = Calendar.getInstance();
		cal2.add(fieldOffsetEnd, offsetValueEnd);
		Date endDate = cal2.getTime();
		
		long timeDiff = endDate.getTime() - beginDate.getTime();
		long days = (timeDiff / 1000 / 60 / 60 / 24);//from Milliseconds to days.
		System.out.println("days in timespan: " + days);
		
		YoutubeChannelVideosCollector.maxResults = YoutubeChannelVideosCollector.maxResultsPerDay * days;
	}
	
	public static String getRfc3339String(int fieldOffset, int offsetValue)
	{
		Calendar cal = Calendar.getInstance();
    	cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    	cal.add(fieldOffset, offsetValue);
    	Date d = cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	SimpleDateFormat sdf2 = new SimpleDateFormat("HH:MM:ss");
    	String formatDate = sdf.format(d);
    	String formatDate2 = sdf2.format(d);
    	String formatDateR = formatDate + "T" + formatDate2 + "Z";
    	
    	return formatDateR;
	}
}
