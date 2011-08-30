package dk.borgstrup.ward.client;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import dk.borgstrup.ward.client.connection.Messages;
import dk.borgstrup.ward.client.connection.ServerConfiguration;
import dk.borgstrup.ward.client.connection.ServerInfo;
import dk.borgstrup.ward.client.connection.WardConnectionListener;

public class StartActivity extends Activity implements WardConnectionListener {
	
	private Button latestButton;
	private Button selectButton;
	private Button configButton;
	
	private ServerConfiguration serverConfig;
	private ServerInfo latestServer;
	
	private Resources res;
	
	private WardApplication app;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        app = (WardApplication)getApplication();
        res = getResources();
        
        assignButtons();
        
    }
    
    @Override
    public void onStart() {
    	super.onStart();
        serverConfig = app.serverAdmin.getConfiguration();
        latestServer = serverConfig.getLatest();

        initializeButtons();
}

	private void initializeButtons() {
		if (latestServer == null) {
			latestButton.setVisibility(View.GONE);
		} else {
			latestButton.setVisibility(View.VISIBLE);
		}
		latestButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				connectTo( latestServer );
			}
		});
		
		selectButton.setEnabled( serverConfig.getServers().size() > 0 );
		
		selectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
				builder.setTitle( StartActivity.this.getResources().getString(R.string.start_select_server));
				final List<String> serverNames = StartActivity.this.serverConfig.getServerNames();
				final String[] serverNameArray = serverNames.toArray(new String[serverNames.size()]);
				builder.setItems(serverNameArray, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String serverName = serverNames.get( which );
						connectTo(serverConfig.getServerWithName(serverName));
					}
				});
				builder.show();
				
			}
		});
        
        configButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(StartActivity.this, ServerConfigActivity.class);
				startActivity( i );
			}
		});
        
        if (latestServer == null) {
        	latestButton.setVisibility( View.GONE );
        } else {
        	latestButton.setText( getResources().getString( R.string.start_latest, latestServer.getName() ) );
        }
	}

	private void assignButtons() {
		latestButton = (Button)findViewById(R.id.start_latest);
        selectButton = (Button)findViewById(R.id.start_select_server);
        configButton = (Button)findViewById(R.id.start_config_servers);
	}
    
    public void connectTo(ServerInfo server) {
    	if (app.connectTo(server, this)) {
    		app.serverAdmin.getConfiguration().setLatest(server);
    		Intent i = new Intent(this, MainActivity.class);
    		startActivity(i);
    	} else {
    		Toast.makeText(this, res.getString(R.string.could_not_connect, server.getHost()), Toast.LENGTH_LONG).show();
    	}
    }
    
	@Override
	public void receivedMessage(final int message, final Bundle data) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				switch (message) {
				case Messages.ERROR:
					int error = data.getInt( Messages.EXTRA_ERROR );
					switch (error) {
					case Messages.ERROR_WINAMP_NOT_RUNNING:
						Toast.makeText(StartActivity.this, R.string.winamp_not_running, Toast.LENGTH_SHORT).show();
						break;
					}
					break;
				}
			}
		});
		
	}

    
}
