package com.example.Beans;

import android.app.Application;


public class Music	extends Application {
	
	private String filename;
	private String Title;
	private int   duration;
	private	String artist;
	private String location;
	private int mMax;
	private int position;

	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getData() {
		return location;
	}
	public void setData(String location) {
		this.location = location;
	}
	public int getmMax() {
		return mMax;
	}
	public void setmMax(int mMax) {
		this.mMax = mMax;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	

}
