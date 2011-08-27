package dk.borgstrup.ward.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import dk.borgstrup.ward.client.connection.ServerConfiguration;
import dk.borgstrup.ward.client.connection.ServerInfo;

public class ServerConfigActivity extends Activity {
	
	private ServerAdministrator serverAdmin;
	private ListView list;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_config);
        
        serverAdmin = new ServerAdministrator(this);
        
        serverAdmin.createDummyFile();
        serverAdmin.readConfiguration();
        
        list = (ListView)findViewById(R.id.server_config_list);
        populateList();
    }

	private void populateList() {
		ServerConfiguration servers = serverAdmin.readConfiguration();
		
		for (ServerInfo server: servers.getServers()) {
			Settings.LogI( "Server " + server.getName() );
			LinearLayout ll = new LinearLayout(this);
			TextView title = new TextView(this);
			TextView description = new TextView(this);
			title.setText( server.getName() );
			description.setText( server.getHost() + ":" + server.getPort() );
			
		}
		
	}
    
}
