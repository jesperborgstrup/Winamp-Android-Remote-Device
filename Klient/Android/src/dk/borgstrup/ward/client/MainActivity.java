package dk.borgstrup.ward.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import dk.borgstrup.ward.client.connection.WardConnectionListener;

public class MainActivity extends Activity implements WardConnectionListener {
	
	private SeekBar volumeControl;
	
	private Button previousButton;
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button nextButton;
	
	private Resources res;
	
	private WardApplication app;
	
	private TextView nowPlayingLabel;
	private ProgressDialog setupDialog;
	
	private int messageCounter = 0;
	private static int MESSAGES_TO_RECEIVE_DURING_SETUP = 3;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        app = (WardApplication)getApplication();
        
        res = getResources();
        initializeComponents();
        
        app.conn.addListener( this );
        
        setupDialog = new ProgressDialog(this);
        setupDialog.setTitle( R.string.setting_up_connection );
        setupDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setupDialog.setCancelable(true);
        setupDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		});
    }
  
    
    @Override
    public void onStart() {
    	super.onStart();
    	messageCounter = 0;
    	updateUI();
    	
    	setupDialog.show();
    }
  
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
    private void updateUI() {
    	app.conn.requestVolume();
    	app.conn.requestPlaybackStatus();
    	app.conn.requestCurrentTitle();
    }

	private void initializeComponents() {
        previousButton = (Button)findViewById(R.id.mainPreviousButton);
        playButton = (Button)findViewById(R.id.mainPlayButton);
        pauseButton = (Button)findViewById(R.id.mainPauseButton);
        stopButton = (Button)findViewById(R.id.mainStopButton);
        nextButton = (Button)findViewById(R.id.mainNextButton);

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
					app.conn.setVolume(progress);
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
        stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.conn.stop();
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
					break;
				}
			}
		});
		Settings.LogI( "MainActivity receivedMessage: " + message + " ("+data.toString()+")" ); 

		messageCounter++;
		if (messageCounter >= MESSAGES_TO_RECEIVE_DURING_SETUP && setupDialog.isShowing()) {
			setupDialog.dismiss();
		}
	}
}