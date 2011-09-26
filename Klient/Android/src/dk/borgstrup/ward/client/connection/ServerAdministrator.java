package dk.borgstrup.ward.client.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import dk.borgstrup.ward.client.Settings;
import dk.borgstrup.ward.client.WakeOnLanSender;

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
	
	public void wakeServer(ServerInfo server) {
		int wakePackets = 10;
		int wakePacketInterval = 200;
		String broadcastIP = "192.168.0.255";

		for (int i = 0; i < wakePackets; i++) {
			WakeOnLanSender.sendSingleWakePacket( broadcastIP, server.getMac() );
			try {
				Thread.sleep( wakePacketInterval, 0 );
			} catch (InterruptedException e) {
			}
		}
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
		try {
			latest = dis.readLine();
			while (null != (line = dis.readLine())) {
				entries = line.split(",");
				String mac = null;
				if (entries.length > 3) {
					mac = entries[3];
				}
				server = new ServerInfo(entries[0], entries[1], Integer.valueOf(entries[2]).intValue(), mac);
				result.addServer(server);
				if (!latest.equals("") && server.getName().equals(latest))
					result.setLatest(server);
			}
			dis.close();
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
			String macText = "";
			for (ServerInfo server: config.getServers()) {
				if (server.getMac() != null )
					macText = server.getMac();
				else if (server.getMac().equals( "null" ))
					macText = "";
				
				dos.writeBytes( server.getName()+","+server.getHost()+","+server.getPort()+","+macText+"\r\n" );
			}
			dos.close();
		} catch (FileNotFoundException e) {
			Settings.LogW( "ServerAdministrator::writeServers::FileNotFoundException", e);
		} catch (IOException e) {{}
			Settings.LogW( "ServerAdministrator::writeServers::IOException", e);
		}
	}

}
