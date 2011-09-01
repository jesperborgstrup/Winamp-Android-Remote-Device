package dk.borgstrup.ward.client;

import java.util.ArrayList;

import android.os.Bundle;

import dk.borgstrup.ward.client.connection.Messages;
import dk.borgstrup.ward.client.connection.WardConnectionListener;

public class Playlist extends ArrayList<PlaylistItem> implements WardConnectionListener{

	private static final long serialVersionUID = 1L;
	private int currentItem = -1;

	public Playlist() {
		super();
	}
	
	public int getCurrentItem() {
		return currentItem;
	}
	
	public boolean isCurrentItem( PlaylistItem item ) {
		return this.indexOf( item ) == currentItem;
	}

	@Override
	public void receivedMessage(int message, Bundle data) {
		switch (message) {
		case Messages.GET_PLAYLIST:
			this.clear();
			for (String title: data.getStringArray(Messages.EXTRA_PLAYLIST_ITEMS) ) {
				this.add( new PlaylistItem( title ) );
			}
			break;
		case Messages.GET_PLAYLIST_POSITION:
			int position = data.getInt(Messages.EXTRA_PLAYLIST_POSITON);
			this.currentItem = position;
		}
	}
	
}
