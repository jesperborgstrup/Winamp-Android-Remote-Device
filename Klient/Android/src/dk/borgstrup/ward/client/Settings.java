package dk.borgstrup.ward.client;

import android.util.Log;

public class Settings {
	
	public static String LOG_TAG = "WardClient";
	
	public static String getServerHost() {
		return "192.168.0.20";
	}
	
	public static int getServerPort() {
		return 9273;
	}
	
	public static void LogW(String msg, Throwable e) {
		Log.w( LOG_TAG, msg, e);
	}

	public static void LogW(String msg) {
		Log.w( LOG_TAG, msg);
	}

	public static void LogI(String msg) {
		Log.i( LOG_TAG, msg);
	}
	
}
