#!/usr/bin/env python

class Messages:
	
	STOP =       chr( 0 )
	
	PLAY =       1
	STOP =       2
	PAUSE =      3
	PREVIOUS =   4
	NEXT =       5
	
	SET_VOLUME = 10
	PLAY_PLAYLIST_ITEM = 20
	
	GET_VOLUME = 110
	
	GET_PLAYBACK_STATUS = 115
	GET_PLAYING_TRACK_LENGTH = 117
	GET_PLAYING_TRACK_POSITION = 118
	GET_CURRENT_TITLE = 120
	
	GET_PLAYLIST = 150
	GET_PLAYLIST_POSITION = 151
	
	ERROR = 200
	
	ERROR_WINAMP_NOT_RUNNING = 5
	
	INFO =  300
	
	INFO_CONNECTED_EVERYTHING_OK       = 10
