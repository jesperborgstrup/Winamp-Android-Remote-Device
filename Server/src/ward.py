import sys, os, time, settings, glob, threading, subprocess

class ChangeMonitor(threading.Thread):
	
	S = settings.Settings()
	server_process = None
	
	def __init__(self):
		threading.Thread.__init__(self)
		self.path = os.getcwd()
		self.pattern = "%s\\*.py" % self.path
		files = glob.glob(self.pattern)
		files = filter(lambda f: f != sys.argv[0], files)
		self.files = {}
		
		for f in files:
			self.files[f] = os.stat(f).st_mtime
			
		self.start_server()
		
	def run(self):
		while 1:
			time.sleep(1)
			if self.poll():
				self.S.log( "", level=1 )
				self.S.log( "-------------------------------------------------------", level=1 )
				self.S.log( "Noticed a change in server source. Restarting server...", level=1 )
				self.S.log( "-------------------------------------------------------", level=1 )
				self.start_server()
		
	def poll(self):
		change = False;
		files = glob.glob(self.pattern)
		files = filter(lambda f: f != sys.argv[0], files)
		
		if len( files) != len( self.files ):
			change = True
			self.files = {}
			
		for f in files:
			mtime = os.stat(f).st_mtime
			if not self.files.has_key(f) or mtime != self.files[f]:
				change = True
				
			self.files[f] = mtime
			
		return change
	
	def start_server(self):
		try:
			if self.server_process != None and self.server_process.poll() is None:
				self.server_process.kill()
				self.server_process.wait()
				
			self.server_process = subprocess.Popen( [sys.executable, r"run_server.py"] )

		except Exception:
			self.S.log_exception()
	
		

if __name__ == "__main__":
	sys.stdout = open("mylog.txt", "w")
	
#	print sys.stdout


	monitor = ChangeMonitor()
	monitor.start()