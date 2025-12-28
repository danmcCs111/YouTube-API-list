import java.util.Comparator;
import java.util.Date;

import com.google.api.client.util.DateTime;

public class YoutubeChannelVideo implements Comparator<YoutubeChannelVideo>
{
	private String 
		title,
		imageUrl,
		videoId;
	private Date dt;
	
	public YoutubeChannelVideo(String title, String imageUrl, String videoId, DateTime dt)
	{
		this.title = title;
		this.imageUrl = imageUrl;
		this.videoId = videoId;
		this.dt = new Date(dt.getValue());
	}
	
	public Date getUploadDate()
	{
		return this.dt;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	public String getImageUrl()
	{
		return this.imageUrl;
	}
	public String getVideoId()
	{
		return this.videoId;
	}

	@Override
	public int compare(YoutubeChannelVideo o1, YoutubeChannelVideo o2) 
	{
		return o2.getUploadDate().compareTo(o1.getUploadDate());
	}
}
