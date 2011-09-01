#!/usr/bin/env python
import socket, time, os, traceback, sys, locale, codecs

class Settings:
	
	hostname = ""
	port = 9273
	LOG_PRINT_LEVEL = 5
	LOG_FILE_LEVEL = 10
	LOG_FILENAME = "log/log-%y-%m-%d.txt"
	LOG_TIME_PREFIX = "%H:%M:%S"
	
	def __init__(self):
		self.default_encoding = locale.getdefaultlocale()[1]

	
	def log(self, msg, level=5):
		prefix = time.strftime( self.LOG_TIME_PREFIX )
		msg = "[%2d] %s> %s" %(level, prefix, msg)
		
#		if isinstance(msg, str):
#			msg = msg.decode( self.default_encoding )
		
		if level <= self.LOG_PRINT_LEVEL:
			print msg
		if level <= self.LOG_FILE_LEVEL:
			log_file = self.get_log_file_handle()
			log_file.write( "%s\r\n" % msg )
	
	def log_exception(self):
		exc_type, exc_value, exc_traceback = sys.exc_info()
		lines = traceback.format_exception( exc_type, exc_value, exc_traceback )
		self.log("=== AN EXCEPTION OCCURED ===", level=1)
		[self.log(line.decode( self.default_encoding ), level=1) for line in lines]
	
	def get_log_file_handle(self):
		filename = os.path.abspath( time.strftime( self.LOG_FILENAME ) )
		handle = codecs.open( filename, "a", encoding='utf-8')
		return handle