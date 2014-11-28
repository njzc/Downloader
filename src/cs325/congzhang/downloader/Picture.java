package cs325.congzhang.downloader;

import java.io.Serializable;

public class Picture implements Serializable{

	public long Id;
	public String Url;
	public String FileName;
	public String State;
	
	public final static String STATE_QUEUED = "Queued";
	public final static String STATE_DOWNLOADED = "Downloaded";
	public final static String STATE_DOWNLOADING = "Downloading";
	
	public Picture(long id, String url, String fileName, String state)
	{
		Id = id;
		Url = url;
		FileName = fileName;
		State = state;
	}
	

}
