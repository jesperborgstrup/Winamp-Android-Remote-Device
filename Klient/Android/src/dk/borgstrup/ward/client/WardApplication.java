package dk.borgstrup.ward.client;

import java.net.UnknownHostException;

import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;
import dk.borgstrup.ward.client.connection.Messages;
import dk.borgstrup.ward.client.connection.ServerAdministrator;
import dk.borgstrup.ward.client.connection.ServerInfo;
import dk.borgstrup.ward.client.connection.WardConnection;
import dk.borgstrup.ward.client.connection.WardConnectionListener;

public class WardApplication extends Application implements WardConnectionListener {
	
	public ServerAdministrator serverAdmin = null;

	public WardConnection conn = null;
	public ServerInfo server = null;
	
	public Playlist playlist = null;
	
	public WardApplication() {
		super();
		serverAdmin = new ServerAdministrator(this);
	}
	
	/**
	 * Connect to a server and immediately add a listener to the connection
	 * @param server
	 * @param listener A listener if needed, else null
	 * @return
	 */
	public boolean connectTo( ServerInfo server, WardConnectionListener listener ) {
		conn = new WardConnection(server.getHost(), server.getPort());
		conn.addListener(listener);
		conn.addListener(this);
		this.server = server;
		try {
			if (conn.Connect()) {
				serverAdmin.setLatest(server);
				playlist = null;
				return true;
			}
		} catch (UnknownHostException e) {
			this.server = null;
			this.conn = null;
		}
		return false;
	}
	
	public boolean connectTo( ServerInfo server ) {
		return connectTo( server, null );
	}

	@Override
	public void receivedMessage(int message, Bundle data) {
		
		switch (message) {
		case Messages.GET_PLAYLIST:
			playlist = new Playlist( data.getStringArray(Messages.EXTRA_PLAYLIST_ITEMS) );
			break;
		}
	}

}
