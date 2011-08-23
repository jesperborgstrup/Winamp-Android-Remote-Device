import socket, sys, time, threading, settings, struct

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
			
	def send_session_started(self, session_id):
		self.client.send("\x02")
		self.client.send(struct.pack(">i", session_id ) )
		self.client.send("\x00")
		
	def test(self, message):
		self.server.log(  "Received from %s:%s: test(%s)" % (str(self.host), str(self.port), message), level=4 )
		
	def set_volume(self, amount):
		self.server.log(  "Received from %s:%s: set_volume(%d)" % (str(self.host), str(self.port), amount), level=4 )
		self.server.winamp.setVolume( amount )

	def start_session(self, device_id):
		if self.debug:
			self.server.log(  "Received from %s:%s: start_session(%s)" % (str(self.host), str(self.port), device_id), level=3 )
		session_id = self.server.start_session(device_id)
		self.send_session_started( session_id )
		
	def probe(self, session_id, time, latitude, longitude):
		if self.debug:
			self.server.log(  "Received from %s:%s: probe(%d,%d,%.6f,%.6f)" % (str(self.host), str(self.port), session_id, time, latitude, longitude), level=4 )
		self.server.probe( session_id, time, latitude, longitude )
			
	def queue_status(self, session_id, time, status):
		if self.debug:
			self.server.log(  "Received from %s:%s: queue_status(%d,%d,%d)" % (str(self.host), str(self.port), session_id, time, status), level=3 )
		self.server.queue_status( session_id, time, status )
		
	def stop_session(self, session_id):
		if self.debug:
			self.server.log(  "Received from %s:%s: stop_session(%d)" % (str(self.host), str(self.port), session_id), level=3 )
		self.server.stop_session( session_id )
		
messages = [[chr(1), ClientThread.test,			 ClientThread.parse_string],
 			[chr(10),ClientThread.set_volume,    ClientThread.parse_byte],
			[chr(4), ClientThread.queue_status,  ClientThread.parse_int,   ClientThread.parse_int, ClientThread.parse_byte],
			[chr(5), ClientThread.stop_session,  ClientThread.parse_int]]

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

