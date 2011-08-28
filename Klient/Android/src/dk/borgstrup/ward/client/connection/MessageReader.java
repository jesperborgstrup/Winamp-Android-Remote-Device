package dk.borgstrup.ward.client.connection;

import java.io.DataInputStream;
import java.io.IOException;

import dk.borgstrup.ward.client.Settings;

import android.os.Bundle;

public class MessageReader extends Thread {
	
	private DataInputStream stream;
	private Short message = null;
	private WardConnection conn;
	
	public MessageReader(DataInputStream stream, WardConnection conn) 
	{
		this.stream = stream;
		this.conn = conn;
		
	}
	
	@Override
	public void run() {
		
		while (true) {
			try {
				if (message == null) {
					message = stream.readShort();
				} else {
					switch (message) {
					case Messages.GET_VOLUME:
						tryParseGetVolume();
						break;
					case Messages.GET_PLAYBACK_STATUS:
						tryParseGetPlaybackStatus();
						break;
					case Messages.GET_CURRENT_TITLE:
						tryParseGetCurrentTitle();
						break;
					case Messages.GET_PLAYLIST:
						tryParseGetPlaylist();
						break;
					case Messages.ERROR:
						tryParseError();
						break;
					}
					message = null;
				}
			} catch (IOException e) {
				Settings.LogW("MessageReader::run::IOException", e);
				break;
			}
		}
	}
	
	private void tryParseGetVolume() throws IOException {
		Settings.LogI("tryparsegetvolume()");
		Bundle data = new Bundle(1);
		int volume = stream.readInt();
		data.putInt( Messages.EXTRA_VOLUME, volume );
		conn.postMessageToListeners( Messages.GET_VOLUME, data );
	}
	
	private void tryParseGetPlaybackStatus() throws IOException {
		Settings.LogI("tryparsegetPlaybackStatus()");
		Bundle data = new Bundle(1);
		int status = stream.readInt();
		data.putInt( Messages.EXTRA_PLAYBACK_STATUS, status );
		conn.postMessageToListeners( Messages.GET_PLAYBACK_STATUS, data );
	}

	private void tryParseGetCurrentTitle() throws IOException {
		Settings.LogI("tryparsegetCurrentTitle()");
		Bundle data = new Bundle(1);
		String title = readString();
		data.putString( Messages.EXTRA_CURRENT_TITLE, title );
		conn.postMessageToListeners( Messages.GET_CURRENT_TITLE, data );
	}

	private void tryParseGetPlaylist() throws IOException {
		Settings.LogI("tryparsegetPlaylist()");
		Bundle data = new Bundle(1);
		int length = stream.readInt();
		String[] playlist = new String[length];
		for (int i = 0; i < length; i++ ) {
			String s = readString();
			Settings.LogI("Playlist item: " + s);
			playlist[i] = s;
		}
		data.putStringArray( Messages.EXTRA_PLAYLIST_ITEMS, playlist );
		conn.postMessageToListeners( Messages.GET_PLAYLIST, data );
	}
	
	private void tryParseError() throws IOException {
		Settings.LogI("tryparseError()");
		Bundle data = new Bundle(1);
		int error = stream.readInt();
		data.putInt( Messages.EXTRA_ERROR, error );
		conn.postMessageToListeners( Messages.ERROR, data );
	}



	private String readString() throws IOException {
		return stream.readUTF();
	}
}
