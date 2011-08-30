import sys, os, time, settings, glob, threading, winamp, server

S = settings.Settings()

def log(msg, level=5):
	if level <= S.LOG_LEVEL:
		print msg
		
class ChangeMonitor(threading.Thread):
	
	ward_server = None
	
	def __init__(self):
		threading.Thread.__init__(self)
		self.path = os.getcwd()
		self.pattern = "%s\\*.py" % self.path
		files = glob.glob(self.pattern)
		files = filter(lambda f: f != sys.argv[0], files)
		self.files = {}
		
		for file in files:
			self.files[file] = os.stat(file).st_mtime
			
		self.start_server()
		
	def run(self):
		while 1:
			time.sleep(1)
			if self.poll():
				print
				print "-------------------------------------------------------"
				print "Noticed a change in server source. Restarting server..."
				print "-------------------------------------------------------"
				self.start_server()
		
	def poll(self):
		change = False;
		files = glob.glob(self.pattern)
		files = filter(lambda f: f != sys.argv[0], files)
		
		if len( files) != len( self.files ):
			change = True
			self.files = {}
			
		for file in files:
			mtime = os.stat(file).st_mtime
			if not self.files.has_key(file) or mtime != self.files[file]:
				change = True
				
			self.files[file] = mtime
			
		return change
	
	def start_server(self):
		reload(winamp)
		winamp_class = winamp.Winamp()
		
		reload(server)
		
		if self.ward_server != None:
			self.ward_server.stop()
			
		log("Initiating server socket on %s:%d..." % (hostname, port), 3 )
		self.ward_server = server.Server(hostname, port, winamp_class)
		log("Starting server...", 2)
		self.ward_server.start()
	
		
		

if __name__ == "__main__":

	hostname = S.hostname
	port = S.port
	
	monitor = ChangeMonitor()
	monitor.start()