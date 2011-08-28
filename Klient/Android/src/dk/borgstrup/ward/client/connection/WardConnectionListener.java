package dk.borgstrup.ward.client.connection;

import android.os.Bundle;

public interface WardConnectionListener {

	public void receivedMessage(int message, Bundle data);
	
}
