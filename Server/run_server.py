import settings, winamp, server, atexit, socket, time

def exit_func():
	print "Exiting, closing socket"
	ward_server.stop()
	print "Socket supposedly closed"
	
def run_server():

	S = settings.Settings()
	
	try:
		winamp_class = winamp.Winamp()
		S.log("Initiating server socket on %s:%d..." % (S.hostname, S.port), 3 )
		ward_server = server.Server(S.hostname, S.port, winamp_class)
		S.log("Starting server...", 2)
		ward_server.start()
		
		atexit.register( lambda x: ward_server.stop() )
	except Exception:
		S.log_exception()


if __name__ == "__main__":
	run_server()
