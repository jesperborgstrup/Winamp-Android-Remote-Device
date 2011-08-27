from winamp import *
from server import *
import time, settings

S = settings.Settings()

def log(msg, level=5):
	if level <= S.LOG_LEVEL:
		print msg
	

if __name__ == "__main__":
	print "main"
	
	hostname = S.hostname
	port = S.port
	
	winamp = Winamp()
	
	log("Initiating server socket on %s:%d..." % (hostname, port), 3 )
	server = Server(hostname, port, winamp)
	log("Starting server...", 2)
	server.run()
	
	
	
	#for i in range(5):
#		print "playing"#
#		winamp.play()
		#time.sleep(2)
#		print "pausing"
#		winamp.pause()
#		time.sleep(2)
		
	print "done"
