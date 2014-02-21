package com.example.healthtube;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener  {

	String idPlayList;
	ListView videosListView;
	YouTubePlayerView youtubePlayerView; 
	ImageView thumb;
	String URL_VIDEO;
	String keyaux1 = "AIzaSyBI";
	String keyaux2 = "Is0u0NXhsZg";
	String keyaux3 = "uv8nCNvSzU";
	String keyaux4 = "mflTt7K1Ek";
	String KEY_DEVELOPER = keyaux1 + keyaux2 + keyaux3 + keyaux4;
	ToggleButton listExtendButton;
	ToggleButton playerExtendButton;
	Button changeChannelButton;
	int height = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Set the layout
		setContentView(R.layout.youtube);
		
		//General Health as the default playlist and obtain all its videos
		idPlayList="PLOhl4anP1Mp3FjD_g3KOEcYWGRrPZPTI0";
		new Thread(new GetYouTubeUserVideosTask(responseHandler, idPlayList)).start();
		
		youtubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		videosListView = (ListView) findViewById(R.id.listListView);
		listExtendButton =  (ToggleButton) findViewById(R.id.listExtendButton);
		
		listExtendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(listExtendButton.isChecked() && playerExtendButton.isChecked()){
					
					playerExtendButton.performClick();
				}
				
				if(listExtendButton.isChecked()){	
					
					youtubePlayerView.setVisibility(View.GONE);
					LayoutParams params = (LayoutParams) videosListView.getLayoutParams();
					height = params.height;
					params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;	
				}
				
				else{

					youtubePlayerView.setVisibility(View.VISIBLE);
					LayoutParams params = (LayoutParams) videosListView.getLayoutParams();
					params.height = height;
				}
				
			}
		});

		playerExtendButton =  (ToggleButton) findViewById(R.id.playerExtendButton);
		
		playerExtendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(listExtendButton.isChecked() && playerExtendButton.isChecked()){
					
					listExtendButton.performClick();
				}
				
				if(playerExtendButton.isChecked()){			
					
					videosListView.setVisibility(View.GONE);
					LayoutParams params = (LayoutParams) youtubePlayerView.getLayoutParams();
					height = params.height;
					params.height = android.view.ViewGroup.LayoutParams.FILL_PARENT;	
				}
				
				else{
					
					videosListView.setVisibility(View.VISIBLE);
					LayoutParams params = (LayoutParams) youtubePlayerView.getLayoutParams();
					params.height = height;
				}
			}
		});
		
		changeChannelButton = (Button) findViewById(R.id.changeChannelButton);
		
		changeChannelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					
				showsDialog();
				
			}
		});

	}
	
    protected void onStop() {
        responseHandler = null;
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
    }
    protected void onStart() {
        super.onStart();
    }
    protected void onResume() {
        super.onResume();
    }
	
    //Handler that is called once the playlist videos have been retrieved
    Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
			fillListView(msg);
		};
	};
			
	/**
	 * Fills the listview of the layout with the videos fetched by getYoutubeFeed
	 * @param msg message that contains all the videos
	 */	
	public void fillListView(Message msg){
		
		ArrayList<Video> listVideos = (ArrayList) msg.getData().get("LISTVIDEOS");
	
		videosListView.setAdapter(new AdapterList(this, R.layout.entry, listVideos){
        	@Override
			public void onEntry(Object entry, View view) {
		        if (entry != null) {
		        	
		            TextView superiorText = (TextView) view.findViewById(R.id.superiorTextView); 
		            if (superiorText != null) 
		            	superiorText.setText(((Video) entry).getTitle()); 
		            		           		            
		            thumb = (ImageView) view.findViewById(R.id.imageView);

		            String aux = ((Video) entry).getThumbUrl();
		            ImageDownloader imageDownloader = new ImageDownloader();
		            imageDownloader.download(aux, thumb);
		        }
        	}
	    });
        
        videosListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> pariente, View view, 
					int posicion, long id) {
				
				Video chosen = (Video)pariente.getItemAtPosition(posicion);	
				String urlVideo = chosen.getUrl();
				
				
				String aux = getYoutubeVideoId(urlVideo);
				URL_VIDEO = aux;
				
		        youtubePlayerView.initialize(KEY_DEVELOPER, new YouTubePlayer.OnInitializedListener() {
		        
					@Override
					public void onInitializationFailure(Provider arg0,
							YouTubeInitializationResult arg1) {
						// TODO Auto-generated method stub
						Log.d("ERROR DISPLAYING VIDEO", URL_VIDEO);
						
					}

					@Override
					public void onInitializationSuccess(Provider arg0,
							YouTubePlayer player, boolean wasRestored) {
						
						// TODO Auto-generated method stub
						if(!wasRestored){		
							Log.d("YOUTUBE", "URL: " + URL_VIDEO);
							player.cueVideo(URL_VIDEO);
						}
					}
				});
			}
		});	    
	}
	
    /**
     * Process the URL to obtain the video ID 
     * @param input Video URL
     * @return Video ID
     */	
	public static String getYoutubeVideoId(String input)
	 {
		
		Pattern p = Pattern.compile("http.*\\?v=([a-zA-Z0-9_\\-]+)(?:&.)*([a-zA-Z=_]*)");
		Matcher m = p.matcher(input);

		if (m.matches()) {
			input = m.group(1);
		}
				
		return input;
	 }
	
    /**
     * Shows a dialog to change the playlist
     */	
	void showsDialog(){
		final CharSequence[] items={"General Health", "Back", "Knee", "Ankle", "Neck"};
		AlertDialog.Builder builder3=new AlertDialog.Builder(this);
		builder3.setTitle("Select playlist").setItems(items, new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			switch(which) {
			//General Health
		    case 0:
				idPlayList = "PLOhl4anP1Mp3FjD_g3KOEcYWGRrPZPTI0"; 
				new Thread(new GetYouTubeUserVideosTask(responseHandler, idPlayList)).start();

				break;
		    //Back
		    case 1:
				idPlayList = "PLOhl4anP1Mp3GR-PVzgH2bBFZbW1fX8HS";
				new Thread(new GetYouTubeUserVideosTask(responseHandler, idPlayList)).start();
		        break;
		    //Knee
		    case 2:
				idPlayList = "PLOhl4anP1Mp1vJKmmqGAcu10h5OSbx4zf";
				new Thread(new GetYouTubeUserVideosTask(responseHandler, idPlayList)).start();
				break;
			//Ankle
		    case 3:
				idPlayList = "PLOhl4anP1Mp3hUCb87AM_Gj87__7cJJJc";
				new Thread(new GetYouTubeUserVideosTask(responseHandler, idPlayList)).start();
				break;
			//Neck
		    case 4:
				idPlayList = "PLOhl4anP1Mp1N774MgOoEDWwC6HwC5pn7";
				new Thread(new GetYouTubeUserVideosTask(responseHandler, idPlayList)).start();
				break;

		}
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Selected playlist: "+items[which], Toast.LENGTH_LONG).show();
		}
		});
		builder3.show();
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitializationSuccess(Provider arg0, YouTubePlayer arg1,
			boolean arg2) {
		// TODO Auto-generated method stub
		
	}
}
