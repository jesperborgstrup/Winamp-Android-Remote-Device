import socket, sys, time

def set_volume(socket, amount):
	# 10 is set volume
	print "setting volume to %d" % amount
	socket.send( "\x0a%c\x00" % ( chr(amount) ) )

if __name__ == "__main__":
	socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	print "made socket"
	socket.connect( ( "localhost", 9273 ) )
	print "connected..."
	set_volume(socket, 64)
	time.sleep(2)
	set_volume(socket, 128)
	time.sleep(2)
	set_volume(socket, 192)
	time.sleep(2)
	set_volume(socket, 255)
	socket.close()
	print "closed"