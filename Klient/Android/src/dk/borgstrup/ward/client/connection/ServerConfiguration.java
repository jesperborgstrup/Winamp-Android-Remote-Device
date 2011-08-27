package dk.borgstrup.ward.client.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerConfiguration {
	
	private List<ServerInfo> servers;
	private ServerInfo latest = null;
	
	public ServerConfiguration() {
		this.servers = new ArrayList<ServerInfo>();
	}

	public ServerConfiguration(List<ServerInfo> servers, ServerInfo latest) {
		this.servers = servers;
		this.latest  = latest;
		Collections.sort( this.servers );
	}
	
	public List<ServerInfo> getServers() {
		return servers;
	}
	
	public List<String> getServerNames() {
		List<String> result = new ArrayList<String>();
		for (ServerInfo s: servers) {
			result.add( s.getName() );
		}
		return result;
	}
	
	public ServerInfo getServerWithName(String name) {
		for (ServerInfo s: servers) {
			if (s.getName().equals(name))
				return s;
		}
		return null;
	}
	
	public ServerInfo getLatest() {
		return latest;
	}
	
	public void setLatest(ServerInfo latest) {
		if (nameExists(latest))
			this.latest = latest;
	}
	
	public void addServer(ServerInfo server) {
		if (!nameExists(server)) {
			servers.add( server );
		}
	}
	
	public void removeServer(ServerInfo server) {
		if (server.equals(latest) ) {
			latest = null;
		}
		servers.remove( server );
	}
	
	public boolean nameExists(ServerInfo server) {
		return nameExists(server.getName());
	}
	
	public boolean nameExists(String server) {
		for (ServerInfo s: servers) {
			if (s.getName().equals(server))
				return true;
		}
		return false;
	}
	
}
