import distutils.dir_util, os.path

if __name__ == "__main__":
	srcdir = os.path.join( os.path.abspath("Server"), "src")
	destdir = r"\\Jesper-PC\Ward Server\src"
	
	print
	print "Copying ward server from"
	print srcdir
	print "to"
	print destdir
	print
	
	distutils.dir_util.copy_tree( srcdir, destdir )
	
	print "Done!"