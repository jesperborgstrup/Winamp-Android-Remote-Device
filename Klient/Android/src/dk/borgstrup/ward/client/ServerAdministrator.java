package dk.borgstrup.ward.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import dk.borgstrup.ward.client.connection.ServerConfiguration;
import dk.borgstrup.ward.client.connection.ServerInfo;

public class ServerAdministrator {

	public static String ServerFile = "servers";
	private Context context;
	
	public ServerAdministrator(Context context) {
		this.context = context;
	}
	
	public ServerConfiguration readConfiguration() {
		FileInputStream fis;
		try {
			fis = context.openFileInput(ServerFile);
		} catch (FileNotFoundException e) {
			try {
				context.openFileOutput(ServerFile, Context.MODE_PRIVATE);
				fis = context.openFileInput(ServerFile);
			} catch (FileNotFoundException e2) {
				// Will never be reached
				fis = null;
			}
		}
		DataInputStream dis = new DataInputStream(fis);
		String line;
		String[] entries;
		String latest;
		ServerInfo server;
		ServerConfiguration result = new ServerConfiguration();
		try {
			latest = dis.readLine();
			while (null != (line = dis.readLine())) {
				entries = line.split(",");
				server = new ServerInfo(entries[0], entries[1], Integer.valueOf(entries[2]).intValue());
				result.addServer(server);
				if (server.getName().equals(latest))
					result.setLatest(server);
			}
			dis.close();
		} catch (IOException e) {
			Settings.LogW( "ServerAdministrator::readServers::IOException", e);
		}
		return result;
	}
/*	
	public void addServer(ServerInfo config) {
		removeServer(config.getName());
		List<ServerInfo> servers = readConfiguration();
		servers.add( config );
		writeServers( servers );
	}
	
	public void removeServer(String name) {
		List<ServerInfo> servers = readConfiguration();
		ServerInfo toRemove = null;
		for (ServerInfo server: servers) {
			if ( server.getName().equals( name ) )
				toRemove = server;
		}
		if (toRemove != null)
			servers.remove( toRemove );
		writeServers( servers );
	}
*/	
	private void writeConfiguration(ServerConfiguration servers) {
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(ServerFile, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream( fos );
			for (ServerInfo server: servers.getServers()) {
				dos.writeBytes( server.getName()+","+server.getHost()+","+server.getPort()+"\r\n" );
			}
			dos.close();
		} catch (FileNotFoundException e) {
			Settings.LogW( "ServerAdministrator::writeServers::FileNotFoundException", e);
		} catch (IOException e) {{}
			Settings.LogW( "ServerAdministrator::writeServers::IOException", e);
		}
	}
	
	public void createDummyFile() {
		try {
			FileOutputStream fos = context.openFileOutput(ServerFile, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeBytes( "Jesper-LAPTOP\r\n" );
			dos.writeBytes( "Localhost,localhost,9273\r\n" );
			dos.writeBytes( "Jesper-LAPTOP,192.168.0.20,9273\r\n" );
			dos.writeBytes( "Jesper-PC,jesper-pc,9273\r\n" );
		} catch (FileNotFoundException e) {
			Settings.LogW( "ServerAdministrator::createDummyFile::FileNotFoundException", e);
		} catch (IOException e) {
			Settings.LogW( "ServerAdministrator::createDummyFile::IOException", e);
		}
	}
}
