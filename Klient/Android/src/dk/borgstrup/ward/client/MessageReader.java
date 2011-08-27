package dk.borgstrup.ward.client;

import java.io.DataInputStream;
import java.io.IOException;

import android.os.Bundle;

public class MessageReader extends Thread {
	
	private DataInputStream stream;
	private Byte message = null;
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
					message = stream.readByte();
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

	private String readString() throws IOException {
		return stream.readUTF();
	}
}
