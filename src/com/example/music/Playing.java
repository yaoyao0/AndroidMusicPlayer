package com.example.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.Beans.Constant;
import com.example.Beans.Music;
import com.example.service.MusicService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Playing extends Activity implements
		android.view.View.OnClickListener {

	private TextView tv,beginTime,endTime;
	int current =0;   //index of current music
	int status = 0x11;
	MyBroadCast mb;//broadcast UI update

	List<Object> musiclists = new ArrayList<Object>();
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	 Button previous, next, play;
	 SeekBar progressBar1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.musicservice);
		musicList();
		init();
		setClickListener();
		getintent();
		
		if(current!=-1){
			updateUI(current);
		if(!MusicService.mPlayer.isPlaying()){			
			if(MusicService.mPlayer.getCurrentPosition()!=0){//if mPlyaer is not playing,progressbar can set time now
			progressBar1.setProgress(MusicService.mPlayer.getCurrentPosition()*100/MusicService.mPlayer.getDuration());
			Log.e("progress", "start!!!!");
			}
		}		
		}		
		/*
		 * register Receiver
		 */
		IntentFilter fil=new IntentFilter();
		fil.addAction(Constant.UPDATE_PLAY);
		mb=new MyBroadCast();
		registerReceiver(mb, fil);
		Log.e("Playing", "onStart");

	}


	private void setClickListener() {
		previous.setOnClickListener(this);
		next.setOnClickListener(this);
		play.setOnClickListener(this);
	
		/*
		 * set seekbar to control music volume
		 */
		final AudioManager am = (AudioManager) Playing.this
				.getSystemService(Context.AUDIO_SERVICE);

		Playing.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SeekBar seekbar = (SeekBar) findViewById(R.id.SeekBar1);
		seekbar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		int progress = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		seekbar.setProgress(progress);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress,
						AudioManager.FLAG_PLAY_SOUND); 
			}
			});
		/*
		 * set progress of music
		 */
		progressBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar progressBar1) {
				if(current!=-1){					
					MusicService.mPlayer.seekTo((progressBar1.getProgress())*(MusicService.mPlayer.getDuration()/100));
			
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar progressBar1) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onProgressChanged(SeekBar progressBar1, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	//MyBroadCast can be used to receive broadcast from MusicService and update UI
	public class MyBroadCast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			int i=intent.getIntExtra("title", 0);		
			if(i==1){
				 current = intent.getIntExtra("current", 0); 
				updateUI(current);
				Log.e("receive",String.valueOf(current));
			}
			else if(i==2){			
				updateUI(current);
				progressBar1.setProgress(intent.getIntExtra("progress", 0));
			}
		}
		
	}

	public void init(){
		previous = (Button) findViewById(R.id.previous);
		next = (Button) findViewById(R.id.next);
		play = (Button) findViewById(R.id.play);
		tv = (TextView) findViewById(R.id.tv);
		beginTime = (TextView) findViewById(R.id.beginTime);
		endTime = (TextView) findViewById(R.id.duration);
		progressBar1 = (SeekBar) findViewById(R.id.progressBar1);
		progressBar1.setMax(100);
	}
	//use it to update current UI
		private void updateUI(int current) {
			// TODO Auto-generated method stub		
			this.current = current;
			tv.setText(((Music) musiclists.get(current)).getTitle());
			beginTime.setText(getTime((int)MusicService.mPlayer.getCurrentPosition()/1000));
			endTime.setText(getTime((int)MusicService.mPlayer.getDuration()/1000));
			}

		
		
	/*
	 * change type of time
	 */
		private String getTime(int times){
		String time;
		int fen=times/60;
		int second=times%60;
		if(second<10){
			time=fen+":0"+second;
		}
		else{
			time=fen+":"+second;
		}
		return time;
	}
		
		//get current position of musiclist
		private void getintent() {
			// TODO Auto-generated method stub
			current = MainActivity.current_position;			
		}

	public void musicList() {

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

	@Override
	public void onClick(View source) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Constant.CTL_ACTION);
		switch (source.getId()) {
		case R.id.play: {
			if(MusicService.mPlayer.isPlaying()){
			play.setText(R.string.play);}
			else{play.setText(R.string.pause);}
			updateUI(current);
			intent.putExtra("control", Constant.PLAY_PAUSE);
			break;
		}
		case R.id.previous: {
			intent.putExtra("control", Constant.MUSIC_PRE);
			current--;
			if(current<0){
				current = MainActivity.county-1;
			}
			updateUI(current);			
			break;
		}
		case R.id.next: {
			intent.putExtra("control", Constant.MUSIC_NEXT);
			current++;
			if (current >=MainActivity.county) {
				current = 0;
			}
			updateUI(current);
			break;
		}
		}
		sendBroadcast(intent);

	}

}
