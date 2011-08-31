import sys, os, time, settings, glob, threading, winamp, server, traceback

def reload_modules():
	S = settings.Settings()
	S.log( "Reloading modules...", level=3 )
	for (name, module) in sys.modules.items():
		if str(module).startswith('<module') and name not in ['__main__']:
			reload(module)
			
	S = settings.Settings()
	S.log( "Done reloading modules.", level=3 )


class ChangeMonitor(threading.Thread):
	
	S = settings.Settings()
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
		try:
			if self.ward_server != None:
				self.ward_server.stop()
				reload_modules()
				
			self.S = settings.Settings()
			
			winamp_class = winamp.Winamp()
		
			
			self.S.log("Initiating server socket on %s:%d..." % (self.S.hostname, self.S.port), 3 )
			self.ward_server = server.Server(self.S.hostname, self.S.port, winamp_class)
			self.S.log("Starting server...", 2)
			self.ward_server.start()
		except Exception as e:
			print "=== AN EXCEPTION OCCURED ==="
			traceback.print_exc()
	
		

if __name__ == "__main__":

	monitor = ChangeMonitor()
	monitor.start()