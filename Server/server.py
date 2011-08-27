import socket, sys, time, threading, settings, struct, pickle
from messages import Messages

S = settings.Settings()


class Server():
	threads = []
	
	def __init__(self, host, port, winamp):
		self.port = port
		self.host = host
		self.backlog = 5
		self.winamp = winamp
		
		self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.socket.bind((self.host,self.port))

	def run(self):
		self.socket.listen(self.backlog)
		self.log( "Server started", 2 )
		while 1:
			client, address = self.socket.accept()
			self.log( "Client %s:%s connected" % address, level=4 )
			thread = ClientThread( self, client, address )
			self.threads.append(thread)
			thread.start()
		
	def log(self, msg, level=5):
		if level <= S.LOG_LEVEL:
			print msg
	

	def stop(self):
		self.socket.close()
		
		
class ClientThread( threading.Thread ):
	data = ""
	function = None
	parse_function = None
	message = None
	params = None
	param_index = None
	
	def __init__(self, server, client, address):
		threading.Thread.__init__(self)
		self.server = server
		self.client = client
		self.host,self.port = address
		self.buffer = CommandBuffer()
		self.filler = BufferFiller(server, client, address, self.buffer)
		self.server.log( "ClientThread initiated", 6 )
		
	def run(self):
		self.filler.start()
		self.server.log( "ClientThread started", 7)
		while 1:
			self.server.log( "ClientThread start of main loop", 7)
			has_lock = False
			try:
				self.buffer.cond.acquire()
				has_lock = True
				while self.buffer.is_empty():
					self.buffer.cond.wait()
				
				char = self.buffer.consume_one()
			finally:
				if has_lock:
					self.buffer.cond.release()
					has_lock = False
			self.server.log( "ClientThread main loop 2", 9)
			if self.parse_function == None:
				if self.param_index == None:
					for msg in messages:
						if char == msg[0]:
							self.server.log( "Client sent message %s" % msg, level=7 )
							self.message = msg
							self.function, self.param_index, self.params = (msg[1], 2, [])
				else:
					if len(self.message) <= self.param_index:
						assert char == chr(0)
						self.function(self,*self.params)
						self.function, self.message, self.param_index, self.params, self.parse_function = (None, None, None, None, None)
					else:
						self.parse_function = self.message[self.param_index]
						self.parse_function(self, char)
			else:
				self.parse_function(self,char)
				
		
		self.filler.kill()
		self.filler.join()
		self.server.log( "Client %s:%s disconnected" % (str(self.host), str(self.port)), level=2 )

	def parse_int(self, char):
		self.data += char
		if len(self.data) >= 4:
			int = struct.unpack(">i", self.data)[0]
			self.params.append(int)
			self.param_index += 1
			self.parse_function, self.data = (None, "")
	def parse_float(self, char):
		self.data += char
		if len(self.data) >= 4:
			float = struct.unpack(">f", self.data)[0]
			self.params.append(float)
			self.param_index += 1
			self.parse_function, self.data = (None, "")
	def parse_byte(self, char):
		self.data += char
		if len(self.data) >= 1:
			byte = struct.unpack(">B", self.data)[0]
			self.params.append(byte)
			self.param_index += 1
			self.parse_function, self.data = (None, "")
	def parse_string(self, char):
		self.server.log("parse_string: %s (%d)" % (char, ord(char)), 10)
		if char == chr(0):
			self.server.log("parse_string: %s" % (self.data), 9)
			self.params.append(self.data)
			self.param_index += 1
			self.parse_function, self.data = (None, "")
		else:
			self.data += char
			
	def play(self):
		self.server.log(  "Received from %s:%s: play()" % (str(self.host), str(self.port)), level=4 )
		self.server.winamp.play()
		self.send_playback_status()
			
	def stop(self):
		self.server.log(  "Received from %s:%s: stop()" % (str(self.host), str(self.port)), level=4 )
		self.server.winamp.stop()
		self.send_playback_status()
			
	def pause(self):
		self.server.log(  "Received from %s:%s: pause()" % (str(self.host), str(self.port)), level=4 )
		self.server.winamp.pause()
		self.send_playback_status()
			
	def previous(self):
		self.server.log(  "Received from %s:%s: previous()" % (str(self.host), str(self.port)), level=4 )
		self.server.winamp.previous()
		self.send_playback_status()
		self.send_current_title()
			
	def next(self):
		self.server.log(  "Received from %s:%s: next()" % (str(self.host), str(self.port)), level=4 )
		self.server.winamp.next()
		self.send_playback_status()
		self.send_current_title()
			
	def set_volume(self, amount):
		self.server.log(  "Received from %s:%s: set_volume(%d)" % (str(self.host), str(self.port), amount), level=4 )
		self.server.winamp.setVolume( amount )
		
	def get_volume(self):
		self.server.log(  "Received from %s:%s: get_volume()" % (str(self.host), str(self.port) ), level=6 )
		self.send_volume()
		
	def send_volume(self):
		self.client.send( Messages.GET_VOLUME )
		self.client.send( struct.pack(">i", self.server.winamp.getVolume()))
		self.client.send( Messages.STOP )
		
	def get_playback_status(self):
		self.server.log(  "Received from %s:%s: get_playback_status()" % (str(self.host), str(self.port) ), level=6 )
		self.send_playback_status()
		
	def send_playback_status(self):
		self.client.send( Messages.GET_PLAYBACK_STATUS )
		status = self.server.winamp.getPlaybackStatus()
		self.server.log( "Sending playback status: %d" % status, level=5 )
		self.client.send( struct.pack(">i", self.server.winamp.getPlaybackStatus()))
		self.client.send( Messages.STOP )
		
	def get_current_title(self):
		self.server.log(  "Received from %s:%s: get_current_title()" % (str(self.host), str(self.port) ), level=6 )
		self.send_current_title()
		
	def send_current_title(self):
		self.client.send( Messages.GET_CURRENT_TITLE )
		self.send_string( self.server.winamp.getCurrentPlayingTitle() )
		self.client.send( Messages.STOP )
		
	def send_string(self, string):
		encoded = string.encode('utf-8')
		# First two bytes of a string transmission is the length of the encoded string
		self.client.send( struct.pack( ">H", len( encoded ) ) )
		self.client.send( encoded )

messages = [[Messages.PLAY,       ClientThread.play],
			[Messages.STOP,       ClientThread.stop],
			[Messages.PAUSE,      ClientThread.pause],
			[Messages.PREVIOUS,   ClientThread.previous],
			[Messages.NEXT,       ClientThread.next],
			[Messages.SET_VOLUME, ClientThread.set_volume,    ClientThread.parse_byte],
			
			[Messages.GET_VOLUME, ClientThread.get_volume],
			[Messages.GET_PLAYBACK_STATUS, ClientThread.get_playback_status],
			[Messages.GET_CURRENT_TITLE, ClientThread.get_current_title]]


class CommandBuffer:
	def __init__(self):
		self.buffer = ""
		self.cond = threading.Condition()
	def add(self, b):
		self.buffer += b
	def consume_one(self):
		b, self.buffer = self.buffer[0], self.buffer[1:]
		return b
	def is_empty(self):
		return self.buffer == ""

# http://linuxgazette.net/107/pai.html
# Serveren blev lavet med en Producer-Consumer pattern,
# da der ellers gik pakker tabt i kommunikationen (fordi vi læste én byte ad gangen fra socket'en)
class BufferFiller( threading.Thread ):
	
	def __init__(self, server, client, address, buffer):
		threading.Thread.__init__(self)
		self.server = server
		self.client = client
		self.buffer = buffer
		self.host,self.port = address
		self._kill = False
		
	def run(self):
		while not self._kill:
			has_lock = False
			buf = ' '
			try:
				buf = self.client.recv(4096)
			except:
				buf = ''
			# Hvis vi får en tom streng tilbage,
			# er forbindelsen lukket
			if buf == '':
				self._kill = True
			self.buffer.cond.acquire()
			has_lock = True
			self.buffer.add( buf )
			self.buffer.cond.notifyAll()
			if has_lock:
				self.buffer.cond.release()
				has_lock = False
			if self._kill:
				break
	
	def kill(self):
		self._kill = True

