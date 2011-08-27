import distutils.dir_util, os.path

if __name__ == "__main__":
	srcdir = os.path.abspath("Server")
	destdir = "Z:\\ward"
	
	print
	print "Copying ward server from"
	print srcdir
	print "to"
	print destdir
	print
	
	distutils.dir_util.copy_tree( srcdir, destdir )
	
	print "Done!"