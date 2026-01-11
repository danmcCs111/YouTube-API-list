package YoutubeApiList;
import java.sql.Timestamp;
import java.util.ArrayList;

public class SqlConvert 
{
	public enum SqlType
	{
		SQL("SQL"),
		SQLite("SQLite");
		
		private String sqlType;
		
		private SqlType(String sqlType)
		{
			this.sqlType = sqlType;
		}
		
		public String getTypeStr()
		{
			return this.sqlType;
		}
		
		public static SqlType getType(String sqlType)
		{
			for(SqlType st : values())
			{
				if(st.getTypeStr().equals(sqlType))
					return st;
			}
			return null;
		}
	}
	private static final String 
		INSERT_SQL = "Insert Into videodatabase.videoYoutube (",
		INSERT_SQLITE = "Insert Into videoYoutube (",
		INSERT_PREFIX = 
				"Id_VideoYoutube_VideoYoutubeDatabase, " + 
				"ParentID_VideoYoutube_VideoYoutubeDatabase," + 
				"Title_VideoYoutube_VideoYoutubeDatabase, " + 
				"Url_VideoYoutube_VideoYoutubeDatabase, " +
				"PosterImageUrl_VideoYoutube_VideoYoutubeDatabase, " +
				"UploadDate_VideoYoutube_VideoYoutubeDatabase, " +
				"InsertDate_VideoYoutube_VideoYoutubeDatabase)" + 
				" values (",
		INSERT_PREFIX_SQL = INSERT_SQL + INSERT_PREFIX,
		INSERT_PREFIX_SQLITE = INSERT_SQLITE + INSERT_PREFIX,
		INSERT_SUFFIX = "CURRENT_TIMESTAMP);";
	
	public static String convertYoutubeChannelVideos(ArrayList<YoutubeChannelVideo> youtubeChannelVideos, SqlType sqlType)
	{
		String sql = "";
		String insertPrefix = "";
		switch(sqlType) 
		{
		case SQL:
			insertPrefix = INSERT_PREFIX_SQL;
		case SQLite:
			insertPrefix = INSERT_PREFIX_SQLITE;
		}
		for(YoutubeChannelVideo ycv : youtubeChannelVideos)
		{
			sql += insertPrefix;
			sql += surroundQuotesComma(ycv.getVideoId(), sqlType);
			sql += ycv.getParentId() + ", ";
			sql += surroundQuotesComma(ycv.getFilteredTitle(), sqlType);
			sql += surroundQuotesComma(ycv.getVideoUrl(), sqlType);
			sql += surroundQuotesComma(ycv.getImageUrl(), sqlType);
			Timestamp t = new Timestamp(ycv.getUploadDate().getTime());
			sql += surroundQuotesComma(t.toLocalDateTime().toString(), sqlType);
			sql += INSERT_SUFFIX;
			sql += "\n";
		}
		return sql;
	}
	
	
	private static String surroundQuotesComma(String txt, SqlType sqlType)
	{
		switch(sqlType)
		{
		case SQL:
			return surroundQuotes(txt) + ", ";
		case SQLite:
			return surroundQuotesSqlite(txt) + ", ";
		}
		return null;
	}
	private static String surroundQuotes(String txt)
	{
		return "\"" + txt + "\"";
	}
	private static String surroundQuotesSqlite(String txt)
	{
		return "\'" + txt + "\'";
	}
}
