import com.google.api.client.util.DateTime;

public class YoutubeChannelVideo 
{
	private String 
		title,
		imageUrl,
		videoId;
	private DateTime dt;
	
	public YoutubeChannelVideo(String title, String imageUrl, String videoId, DateTime dt)
	{
		this.title = title;
		this.imageUrl = imageUrl;
		this.videoId = videoId;
		this.dt = dt;
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
}
