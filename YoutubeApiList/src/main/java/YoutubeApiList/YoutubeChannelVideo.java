package YoutubeApiList;
import java.util.Comparator;
import java.util.Date;

import com.google.api.client.util.DateTime;

public class YoutubeChannelVideo implements Comparator<YoutubeChannelVideo>
{
	private static final String VIDEO_ID_PREFIX = "www.youtube.com/watch?v=";
	
	private int parentId = -1;
	private String 
		title,
		filteredTitle,
		imageUrl,
		videoId;
	private Date dt;
	
	public YoutubeChannelVideo(int parentId, String title, String imageUrl, String videoId, DateTime dt)
	{
		this.parentId = parentId;
		this.title = title;
		filteredTitle = PathUtility.filterTitle(this.title);
		
		this.imageUrl = imageUrl;
		this.videoId = videoId;
		this.dt = new Date(dt.getValue());
	}
	
	
	public int getParentId()
	{
		return this.parentId;
	}
	public Date getUploadDate()
	{
		return this.dt;
	}
	public String getFilteredTitle()
	{
		return filteredTitle;
	}
	public String getImageUrl()
	{
		return this.imageUrl;
	}
	public String getVideoId()
	{
		return this.videoId;
	}
	public String getVideoUrl()
	{
		return VIDEO_ID_PREFIX + this.videoId;
	}

	@Override
	public int compare(YoutubeChannelVideo o1, YoutubeChannelVideo o2) 
	{
		return o2.getUploadDate().compareTo(o1.getUploadDate());
	}
}
