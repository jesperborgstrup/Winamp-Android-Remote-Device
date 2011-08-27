package dk.borgstrup.ward.client;

import java.net.UnknownHostException;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements WardConnectionListener {
	
	private WardConnection conn;
	
	private SeekBar volumeControl;
	private Button reConnectButton;
	
	private Button previousButton;
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button nextButton;
	
	private Resources res;
	
	private String host;
	private int port;
	
	private TextView nowPlayingLabel;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        res = getResources();
        initializeComponents();
        
        host = getIntent().getStringExtra(Messages.EXTRA_SERVER_HOST);
        port = getIntent().getIntExtra(Messages.EXTRA_SERVER_PORT, 9273);
        
       	conn = new WardConnection( host, port );
        conn.addListener( this );
        
    }
    
    @Override
    public void onStart() {
    	super.onStart();
      	try {
			if (!conn.Connect()) {
				finish();
			}
		} catch (UnknownHostException e) {
			Toast( R.string.could_not_connect, host ); 
			finish();
		}
    }
  
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
    public void connect() {
        try {
			if (conn.Connect()) {
			    Toast( "Connected!" );
			    updateUI();
			} else {
				Toast("Failed to connect...");
			}
		} catch (UnknownHostException e) {
			Toast( R.string.could_not_connect, host ); 
			finish();
		}
    }
    
    private void updateUI() {
        conn.requestVolume();
        conn.requestPlaybackStatus();
        conn.requestCurrentTitle();
    }

	private void initializeComponents() {
        previousButton = (Button)findViewById(R.id.mainPreviousButton);
        playButton = (Button)findViewById(R.id.mainPlayButton);
        pauseButton = (Button)findViewById(R.id.mainPauseButton);
        stopButton = (Button)findViewById(R.id.mainStopButton);
        nextButton = (Button)findViewById(R.id.mainNextButton);
        reConnectButton = (Button)findViewById(R.id.mainReConnectButton);

        nowPlayingLabel = (TextView)findViewById(R.id.mainNowPlaying);
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
					conn.setVolume(progress);
			}
		});
	}

	private void initializeButtons() {
		previousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				conn.previous();
			}
		});
        playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				conn.play();
			}
		});
        pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				conn.pause();
			}
		});
        stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				conn.stop();
			}
		});
        nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				conn.next();
			}
		});
        
        reConnectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				connect();
			}
		});
	}
    
    private void Toast(String msg) {
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    	Settings.LogI( "Toast: " + msg );
    }

    private void Toast(int rId) {
    	String s = getResources().getString(rId);
    	Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    	Settings.LogI( "Toast: " + s );
    }

    private void Toast(int rId, Object... formatArgs) {
    	String s = getResources().getString(rId, formatArgs);
    	Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    	Settings.LogI( "Toast: " + s );
    }

	@Override
	public void receivedMessage(final byte message, final Bundle data) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				switch (message) {
				case Messages.GET_VOLUME:
					volumeControl.setProgress( data.getInt( Messages.EXTRA_VOLUME ) ) ;
					break;
				case Messages.GET_PLAYBACK_STATUS:
					int status = data.getInt( Messages.EXTRA_PLAYBACK_STATUS );
					playButton.setEnabled( status != Messages.PLAYBACK_PLAYING );
					pauseButton.setEnabled( status != Messages.PLAYBACK_PAUSE );
					stopButton.setEnabled( status != Messages.PLAYBACK_NOT_PLAYING );
					break;
				case Messages.GET_CURRENT_TITLE:
					String title = data.getString( Messages.EXTRA_CURRENT_TITLE );
					nowPlayingLabel.setText( res.getString( R.string.now_playing_string, title ) );
				}
			}
		});
		Settings.LogI( "MainActivity receivedMessage: " + message + " ("+data.toString()+")" ); 
	}
}