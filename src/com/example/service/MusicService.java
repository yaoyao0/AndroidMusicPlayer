package com.example.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.Beans.Constant;
import com.example.Beans.Music;
import com.example.music.MainActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;



public class MusicService extends Service{
	List<Object> musiclists = new ArrayList<Object>();
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	List<Object> list1 =  new ArrayList<Object>();
	MyReceiver serviceReceiver;
	public static MediaPlayer mPlayer;
	MyThread myThread=new MyThread();
	Thread td=new Thread(myThread);

	int status = 0x11;//0x11 stop;0x12 play ;0x13 pause
	int count = 0;
    int flog = 0;
    int current = 0;
  
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		flog=0;
		mPlayer.stop();
		mPlayer.release();
		unregisterReceiver(serviceReceiver);
		super.onDestroy();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
			super.onCreate();
		flog=1;
		musicList();
		count = list.size();
		current = MainActivity.current_position;
		Log.e("Service", "onStart");
		serviceReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.CTL_ACTION);
		registerReceiver(serviceReceiver, filter);			
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				current++;
				if (current >= count) {
					current = 0;
				}
				String filename = ((Music) musiclists.get(current)).getData();
				playMusic(filename);
				Log.e("current----", String.valueOf(current));
				//change current UI including songInformation. time
				 Intent in=new Intent(Constant.UPDATE_PLAY);
				 in.putExtra("title", 1);
				 in.putExtra("current", current);
				 in.putExtra("completion_current", ((Music) musiclists.get(current)).getTitle());
				 sendBroadcast(in);
				
			}
		});
	
	
	}

	

	class MyThread implements Runnable{
//send progress of music every 0.2s
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(mPlayer.isPlaying()){
				Intent intent =new Intent(Constant.UPDATE_PLAY);
				intent.putExtra("title", 2); 
				intent.putExtra("progress", mPlayer.getCurrentPosition()*100/mPlayer.getDuration());
				sendBroadcast(intent);				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
	}
	
	
	
	 private void playMusic(String path)   
	    {   mPlayer.reset(); 
	        try   
	        {   
	        	  
	        	mPlayer.setDataSource(path);   
	        	mPlayer.prepare();   
	        	mPlayer.start();  
	        	MyThread myThread=new MyThread();
				Thread td=new Thread(myThread);
				td.start();
	        }catch (IOException e){}   
	    }   
		

		public void musicList() {
			//get file from specified path and display on list
			String[] music = new String[] { Media._ID, Media.DISPLAY_NAME,
					Media.TITLE, Media.DURATION, Media.ARTIST, Media.DATA };
			Cursor cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI,
					music, null, null, null);
			while (cursor.moveToNext()) {
				Music temp = new Music();
				temp.setFilename(cursor.getString(1));
				temp.setTitle(cursor.getString(2));
				temp.setDuration(cursor.getInt(3));
				temp.setArtist(cursor.getString(4));
				temp.setData(cursor.getString(5));
				musiclists.add(temp);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", cursor.getString(1));
				map.put("artist", cursor.getString(4));
				list.add(map);
				
			}
		}
	
	public class MyReceiver extends BroadcastReceiver {
		//receive broadcast from Playing and change status of song
		@Override
		public void onReceive(Context context, Intent intent) {
			int control = intent.getIntExtra("control", -1);
			switch (control) {
			case Constant.PLAY_PAUSE: {				
				if (status == Constant.STATUS_STOP) {
					playMusic( ((Music) musiclists.get(current)).getData());
					status = Constant.STATUS_PLAY;
				}			
				else if (status == Constant.STATUS_PLAY) {
					mPlayer.pause();
					status = Constant.STATUS_PAUSE;
				}			
				else if (status == Constant.STATUS_PAUSE) {
					mPlayer.start();
					status = Constant.STATUS_PLAY;					
				}
				break;
			}
			case Constant.PLAY_STOP: {			
				if (status == Constant.STATUS_PLAY || status == Constant.STATUS_PAUSE) {
					mPlayer.stop();
					status = Constant.STATUS_STOP;
				}
				break;
			}
			case Constant.MUSIC_PRE: {
				current--;
				if (current < 0) {
					current = count-1;
				}
				playMusic( ((Music) musiclists.get(current)).getData());
				status = Constant.STATUS_PLAY;
				break;
			}
			case Constant.MUSIC_NEXT: {
				current++;
				if (current >= count) {
					current = 0;
				}
				playMusic( ((Music) musiclists.get(current)).getData());
				status = Constant.STATUS_PLAY;
				break;
			}
			case Constant.BROADCAST_FROM_MAIN: {
				current = intent.getIntExtra("current", -1);;
				playMusic( ((Music) musiclists.get(current)).getData());
				break;
			}
			}
			
			 MyThread myThread=new MyThread();
			 Thread td=new Thread(myThread);
			 td.start();
		
			
			
		}
	}
}