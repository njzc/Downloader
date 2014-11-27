package cs325.congzhang.downloader;

import java.io.Serializable;

public class Picture implements Serializable{

	private String url;
	private String fileName;
	private String state;
	
	public Picture(String url, String fileName, String state)
	{
		this.url = url;
		this.fileName = fileName;
		this.state = state;
	}
	
	public String getUrl()
	{
		return this.url;
	}

	public String getFileName()
	{
		return this.fileName;
	}
	
	public String getState()
	{
		return this.state;
	}

}
