package dk.borgstrup.ward.client.connection;

import java.io.DataOutputStream;
import java.io.IOException;

import dk.borgstrup.ward.client.Settings;

public class MessageWriter {
	
	private DataOutputStream stream;
	
	public MessageWriter(DataOutputStream stream) 
	{
		this.stream = stream;
	}
	
	private void writeMessageType( int type ) throws IOException
	{
		stream.writeInt( type );
	}
	
	private void writeStopMessage() throws IOException
	{
		stream.writeByte( Messages.MESSAGE_STOP );
		stream.flush();
	}
	
	/**
	 * Sets the volume 
	 * @param amount 0-255, where 255 is max (100%)
	 */
	public void setVolume(int amount)
	{
		try {
			writeMessageType( Messages.SET_VOLUME );
			stream.writeByte( amount );
			writeStopMessage();
		} catch (IOException e) {
			Settings.LogW("MessageWriter::SetVolume::IOException", e);
		}
	}
	
	public void play() {
		writeEmptyMessage(Messages.PLAY);
	}

	public void stop() {
		writeEmptyMessage(Messages.STOP);
	}

	public void pause() {
		writeEmptyMessage(Messages.PAUSE);
	}

	public void previous() {
		writeEmptyMessage(Messages.PREVIOUS);
	}

	public void next() {
		writeEmptyMessage(Messages.NEXT);
	}

	private void writeEmptyMessage(int message) {
		try {
			writeMessageType( message );
			writeStopMessage();
		} catch (IOException e) {
			Settings.LogW("MessageWriter::play::IOException", e);
		}
	}
	
	public void requestVolume() {
		try {
			writeMessageType( Messages.GET_VOLUME );
			writeStopMessage();
			Settings.LogI("MessageWriter send GET_VOLUME");
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestVolume::IOException", e);
		}
	}

	public void requestPlaybackStatus() {
		try {
			writeMessageType( Messages.GET_PLAYBACK_STATUS );
			writeStopMessage();
			Settings.LogI("MessageWriter send GET_PLAYBACK_STATUS");
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestPlaybackStatus::IOException", e);
		}
	}

	public void requestPlayingTrackLength() {
		try {
			writeMessageType( Messages.GET_PLAYING_TRACK_LENGTH );
			writeStopMessage();
			Settings.LogI("MessageWriter send GET_GET_PLAYING_TRACK_LENGTH");
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestPlayingTrackLength::IOException", e);
		}
	}

	public void requestPlayingTrackPosition() {
		try {
			writeMessageType( Messages.GET_PLAYING_TRACK_POSITION );
			writeStopMessage();
			Settings.LogI("MessageWriter send GET_GET_PLAYING_TRACK_POSITION");
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestPlayingTrackPosition::IOException", e);
		}
	}

	public void requestCurrentTitle() {
		try {
			writeMessageType( Messages.GET_CURRENT_TITLE );
			writeStopMessage();
			Settings.LogI("MessageWriter send GET_CURRENT_TITLE");
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestCurrentTitle::IOException", e);
		}
	}

	public void requestPlaylist() {
		try {
			writeMessageType( Messages.GET_PLAYLIST );
			writeStopMessage();
			Settings.LogI("MessageWriter send GET_PLAYLIST");
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestPlaylist::IOException", e);
		}
	}

	public void requestPlaylistPosition() {
		try {
			writeMessageType( Messages.GET_PLAYLIST_POSITION );
			writeStopMessage();
			Settings.LogI("MessageWriter send GET_PLAYLIST_POSITION");
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestPlaylistPosition::IOException", e);
		}
	}

	public void playPlaylistItem(int position) {
		try {
			writeMessageType( Messages.PLAY_PLAYLIST_ITEM );
			stream.writeInt( position );
			writeStopMessage();
			Settings.LogI("MessageWriter send PLAY_PLAYLIST_ITEM ("+position+")");
		} catch (IOException e) {
			Settings.LogW("MessageWriter::playPlaylistItem::IOException", e);
		}
	}

}
