package dk.borgstrup.ward.client;

import java.io.DataOutputStream;
import java.io.IOException;

public class MessageWriter {
	
	private DataOutputStream stream;
	
	public MessageWriter(DataOutputStream stream) 
	{
		this.stream = stream;
	}
	
	private void writeMessageType( int type ) throws IOException
	{
		stream.writeByte( type );
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

	private void writeEmptyMessage(byte message) {
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
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestVolume::IOException", e);
		}
	}

	public void requestPlaybackStatus() {
		try {
			writeMessageType( Messages.GET_PLAYBACK_STATUS );
			writeStopMessage();
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestPlaybackStatus::IOException", e);
		}
	}

	public void requestCurrentTitle() {
		try {
			writeMessageType( Messages.GET_CURRENT_TITLE );
			writeStopMessage();
		} catch (IOException e) {
			Settings.LogW("MessageWriter::RequestCurrentTitle::IOException", e);
		}
	}

}
