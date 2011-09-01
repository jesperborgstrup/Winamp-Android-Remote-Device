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
		Settings.LogI("Wake server");
		
        String ipStr = "192.168.0.255";
        String macStr = server.getMac();
        if (macStr == null) {
        	return;
        }
        
        try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }
            
            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            
            Settings.LogI("Wake-on-LAN packet sent.");
        }
        catch (Exception e) {
            Settings.LogW("Failed to send Wake-on-LAN packet", e);
        }
	}
        
	/**
	 * 
	 * @param macStr enter like this: 00AE56F332BE
	 * @return
	 * @throws IllegalArgumentException
	 */
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        if (macStr.length() != 12) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
        	String hex;
            for (int i = 0; i < 6; i++) {
            	hex = macStr.substring(i*2, i*2+2);
            	Settings.LogI( "MAC part " + (1+i)+": " + hex);
                bytes[i] = (byte) Integer.parseInt(hex, 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
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
