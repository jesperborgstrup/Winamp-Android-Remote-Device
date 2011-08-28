package dk.borgstrup.ward.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlaylistAdapter extends BaseAdapter {
	
	private Context context;
	private Playlist playlist;

	public PlaylistAdapter(Context context, Playlist playlist) {
		super();
		this.context = context;
		this.playlist = playlist;
	}

	@Override
	public int getCount() {
		return playlist.size();
	}

	@Override
	public Object getItem(int position) {
		return playlist.get( position );
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.playlist_row, null);
		}
		
		PlaylistItem item = playlist.get( position );
		if (item != null) {
			TextView tracknumber = (TextView)v.findViewById(R.id.playlist_row_tracknumber);
			TextView title = (TextView)v.findViewById(R.id.playlist_row_title);
			
			tracknumber.setText( (position+1) + ". " );
			title.setText( item.getTitle() );
		}
		
		return v;
	}

}
