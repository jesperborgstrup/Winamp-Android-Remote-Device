package dk.borgstrup.ward.client;

import java.net.UnknownHostException;

import android.app.Application;
import dk.borgstrup.ward.client.connection.ServerAdministrator;
import dk.borgstrup.ward.client.connection.ServerInfo;
import dk.borgstrup.ward.client.connection.WardConnection;

public class WardApplication extends Application {
	
	public ServerAdministrator serverAdmin = null;

	public WardConnection conn = null;
	public ServerInfo server = null;
	
	public WardApplication() {
		super();
		serverAdmin = new ServerAdministrator(this);
	}
	
	public boolean connectTo( ServerInfo server ) {
		conn = new WardConnection(server.getHost(), server.getPort());
		this.server = server;
		try {
			if (conn.Connect()) {
				serverAdmin.setLatest(server);
				return true;
			}
		} catch (UnknownHostException e) {
			this.server = null;
			this.conn = null;
		}
		return false;
	}

}
