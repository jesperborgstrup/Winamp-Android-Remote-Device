package dk.borgstrup.ward.client.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import dk.borgstrup.ward.client.Settings;

import android.os.Bundle;

public class WardConnection extends Thread {
	
	private String host;
	private int port;
	private Socket socket;
	
	private MessageWriter mw;
	private MessageReader mr;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private List<WardConnectionListener> listeners;
	
	protected int lastError = -1;
	protected int lastInfo = -1;
	
	public WardConnection(String host, int port )
	{
		super();
	    this.host = host;
	    this.port = port;
	    this.listeners = new ArrayList<WardConnectionListener>();
	}
	
	public void addListener(WardConnectionListener listener) {
		if (listener == null || this.listeners.contains( listener ))
			return;
		
		this.listeners.add( listener );
	}

	public boolean removeListener(WardConnectionListener listener) {
		return this.listeners.remove( listener );
	}
	
	public void postMessageToListeners(int message, Bundle data) {
		for (WardConnectionListener l: listeners) {
			l.receivedMessage(message, data);
		}
	}
	
	 public boolean Connect() throws UnknownHostException {
		try {
		    
		    InetAddress addr = InetAddress.getByName(host);
		    Settings.LogI( "Trying to connect to " + host + ":" + port );
		    SocketAddress sockaddr = new InetSocketAddress(addr, port);

		    this.socket = new Socket();

		    int timeoutMs = 10000;   // 2 seconds
		    this.socket.connect(sockaddr, timeoutMs);
		    
		    dos = new DataOutputStream( this.socket.getOutputStream() );
		    mw = new MessageWriter( dos );
		    dis = new DataInputStream( this.socket.getInputStream() );
		    mr = new MessageReader( dis, this );
		    mr.start();
		    
		    return true;
		} catch (SocketTimeoutException e) {
		    Settings.LogW( "Socket timeout", e );
			e.printStackTrace();
		} catch (IOException e) {
		    Settings.LogW( "IO Exception", e );
			e.printStackTrace();
		}
		return false;
		
	}
	
	 public boolean isConnected() {
		 return (socket != null && socket.isConnected());
	 }
	 
	 public void disconnect() {
		 // TODO: Make this
		 try {
			socket.close();
		} catch (IOException e) {
			Settings.LogW( "IO Exception on socket close", e);
		}
	 }
	 
	/**
	* Sets the volume 
	* @param amount 0-255, where 255 is max (100%)
	*/
	public void setVolume(int amount) {
		assert amount >= 0 && amount <= 255;
		if (isConnected())
			mw.setVolume(amount);
	}
	
	/**
	 * Start playback
	 */
	public void play() {
		if (isConnected())
			mw.play();
	}
	
	/**
	 * Stop playback
	 */
/*	public void stop() {
		if (isConnected())
			mw.stop();
	}
	*/
	
	/**
	 * Pause playback
	 */
	public void pause() {
		if (isConnected())
			mw.pause();
	}
	
	/**
	 * Previous track
	 */
	public void previous() {
		if (isConnected())
			mw.previous();
	}
	/**
	 * Next track
	 */
	public void next() {
		if (isConnected())
			mw.next();
	}
	
	/**
	 * Requests the current volume.
	 */
	public void requestVolume() {
		if (isConnected())
			mw.requestVolume();
	}

	/**
	 * Requests the current status for playback
	 */
	public void requestPlaybackStatus() {
		if (isConnected())
			mw.requestPlaybackStatus();
	}

	public void requestPlayingTrackLength() {
		if (isConnected())
			mw.requestPlayingTrackLength();
	}

	public void requestPlayingTrackPosition() {
		if (isConnected())
			mw.requestPlayingTrackPosition();
	}

	/**
	 * Requests the current track title
	 */
	public void requestCurrentTitle() {
		if (isConnected())
			mw.requestCurrentTitle();
	}
	
	public void requestPlaylist() {
		if (isConnected())
			mw.requestPlaylist();
	}
	
	public void requestPlaylistPosition() {
		if (isConnected()) {
			mw.requestPlaylistPosition();
		}
	}

	/**
	 * Playback a specific playlist item 
	 * @param position 0-indexed playlist item
	 */
	public void playPlaylistItem(int position) {
		if (isConnected()) {
			mw.playPlaylistItem( position );
		}
	}
	
	public int getLastError() {
		return this.lastError;
	}
	
	public int getLastInfo() {
		return this.lastInfo;
	}
}
