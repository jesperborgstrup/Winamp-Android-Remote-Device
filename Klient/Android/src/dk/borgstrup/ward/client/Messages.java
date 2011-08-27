package dk.borgstrup.ward.client;

public class Messages {
	
	public static final byte MESSAGE_STOP = 0;

	public static final byte PLAY     = 1;
	public static final byte STOP     = 2;
	public static final byte PAUSE    = 3;
	public static final byte PREVIOUS = 4;
	public static final byte NEXT     = 5;

	public static final byte SET_VOLUME = 10;
	
	public static final byte GET_VOLUME = 110;
	public static final byte GET_PLAYBACK_STATUS = 115;
	public static final byte GET_CURRENT_TITLE = 120;
	
	public static final String EXTRA_SERVER_HOST = "server_host";
	public static final String EXTRA_SERVER_PORT = "server_port";
	public static final String EXTRA_SERVER_ADMIN = "server_admin";
	
	public static final String EXTRA_VOLUME = "volume";
	public static final String EXTRA_PLAYBACK_STATUS = "playback_status";
	public static final String EXTRA_CURRENT_TITLE = "current_title";

	public static final int PLAYBACK_NOT_PLAYING = 0;
	public static final int PLAYBACK_PLAYING = 1;
	public static final int PLAYBACK_PAUSE = 3;

	
}
