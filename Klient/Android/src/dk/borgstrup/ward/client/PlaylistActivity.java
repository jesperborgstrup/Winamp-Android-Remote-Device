package dk.borgstrup.ward.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import dk.borgstrup.ward.client.connection.Messages;
import dk.borgstrup.ward.client.connection.WardConnectionListener;

public class PlaylistActivity extends Activity implements WardConnectionListener {
	
	private WardApplication app;
	private Resources res;
	
	private ProgressDialog setupDialog;
	
	private ListView list;
	
	private PlaylistAdapter adapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        app = (WardApplication)getApplication();
        
        res = getResources();
        
        app.conn.addListener( this );

    	list = (ListView)findViewById(R.id.playlist_list);
        setupDialog = new ProgressDialog(this);
        setupDialog.setTitle( R.string.retrieving_playlist );
        setupDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setupDialog.setCancelable(true);
        setupDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				PlaylistActivity.this.finish();
			}
		});
        
        if ( app.playlist == null ) {
        	setupDialog.show();
        	app.conn.requestPlaylist();
        } else {
        	initializeListView();
        }
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	app.conn.removeListener( this );
    }

	private void initializeListView() {
        this.adapter = new PlaylistAdapter(this, app.playlist);

        list = (ListView)findViewById(R.id.playlist_list);
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

}
