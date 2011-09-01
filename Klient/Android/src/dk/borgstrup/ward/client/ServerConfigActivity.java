package dk.borgstrup.ward.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import dk.borgstrup.ward.client.connection.ServerInfo;
import java.util.regex.*;

public class ServerConfigActivity extends Activity {
	
	private ListView list;
	
	private ServerInfoAdapter adapter;
	private WardApplication app;
	
	private Resources res;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_config);
        
        app = (WardApplication)getApplication();
        
        this.res = getResources();
        
        initializeListView();
        
        if (app.serverAdmin.getConfiguration().getServers().size() == 0) {
        	addServerClicked();
        }
    }
    
    private void initializeListViewMenu(int position) {
		final ServerInfo server = (ServerInfo)adapter.getItem(position);
		final AlertDialog.Builder builder = new AlertDialog.Builder(ServerConfigActivity.this);
		builder.setTitle( server.getName() );

		final String[] options = { res.getString( R.string.server_config_remove ),
				                   res.getString( R.string.server_config_wake ) };

		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0: // REMOVE
					app.serverAdmin.removeServer(server);
					adapter.notifyDataSetChanged();
					break;
				case 1: // WAKE
					app.serverAdmin.wakeServer(server);
					break;
				}
			}
		});
		builder.show();
	}

	private void initializeListView() {
        this.adapter = new ServerInfoAdapter(this, app.serverAdmin);

        list = (ListView)findViewById(R.id.server_config_list);
        list.setAdapter( this.adapter );
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				initializeListViewMenu(position);
			}
		});

        list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				initializeListViewMenu(position);
				return true;
			}
		});
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.server_config_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.server_config_menu_add:
    		addServerClicked();
    		return true;
    	default:
    		return false;
    	}
    }
    
    private void addServerClicked() {
    	final Dialog dialog = new Dialog(this);
    	dialog.setContentView(R.layout.add_server_dialog);
    	dialog.setTitle( R.string.server_config_add );
    	
    	LayoutParams params = dialog.getWindow().getAttributes(); 
        params.width = LayoutParams.FILL_PARENT; 
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params); 
    	
    	final Button addButton = (Button)dialog.findViewById(R.id.add_server_dialog_add);
    	final EditText textName = (EditText)dialog.findViewById(R.id.add_server_dialog_name);
    	final EditText textHost = (EditText)dialog.findViewById(R.id.add_server_dialog_host);
    	final EditText textPort = (EditText)dialog.findViewById(R.id.add_server_dialog_port);
    	final EditText textMac  = (EditText)dialog.findViewById(R.id.add_server_dialog_mac);
    	
    	addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = textName.getText().toString();
				String host = textHost.getText().toString();
				String portText = textPort.getText().toString();
				String macText = textMac.getText().toString();
				String mac = null;
				int port = -1;
				
				try {
					port = Integer.parseInt(portText);
				} catch (NumberFormatException e) {
				}

				if (port <= 0 || port >= 65536) {
					Toast.makeText(ServerConfigActivity.this, R.string.add_server_dialog_invalid_port, Toast.LENGTH_LONG).show();
					return;
				}
				
				if ( name.trim().equals( "" ) ) {
					Toast.makeText(ServerConfigActivity.this, R.string.add_server_dialog_enter_name, Toast.LENGTH_LONG).show();
					return;
				}
				
				if ( host.trim().equals( "" ) ) {
					Toast.makeText(ServerConfigActivity.this, R.string.add_server_dialog_enter_host, Toast.LENGTH_LONG).show();
					return;
				}
				
				if ( !macText.trim().equals( "" ) ) {
					Pattern p = Pattern.compile( "^([0-9a-fA-F]{2})[-:\\s]?([0-9a-fA-F]{2})[-:\\s]?([0-9a-fA-F]{2})[-:\\s]?([0-9a-fA-F]{2})[-:\\s]?([0-9a-fA-F]{2})[-:\\s]?([0-9a-fA-F]{2})$" );
					Matcher m = p.matcher( macText );
					if ( m.find() ) {
						mac = m.group(1) + m.group(2) + m.group(3) + m.group(4) + m.group(5) + m.group(6);
						mac = mac.toUpperCase();
					} else {
						Toast.makeText(ServerConfigActivity.this, R.string.add_server_dialog_invalid_mac, Toast.LENGTH_LONG).show();
						return;
					}
				}
				
				if (app.serverAdmin.getConfiguration().nameExists(name)) {
					Toast.makeText(ServerConfigActivity.this, R.string.add_server_dialog_name_exists, Toast.LENGTH_LONG).show();
					return;
				}
				
				ServerInfo server = new ServerInfo(name, host, port, mac);
				app.serverAdmin.addServer(server);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
				
			}
		});
    	
    	dialog.setCancelable(true);
    	dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
		        if (app.serverAdmin.getConfiguration().getServers().size() == 0) {
		        	ServerConfigActivity.this.finish();
		        }
			}
		});
    	dialog.show();

}

}
