package dk.borgstrup.ward.client;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import dk.borgstrup.ward.client.connection.ServerConfiguration;

public class ServerInfoAdapter extends BaseAdapter {
	
	private ServerConfiguration config;

	public ServerInfoAdapter(ServerConfiguration config) {
		this.config = config;
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
		// TODO Auto-generated method stub
		return null;
	}

}
