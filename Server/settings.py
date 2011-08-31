#!/usr/bin/env python
import socket, time, os

class Settings:
	
	hostname = ""
	port = 9273
	LOG_PRINT_LEVEL = 5
	LOG_FILE_LEVEL = 10
	LOG_FILENAME = "log/log-%y-%m-%d.txt"
	LOG_TIME_PREFIX = "%H:%M:%S"
	
	def log(self, msg, level=5):
		prefix = time.strftime( self.LOG_TIME_PREFIX )
		msg = "[%2d] %s> %s" %(level, prefix, msg)
		
		if level <= self.LOG_PRINT_LEVEL:
			print msg
		if level <= self.LOG_FILE_LEVEL:
			log_file = self.get_log_file_handle()
			log_file.write( "%s\r\n" % msg )
	
	
	def get_log_file_handle(self):
		filename = os.path.abspath( time.strftime( self.LOG_FILENAME ) )
		handle = open( filename, "a" )
		return handle