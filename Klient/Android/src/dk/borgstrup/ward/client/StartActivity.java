package dk.borgstrup.ward.client;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import dk.borgstrup.ward.client.connection.ServerConfiguration;
import dk.borgstrup.ward.client.connection.ServerInfo;

public class StartActivity extends Activity {
	
	private Button latestButton;
	private Button selectButton;
	private Button configButton;
	
	private ServerAdministrator serverAdmin;
	private ServerConfiguration serverConfig;
	private ServerInfo latestServer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        serverAdmin = new ServerAdministrator(this);
        serverConfig = serverAdmin.readConfiguration();
        latestServer = serverConfig.getLatest();
        
        assignButtons();
        
        initializeButtons();
    }

	private void initializeButtons() {
		latestButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				connectTo( latestServer );
			}
		});
		
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
   		Intent i = new Intent(this, MainActivity.class);
   		i.putExtra(Messages.EXTRA_SERVER_HOST, server.getHost());
   		i.putExtra(Messages.EXTRA_SERVER_PORT, server.getPort());
   		startActivity( i );
   		Toast.makeText(this, "Connecting to " +server.getName()+"...", Toast.LENGTH_SHORT).show();
    }
    
}
