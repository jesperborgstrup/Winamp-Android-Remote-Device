package dk.borgstrup.ward.client;

import java.util.Calendar;
import java.util.Date;

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
	
	private int track_length = 0;
	private long track_position = 0;
	private Calendar track_position_updated = Calendar.getInstance();
	
	private boolean connection_error = false;
	
	public WinampManager(WardConnection conn) {
		this.conn = conn;
		this.conn.addListener( this );
		this.conn.addListener( this.playlist );
		track_position_updated.setTime( new Date() );
		Settings.LogI("Starting WinampManager " + track_position_updated.getTimeInMillis());
		requestInitialValues();
	}
	
	private void requestInitialValues() {
		this.conn.requestVolume();
		this.conn.requestPlaybackStatus();
		this.conn.requestPlayingTrackLength();
		this.conn.requestPlayingTrackPosition();
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
	
	public int getPlayingTrackLength() {
		return track_length;
	}
	
	public int getPlayingTrackPosition() {
		return (int) (getPlayingTrackPositionMillis() / 1000);
	}

	public long getPlayingTrackPositionMillis() {
		if ( this.playback_status == Messages.PLAYBACK_PLAYING ) {
			long millis = track_position + Calendar.getInstance().getTimeInMillis() - track_position_updated.getTimeInMillis();
			Settings.LogI(track_position+ " + " + Calendar.getInstance().getTimeInMillis() +" - " + track_position_updated.getTimeInMillis());
			Settings.LogI(millis+"");
			return millis;
		} else {
			return track_position;
		}
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
			if ( status == Messages.PLAYBACK_PAUSE ) {
				track_position = getPlayingTrackPositionMillis();
				conn.requestPlayingTrackPosition();
			} else if ( status == Messages.PLAYBACK_PLAYING ) {
				track_position_updated.setTime( new Date() );
			} else if ( status == Messages.PLAYBACK_NOT_PLAYING ) {
				track_position = 0;
			}
			this.playback_status = status;
			break;
		case Messages.GET_PLAYING_TRACK_LENGTH:
			int length = data.getInt( Messages.EXTRA_PLAYING_TRACK_LENGTH );
			this.track_length = length;
			break;
		case Messages.GET_PLAYING_TRACK_POSITION:
			int pos = data.getInt( Messages.EXTRA_PLAYING_TRACK_POSITION );
			track_position_updated.setTime( new Date() );
			this.track_position = pos;
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
