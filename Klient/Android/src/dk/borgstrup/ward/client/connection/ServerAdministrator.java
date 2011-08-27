package dk.borgstrup.ward.client.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.widget.Toast;
import dk.borgstrup.ward.client.Settings;

public class ServerAdministrator {

	public static String ServerFile = "servers";
	private Context context;
	
	private ServerConfiguration config;
	
	public ServerAdministrator(Context context) {
		this.context = context;
	}
	
	public void update() {
		config = readConfiguration();
	}
	
	public ServerConfiguration getConfiguration() {
		if (config == null) {
			config = readConfiguration();
		}
		
		return config;
	}
	
	public void setLatest(ServerInfo server) {
		config.setLatest(server);
		writeConfiguration();
	}
	
	public void addServer(ServerInfo server) {
		config.addServer(server);
		writeConfiguration();
	}
	
	public void removeServer(ServerInfo server) {
		config.removeServer(server);
		writeConfiguration();
	}
	
	private ServerConfiguration readConfiguration() {
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
		String s = "";
		try {
			latest = dis.readLine();
			s += "Latest: " + latest + "\r\n";
			while (null != (line = dis.readLine())) {
				entries = line.split(",");
				server = new ServerInfo(entries[0], entries[1], Integer.valueOf(entries[2]).intValue());
				result.addServer(server);
				s+=entries[0] + "\r\n";
				if (!latest.equals("") && server.getName().equals(latest))
					result.setLatest(server);
			}
			dis.close();
			Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Settings.LogW( "ServerAdministrator::readServers::IOException", e);
		}
		return result;
	}

	public void writeConfiguration() {
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(ServerFile, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream( fos );
			if (config.getLatest() == null) {
				dos.writeBytes("\r\n");
			} else {
				dos.writeBytes(config.getLatest().getName()+"\r\n");
			}
			for (ServerInfo server: config.getServers()) {
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
			dos.writeBytes( "Jesper-LAPTOP,192.168.0.20,9273\r\n" );
			dos.writeBytes( "Jesper-PC,192.168.0.13,9273\r\n" );
			/*
			dos.writeBytes( "Localhost2,localhost,9273\r\n" );
			dos.writeBytes( "Jesper-LAPTOP2,192.168.0.20,9273\r\n" );
			dos.writeBytes( "Jesper-PC2,192.168.0.13,9273\r\n" );
			dos.writeBytes( "Localhost3,localhost,9273\r\n" );
			dos.writeBytes( "Jesper-LAPTOP3,192.168.0.20,9273\r\n" );
			dos.writeBytes( "Jesper-PC3,192.168.0.13,9273\r\n" );
			dos.writeBytes( "Localhost4,localhost,9273\r\n" );
			dos.writeBytes( "Jesper-LAPTOP4,192.168.0.20,9273\r\n" );
			dos.writeBytes( "Jesper-PC4,192.168.0.13,9273\r\n" );
			*/
		} catch (FileNotFoundException e) {
			Settings.LogW( "ServerAdministrator::createDummyFile::FileNotFoundException", e);
		} catch (IOException e) {
			Settings.LogW( "ServerAdministrator::createDummyFile::IOException", e);
		}
	}

}
