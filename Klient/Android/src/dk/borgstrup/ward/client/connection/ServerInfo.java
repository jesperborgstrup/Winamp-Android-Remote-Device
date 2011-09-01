package dk.borgstrup.ward.client.connection;

public class ServerInfo implements Comparable<ServerInfo> {
	
	private String name = "";
	private String host = "";
	private int port = 9273;
	private String mac = "";

	public ServerInfo() {}
	
	public ServerInfo(String name, String host, int port, String mac) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.mac = mac;
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
	
	public String getMac() {
		return mac;
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
