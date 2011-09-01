package dk.borgstrup.ward.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import dk.borgstrup.ward.client.connection.Messages;
import dk.borgstrup.ward.client.connection.WardConnectionListener;

public class PlaylistActivity extends Activity implements WardConnectionListener {
	
	private WardApplication app;
	
	private ProgressDialog setupDialog;
	
	private ListView list;
	
	private PlaylistAdapter adapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        app = (WardApplication)getApplication();
        
        app.conn.addListener( this );

    	list = (ListView)findViewById(R.id.playlist_list);
        setupDialog = new ProgressDialog(this);

        setupDialog.setTitle( R.string.retrieving_playlist );
        setupDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setupDialog.setCancelable(true);{}
        setupDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				PlaylistActivity.this.finish();
			}
		});
        
        if ( app.playlist.size() == 0 ) {
        	setupDialog.show();
        	app.conn.requestPlaylist();
        } else {
        	initializeListView();
        }
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	scrollToCurrentItem();
    }

	private void scrollToCurrentItem() {
		if (app.playlist.getCurrentItem() >= 0) {
    		list.setSelection( app.playlist.getCurrentItem() );
    	}
	}
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	app.conn.removeListener( this );
    }

	private void initializeListView() {
        this.adapter = new PlaylistAdapter(this, app.playlist);

        list.setAdapter( this.adapter );
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				app.conn.playPlaylistItem( position );
			}
		});
/*
        list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				initializeListViewMenu(position);
				return true;
			}
		});
		*/
	}
    
	@Override
	public void receivedMessage(final int message, final Bundle data) {

		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				switch (message) {
				case Messages.GET_PLAYLIST:
		        	initializeListView();
					if (setupDialog.isShowing())
						setupDialog.dismiss();
					scrollToCurrentItem();
					break;
				case Messages.GET_PLAYLIST_POSITION:
//					int position = data.getInt( Messages.EXTRA_PLAYLIST_POSITON );
					PlaylistActivity.this.adapter.notifyDataSetChanged();
					break;
				case Messages.ERROR:
					int error = data.getInt( Messages.EXTRA_ERROR );
					switch (error) {
					case Messages.ERROR_WINAMP_NOT_RUNNING:
//						finish();
						break;
					}
					break;
				}
			}
		});
	}

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.playlist_refresh:
    		app.conn.requestPlaylist();
    		app.conn.requestPlaylistPosition();
    		setupDialog.show();
    		return true;
    	default:
    		return false;
    	}
    }

}
