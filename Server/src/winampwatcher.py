import threading, time, server, winamp

class WinampWatcher(threading.Thread):
	
	val_volume = -1
	val_current_track = ""
	val_playback_status = -1
	connected = False
	stop_thread = False
	
	def __init__(self, server, winamp_object):
		threading.Thread.__init__(self)
		self.server = server
		self.winamp = winamp_object
		
		if self.__ensure_connected():
			self.val_volume = self.winamp.getVolume()
			self.val_current_track = self.winamp.getCurrentPlayingTitle()
			self.val_playback_status = self.winamp.getPlaybackStatus()
		
	def run(self):
		while not self.stop_thread:
			time.sleep( self.server.S.WINAMP_POLL_INTERVAL_SECONDS )
			
			if not self.__ensure_connected():
				continue
			
			#Volume
			volume = self.winamp.getVolume()
			if volume != self.val_volume:
				self.server.S.log("Volume changed (from %d to %d)" % (self.val_volume, volume), level=8)
				self.val_volume = volume
				self.server.call_on_all_clients(server.ClientThread.send_volume)
				
			track = self.winamp.getCurrentPlayingTitle()
			if track != self.val_current_track:
				self.server.S.log("Track changed to %s" % (track), level=7)
				self.val_current_track = self.winamp.getCurrentPlayingTitle()
				self.server.call_on_all_clients(server.ClientThread.send_current_title)
				self.server.call_on_all_clients(server.ClientThread.send_playlist_position)
				self.server.call_on_all_clients(server.ClientThread.send_playing_track_length)
				self.server.call_on_all_clients(server.ClientThread.send_playing_track_position)
			
			playback_status = self.winamp.getPlaybackStatus()
			if playback_status != self.val_playback_status:
				self.server.S.log("Playback status changed %s" % (playback_status), level=7)
				self.val_playback_status = playback_status
				self.server.call_on_all_clients(server.ClientThread.send_playback_status)
			
	def __ensure_connected(self):
		try:
			self.winamp._Winamp__ensure_winamp_running()
			self.connected = True
		except winamp.WinampNotRunningException:
			self.connected = False
			
		return self.connected
		
	def stop(self):
		self.stop_thread = True