package dk.borgstrup.ward.client;

import android.os.Bundle;

public interface WardConnectionListener {

	public void receivedMessage(byte message, Bundle data);
	
}
