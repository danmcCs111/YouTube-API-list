package YoutubeApiList;

import YoutubeApiList.SqlConvert.SqlType;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class YoutubeApiList 
{
    private static final String [] 
    	OPERATION_OPTIONS = new String [] {
    		"showResult", "collectDuration"
    };
    private static final int [] []
    	DEFAULT_TIMESPAN = new int [] [] {
	    	{Calendar.MONTH, -6}, {Calendar.MINUTE, 0}
    };
    private int [] []
    	timespanBeginEnd = DEFAULT_TIMESPAN;
    
    private String
    	operation,
		sqlType,
		apiKey,
		handleName,
		absoluteFileLocationInsert;
    private int
    	parentId;
    private long 
    	lastTimestamp;
//    	beginTimestamp,//API limited.
//    	endTimestamp;
    private String [] 
    	videoIds = null;
    private LoadingDisplay 
    	loadingDisplay;
    
    private YoutubeApiList()
    {
    	loadingDisplay = new LoadingDisplay();
    }
    
    public YoutubeApiList(
    		String operation, String sqlType, String apiKey, String handleName, String absoluteFileLocationInsert,
    		int parentId, long lastTimestamp)
    {
    	this();
    	this.operation = operation;
		this.sqlType = sqlType;
		this.apiKey = apiKey;
		this.handleName = handleName;
		this.absoluteFileLocationInsert = absoluteFileLocationInsert;
		this.parentId = parentId;
		
		this.lastTimestamp = lastTimestamp;
    }
    
    public YoutubeApiList(
    		String operation, String sqlType, String apiKey, String handleName, String absoluteFileLocationInsert,
    		int parentId, long beginTimestamp, long endTimestamp)
    {
    	this();
    	this.operation = operation;
		this.sqlType = sqlType;
		this.apiKey = apiKey;
		this.handleName = handleName;
		this.absoluteFileLocationInsert = absoluteFileLocationInsert;
		this.parentId = parentId;
		
		this.lastTimestamp = beginTimestamp;
//		this.beginTimestamp = beginTimestamp;
//		this.endTimestamp = endTimestamp;
    }
    
    public LoadingDisplay getLoadingDisplay()
    {
    	return loadingDisplay;
    }
    
    public void setVideoIds(String [] videoIds)
    {
    	this.videoIds = videoIds;
    }
    
    public int [] getTimespanScan(long timestamp)
    {
    	Date d = new Date(timestamp);
    	Date current = Calendar.getInstance().getTime();
    	Calendar cal = Calendar.getInstance();
    	cal.add(timespanBeginEnd[0][0], timespanBeginEnd[0][1]);
    	
    	int [] retTimeAdjust = new int [2];
    	if (timestamp == -1)
    	{
    		System.out.println("default: " + d.toString());
    		return timespanBeginEnd[0];
    	}
    	else
    	{
    		long timeDiff = current.getTime() - d.getTime();
    		long days = (timeDiff / 1000 / 60 / 60 / 24);//from Milliseconds to days.
    		int second = (int) (timeDiff / 1000);//from Milliseconds to days.
    		System.out.println("days lapsed: " + days);
    		retTimeAdjust[0] = Calendar.SECOND;
    		retTimeAdjust[1] = -second + 1; // add one second to not include current.
    	}
    	return retTimeAdjust;
    }
    
    public void runOperation() 
    {
    	if(operation.equals(OPERATION_OPTIONS[0]))
    	{
    		YoutubeChannelVideosCollector ycvc = new YoutubeChannelVideosCollector();
    		int timeSpan[] = getTimespanScan(lastTimestamp);
    		
    		ArrayList<YoutubeChannelVideo> ycvs;
			try {
				ycvs = ycvc.collectYoutubeChannelVideos(
						parentId, apiKey, handleName, timeSpan[0], timeSpan[1]);
			} catch (IOException e) {
				e.printStackTrace();
				loadingDisplay.setError(e.getMessage());
				return;
			}
    		
    		String sql = SqlConvert.convertYoutubeChannelVideos(ycvs, SqlType.getType(sqlType));
    		System.out.println(sql);
    		File f = new File(absoluteFileLocationInsert);
    		PathUtility.writeStringToFile(f, sql);
    		
    		for(YoutubeChannelVideo ycv : ycvs)
    		{
    			System.out.println(
    					"Date Time: " + ycv.getUploadDate() + 
    					" | Video Title: " + ycv.getFilteredTitle() + 
    					" | Video ID: " + ycv.getVideoUrl() + 
    					" | Thumbnail URL: " + ycv.getImageUrl());
    		}
    	}
    	else if(operation.equals(OPERATION_OPTIONS[1])) //collect duration
    	{
    		YoutubeChannelVideosCollector ycvc = new YoutubeChannelVideosCollector();
    		String sql = "";
    		for(String videoId : videoIds)
    		{
    			String duration = ycvc.collectYoutubeVideoDuration(videoId, apiKey);
    			if(duration == null)
    				continue;
    			
    			sql += SqlConvert.createUpdateYoutubeChannelVideo(duration, videoId, SqlType.SQLite);
    		}
    		System.out.println(sql);
    		File f = new File(absoluteFileLocationInsert);
    		PathUtility.writeStringToFile(f, sql);
    	}
    	loadingDisplay.setCompleted();
    }
    
    public static YoutubeApiList parseArgs(String [] args)
    {
    	YoutubeApiList yal = null;
    	
    	if(args.length < 7)
    	{
    		System.out.println(
    				"Enter: \n" + 
    				"1) Operation, \n" + 
    				"2) SQL type (SQL, SQLITE), \n" +		
    				"3) API Key, \n" +
    				"4) Parent Primary Key, \n" +
    				"5) Channel Name, \n" +
    				"6) Last Timestamp \n" + 
    				"7) Absolute File Output Path For Insert \n"
    		);
    		return null;
    	}
    	
    	String 
    		operation = args[0],
	    	sqlType = args[1],
	    	apiKey = args[2],
	    	handleName = args[4];
    	int 
    		parentId = Integer.valueOf(args[3]);
    	
		long lastTimestamp = Long.valueOf(args[5]);
		String absoluteFileLocationInsert = args[6];
		yal = new YoutubeApiList(
				operation, sqlType, apiKey, handleName, absoluteFileLocationInsert, 
				parentId, lastTimestamp);
		yal.getLoadingDisplay().setTitleText(handleName);
    	if(args.length > 7)
    	{
    		String [] videoIds = new String[args.length-7];
    		for(int i = 7; i < args.length; i++)
    		{
    			videoIds[i-7] = args[i];
    		}
    		yal.setVideoIds(videoIds);
    	}
    	
    	return yal;
    }
    
    public static void main(String[] args) throws IOException 
    {
    	YoutubeApiList yal = parseArgs(args);
    	yal.runOperation();
    }
    
}