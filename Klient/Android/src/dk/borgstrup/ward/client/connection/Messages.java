package dk.borgstrup.ward.client.connection;

public class Messages {
	
	public static final int MESSAGE_STOP = 0;

	public static final int PLAY     = 1;
	public static final int STOP     = 2;
	public static final int PAUSE    = 3;
	public static final int PREVIOUS = 4;
	public static final int NEXT     = 5;

	public static final int SET_VOLUME = 10;
	public static final int PLAY_PLAYLIST_ITEM = 20;
	
	public static final int GET_VOLUME = 110;
	public static final int GET_PLAYBACK_STATUS = 115;
	public static final int GET_CURRENT_TITLE = 120;
	
	public static final int GET_PLAYLIST = 150;
	
	public static final String EXTRA_SERVER_HOST = "server_host";
	public static final String EXTRA_SERVER_PORT = "server_port";
	public static final String EXTRA_SERVER_ADMIN = "server_admin";
	
	public static final String EXTRA_VOLUME = "volume";
	public static final String EXTRA_PLAYBACK_STATUS = "playback_status";
	public static final String EXTRA_CURRENT_TITLE = "current_title";
	
	public static final String EXTRA_PLAYLIST_ITEMS = "playlist_items";

	public static final int PLAYBACK_NOT_PLAYING = 0;
	public static final int PLAYBACK_PLAYING = 1;
	public static final int PLAYBACK_PAUSE = 3;

	
}
