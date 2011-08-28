package dk.borgstrup.ward.client;

import java.net.UnknownHostException;

import android.app.Application;
import android.os.Bundle;
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
	
	public boolean connectTo( ServerInfo server ) {
		conn = new WardConnection(server.getHost(), server.getPort());
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

	@Override
	public void receivedMessage(int message, Bundle data) {
		switch (message) {
		case Messages.GET_PLAYLIST:
			playlist = new Playlist( data.getStringArray(Messages.EXTRA_PLAYLIST_ITEMS) );
			break;
		}
	}

}
