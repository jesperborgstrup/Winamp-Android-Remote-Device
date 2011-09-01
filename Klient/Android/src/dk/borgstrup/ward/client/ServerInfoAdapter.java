package dk.borgstrup.ward.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dk.borgstrup.ward.client.connection.ServerAdministrator;
import dk.borgstrup.ward.client.connection.ServerConfiguration;
import dk.borgstrup.ward.client.connection.ServerInfo;

public class ServerInfoAdapter extends BaseAdapter {
	
	private ServerConfiguration config;
	private Context context;

	public ServerInfoAdapter(Context context, ServerAdministrator admin) {
		super();
		this.context = context;
		this.config = admin.getConfiguration();
	}

	@Override
	public int getCount() {
		return this.config.getServers().size();
	}

	@Override
	public Object getItem(int position) {
		return this.config.getServers().get( position );
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.server_row, null);
		}
		
		ServerInfo server = this.config.getServers().get(position);
		if (server != null) {
			TextView title = (TextView)v.findViewById(R.id.server_row_title);
			TextView description = (TextView)v.findViewById(R.id.server_row_description);
			title.setText( server.getName() );
			if ( server.getMac() == null ) {
				description.setText( server.getHost() );
			} else {
				description.setText( server.getHost() + " (MAC: "+server.getMac()+")" );
			}
		}
		
		return v;
	}

}
