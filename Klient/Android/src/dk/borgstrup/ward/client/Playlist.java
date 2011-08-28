package dk.borgstrup.ward.client;

import java.util.ArrayList;

public class Playlist extends ArrayList<PlaylistItem> {

	private static final long serialVersionUID = 1L;

	public Playlist() {
		super();
	}
	
	public Playlist(String[] titles) {
		for (String title: titles) {
			this.add( new PlaylistItem( title ) );
		}
	}
	
}
