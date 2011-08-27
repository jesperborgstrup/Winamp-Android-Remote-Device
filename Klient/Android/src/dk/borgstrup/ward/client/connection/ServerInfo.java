package dk.borgstrup.ward.client.connection;

public class ServerInfo implements Comparable<ServerInfo> {
	
	private String name = "";
	private String host = "";
	private int port = 9273;

	public ServerInfo() {}
	
	public ServerInfo(String name, String host, int port) {
		this.name = name;
		this.host = host;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ServerInfo) {
			ServerInfo o = (ServerInfo)other;
			if (o.getName().equals( this.getName() ) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(ServerInfo other) {
		return this.getName().compareTo( other.getName() );
	}
}
