package dk.borgstrup.ward.client;

import android.os.Bundle;
import dk.borgstrup.ward.client.connection.Messages;
import dk.borgstrup.ward.client.connection.WardConnection;
import dk.borgstrup.ward.client.connection.WardConnectionListener;

public class WinampManager implements WardConnectionListener {

	public Playlist playlist = new Playlist();
	private WardConnection conn;
	
	private int volume = -1;
	private int playback_status = -1;
	private String current_title = null;
	
	private boolean connection_error = false;
	
	public WinampManager(WardConnection conn) {
		this.conn = conn;
		this.conn.addListener( this );
		this.conn.addListener( this.playlist );
		requestInitialValues();
	}
	
	private void requestInitialValues() {
		this.conn.requestVolume();
		this.conn.requestPlaybackStatus();
		this.conn.requestCurrentTitle();
		this.conn.requestPlaylistPosition();
		this.conn.requestPlaylist();
	}
	
	public boolean isInitialized() {
		return ( !connection_error &&
				 volume != -1 && 
				 playback_status != -1 &&
				 current_title != null );
	}
	

	public void setVolume(int amount) {
		this.conn.setVolume( amount );
	}

	public int getVolume() {
		return volume;
	}

	public int getPlaybackStatus() {
		return playback_status;
	}

	public String getCurrenTitle() {
		return current_title;
	}

	@Override
	public void receivedMessage(int message, Bundle data) {
		switch (message) {
		case Messages.GET_VOLUME:
			int volume = data.getInt( Messages.EXTRA_VOLUME );
			this.volume = volume;
			break;
		case Messages.GET_PLAYBACK_STATUS:
			int status = data.getInt( Messages.EXTRA_PLAYBACK_STATUS );
			this.playback_status = status;
			break;
		case Messages.GET_CURRENT_TITLE:
			String title = data.getString( Messages.EXTRA_CURRENT_TITLE );
			this.current_title = title;
			break;
		case Messages.ERROR:
			int error = data.getInt( Messages.EXTRA_ERROR );
			switch (error) {
			case Messages.ERROR_WINAMP_NOT_RUNNING:
				connection_error = true;
				break;
			}
			break;
		}
	}

}
