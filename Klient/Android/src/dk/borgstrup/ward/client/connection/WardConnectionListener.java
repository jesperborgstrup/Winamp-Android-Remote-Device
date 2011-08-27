package dk.borgstrup.ward.client.connection;

import android.os.Bundle;

public interface WardConnectionListener {

	public void receivedMessage(byte message, Bundle data);
	
}
