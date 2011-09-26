package dk.borgstrup.ward.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WakeOnLanSender {

	/**
	 * 
	 * @param broadcastIP
	 * @param mac without delimiters in uppercase, e.g. 00D0F446CD01
	 */
	public static void sendSingleWakePacket(String broadcastIP, String mac) {
        if (mac == null) {
        	return;
        }
        
        try {
            byte[] macBytes = getMacBytes(mac);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }
            
            InetAddress address = InetAddress.getByName(broadcastIP);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            
            Settings.LogI("Wake-on-LAN packet sent.");
        }
        catch (Exception e) {
            Settings.LogW("Failed to send Wake-on-LAN packet", e);
        }
	}
        
	/**
	 * 
	 * @param macStr enter like this: 00AE56F332BE
	 * @return
	 * @throws IllegalArgumentException
	 */
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        if (macStr.length() != 12) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
        	String hex;
            for (int i = 0; i < 6; i++) {
            	hex = macStr.substring(i*2, i*2+2);
            	Settings.LogI( "MAC part " + (1+i)+": " + hex);
                bytes[i] = (byte) Integer.parseInt(hex, 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
    


}
