package dk.borgstrup.ward.client;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import dk.borgstrup.ward.client.connection.Messages;
import dk.borgstrup.ward.client.connection.WardConnectionListener;

public class MainActivity extends Activity implements WardConnectionListener {
	
	private SeekBar volumeControl;
	
	private ImageButton previousButton;
	private ImageButton playButton;
	private ImageButton pauseButton;
	private ImageButton nextButton;
	
	private TextView volumeLabel;
	
	private Resources res;
	
	private WardApplication app;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        app = (WardApplication)getApplication();
        
        res = getResources();
        initializeComponents();
        
        // If the application is killed while in this activity,
        // the line 'app.conn.addListener( this );' would return
        // a NullPointerException
        if ( app.conn == null ) {
        	finish();
        	return;
        }
        
        app.conn.addListener( this );
        
    }
  
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	app.conn.removeListener( this );
    }
    
    @Override
    public void onStart() {
    	super.onStart();

    	if (app.conn.getLastError() != -1) {
    		finish();
    	} else
    		while ( !app.winamp.isInitialized() );
    	
    	updateUI();
    	
    }
  
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
    private void updateUI() {
		volumeControl.setProgress( app.winamp.getVolume() ) ;
		int percent = app.winamp.getVolume() * 100 / 255;
		volumeLabel.setText( res.getString( R.string.volume_percent, percent ) );
		playButton.setEnabled( app.winamp.getPlaybackStatus() != Messages.PLAYBACK_PLAYING );
		pauseButton.setEnabled( app.winamp.getPlaybackStatus() == Messages.PLAYBACK_PLAYING );
		MainActivity.this.setTitle( app.winamp.getCurrenTitle() );
    }

	private void initializeComponents() {
        previousButton = (ImageButton)findViewById(R.id.mainPreviousButton);
        playButton = (ImageButton)findViewById(R.id.mainPlayButton);
        pauseButton = (ImageButton)findViewById(R.id.mainPauseButton);
        nextButton = (ImageButton)findViewById(R.id.mainNextButton);
        
        volumeLabel = (TextView)findViewById(R.id.main_volume_label);

        volumeControl = (SeekBar)findViewById(R.id.mainVolumeControl);

        initializeButtons();
        
        initializeVolumeControl();
	}

	private void initializeVolumeControl() {
		volumeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser)
					app.conn.setVolume(progress);
					int percent = progress * 100 / 255;
					volumeLabel.setText( res.getString( R.string.volume_percent, percent ) );
			}
		});
	}

	private void initializeButtons() {
		previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.conn.previous();
			}
		});
        playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.conn.play();
			}
		});
        pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.conn.pause();
			}
		});
        nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.conn.next();
			}
		});
        
	}
    
	@Override
	public void receivedMessage(final int message, final Bundle data) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				switch (message) {
				case Messages.GET_VOLUME:
				case Messages.GET_PLAYBACK_STATUS:
				case Messages.GET_CURRENT_TITLE:
					updateUI();
					break;
				}
			}
		});
		Settings.LogI( "MainActivity receivedMessage: " + message + " ("+data.toString()+")" ); 

	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.main_menu_playlist:
    		Intent i = new Intent(this, PlaylistActivity.class);
    		startActivity(i);
    		return true;
    	default:
    		return false;
    	}
    }
    

}