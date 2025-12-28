
public class YoutubeChannelVideo 
{
	private String 
		title,
		imageUrl,
		videoId;
	
	public YoutubeChannelVideo(String title, String imageUrl, String videoId)
	{
		this.title = title;
		this.imageUrl = imageUrl;
		this.videoId = videoId;
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
