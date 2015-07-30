package com.example.music;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.Beans.Constant;
import com.example.Beans.Music;
import com.example.service.MusicService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	List<Object> musiclists = new ArrayList<Object>();
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	Button play_pause, stop, onplay, downplay, close, exit;	
	public static int current_position=-1;//current position of music
	public static int county=0;//size of list
	Intent intentservice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		musicList();
		intentservice = new Intent(this, MusicService.class);
		startService(intentservice);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}	

	public void musicList() {

		String[] music = new String[] { Media._ID, Media.DISPLAY_NAME,
				Media.TITLE, Media.DURATION, Media.ARTIST, Media.DATA,};
		Cursor cs = getContentResolver().query(Media.EXTERNAL_CONTENT_URI,
				music,null, null, null);//MediaStore.Audio.		
		while (cs.moveToNext()) {
			Music temp = new Music();
			temp.setFilename(cs.getString(1));
			temp.setTitle(cs.getString(2));
			temp.setDuration(cs.getInt(3));
			temp.setArtist(cs.getString(4));
			temp.setData(cs.getString(5));
			musiclists.add(temp);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", cs.getString(1));
			map.put("artist", cs.getString(4));
			list.add(map);
		}
		 county = list.size();

		final ListView listview = (ListView) findViewById(R.id.musics);
		final SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.musicsshow, new String[] { "name", "artist" },
				new int[] { R.id.name, R.id.artist });
		
		listview.setAdapter(adapter);	
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int current, long id) {
				current_position = current;
				Intent intent=new Intent(Constant.CTL_ACTION);
				intent.putExtra("control",Constant.BROADCAST_FROM_MAIN);
				intent.putExtra("current", current);				
				sendBroadcast(intent);
				Intent intent1= new Intent(MainActivity.this,Playing.class);
				startActivity(intent1);
			}
		});
		
		//LongClick and choose yes to delete this music file
listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(final AdapterView<?> listView, final View view,
					final int current, long id) {
				// TODO Auto-generated method stub
				//dialog();
					AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setMessage(R.string.confirm);
				builder.setTitle(R.string.hint);
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {						
						
							// TODO Auto-generated method stub
						String spath = ((Music) musiclists.get(current)).getData();
					
				//	String path =Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+spath; 
						if(MusicService.mPlayer.isPlaying()){
							MusicService.mPlayer.stop();
							
						}
						deleteFile(spath);	
						list.remove(current);
						adapter.notifyDataSetChanged();
						dialog.dismiss();					
					}				
					} );  					
				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				builder.create().show();

				return true;
			}		
		});
	}
	public boolean deleteFile(String sPath) {   
	     
	   File file = new File(sPath);      
	    if (file.isFile() && file.exists()) {   
	        file.delete();   
	         
	    }   
	    return true;
	}  

	


}