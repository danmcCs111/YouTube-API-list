package YoutubeApiList;
import java.sql.Timestamp;
import java.util.ArrayList;

public class SqlConvert 
{
	private static final String 
		INSERT_PREFIX = "Insert Into videodatabase.videoYoutube (" +
				"Id_VideoYoutube_VideoYoutubeDatabase, " + 
				"ParentID_VideoYoutube_VideoYoutubeDatabase," + 
				"Title_VideoYoutube_VideoYoutubeDatabase, " + 
				"Url_VideoYoutube_VideoYoutubeDatabase, " +
				"PosterImageUrl_VideoYoutube_VideoYoutubeDatabase, " +
				"UploadDate_VideoYoutube_VideoYoutubeDatabase, " +
				"InsertDate_VideoYoutube_VideoYoutubeDatabase)" + 
				" values (",
		INSERT_SUFFIX = "CURRENT_TIMESTAMP);";
	
	public static String convertYoutubeChannelVideos(ArrayList<YoutubeChannelVideo> youtubeChannelVideos)
	{
		String sql = "";
		for(YoutubeChannelVideo ycv : youtubeChannelVideos)
		{
			sql += INSERT_PREFIX;
			sql += surroundQuotesComma(ycv.getVideoId());
			sql += ycv.getParentId() + ", ";
			sql += surroundQuotesComma(ycv.getFilteredTitle());
			sql += surroundQuotesComma(ycv.getVideoUrl());
			sql += surroundQuotesComma(ycv.getImageUrl());
			Timestamp t = new Timestamp(ycv.getUploadDate().getTime());
			sql += surroundQuotesComma(t.toLocalDateTime().toString());
			sql += INSERT_SUFFIX;
			sql += "\n";
		}
		return sql;
	}
	
	
	private static String surroundQuotesComma(String txt)
	{
		return surroundQuotes(txt) + ", "; 
	}
	private static String surroundQuotes(String txt)
	{
		return "\"" + txt + "\"";
	}
}
