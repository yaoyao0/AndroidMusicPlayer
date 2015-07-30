package com.example.Beans;


//conserve variables

public class Constant {
	public static final int STATUS_STOP=0X11;//0x11 stop;0x12 play ;0x13 pause
	public static final int STATUS_PLAY=0X12;
	public static final int STATUS_PAUSE=0X13;
	public static final int PLAY_PAUSE = 1;
	public static final int PLAY_STOP = 2;
	public static final int MUSIC_PRE = 3;
	public static final int MUSIC_NEXT = 4;
	public static final int BROADCAST_FROM_MAIN = 5;
	public static final String CTL_ACTION = "com.example.music.CTL_ACTION";
	public static final String UPDATE_ACTION = "com.example.music.UPDATE_ACTION";	
	public static final String PLAY="com.example.play";//播放
	public static final String PASE="com.example.pase";//停止
	public static final String PLAY_ACTIVITY="com.example.stop";//播放暂停
	public static final String UPDATE_PLAY="com.example.update";//service,发送更新，控制播放界面的UI
}
