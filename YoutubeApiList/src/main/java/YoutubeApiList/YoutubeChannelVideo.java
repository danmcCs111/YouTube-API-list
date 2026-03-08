package YoutubeApiList;
import java.util.Comparator;
import java.util.Date;

import com.google.api.client.util.DateTime;

public class YoutubeChannelVideo implements Comparator<YoutubeChannelVideo>
{
	private static final String 
		VIDEO_ID_PREFIX = "www.youtube.com/watch?v=";
	
	private int 
		parentId = -1;
	private String 
		title,
		filteredTitle,
		duration,
		durationHoursMinutesSeconds,
		imageUrl,
		videoId;
	private Date 
		dt;
	
	public YoutubeChannelVideo()
	{
		
	}
	
	public YoutubeChannelVideo(int parentId, String title, String imageUrl, String videoId, String duration, DateTime dt)
	{
		this.parentId = parentId;
		this.title = title;
		filteredTitle = PathUtility.filterTitle(this.title);
		
		this.imageUrl = imageUrl;
		this.videoId = videoId;
		this.duration = duration;
		this.durationHoursMinutesSeconds = convertISO8601HoursMinutesSeconds(duration);
		this.dt = new Date(dt.getValue());
	}
	
	
	public static String convertISO8601HoursMinutesSeconds(String duration)
	{
		int
			hour = 0,
			minute = 0,
			second = 0;
		
		//youtube video limit 12 hours
		char [] chars = duration.toCharArray();
		for(int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			if(c == 'P' || c == 'T')
			{
				continue;
			}
			else if(Character.isDigit(c))
			{
				String tmp = "";
				do
				{
					tmp += chars[i];
					i++;
				} while(Character.isDigit(chars[i]));
				char c2 = chars[i];
				switch(c2)
				{
				case 'H':
					hour = Integer.parseInt(tmp);
					break;
				case 'M':
					minute = Integer.parseInt(tmp);
					break;
				case 'S':
					second = Integer.parseInt(tmp);
					break;
				}
			}
		}
		return hour + "," + minute + "," + second;
	}
	
	public int getParentId()
	{
		return this.parentId;
	}
	public Date getUploadDate()
	{
		return this.dt;
	}
	public String getHoursMinutesSeconds()
	{
		return this.durationHoursMinutesSeconds;
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
