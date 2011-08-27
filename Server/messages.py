#!/usr/bin/env python

class Messages:
	
	STOP =       chr( 0 )
	
	PLAY =       chr( 1 )
	STOP =       chr( 2 )
	PAUSE =      chr( 3 )
	PREVIOUS =   chr( 4 )
	NEXT =       chr( 5 )
	
	SET_VOLUME = chr( 10 )
	
	GET_VOLUME = chr( 110 )
	
	GET_PLAYBACK_STATUS = chr( 115 )
	GET_CURRENT_TITLE = chr( 120 )