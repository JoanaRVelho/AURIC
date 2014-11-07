package hcim.auric.activities.images;

import hcim.auric.recognition.Picture;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.hcim.intrusiondetection.R;

public abstract class PicturesGrid extends Activity {

	protected ImageAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pictures_grid);
		
		adapter = getAdapter();

		if (adapter == null) {
			Toast.makeText(this, "No faces were detected!", Toast.LENGTH_LONG)
					.show();
			finish();
		}
	}
	
	@Override
	protected void onResume() {
		adapter = getAdapter();

		if (adapter == null) {
			Toast.makeText(this, "No faces were detected!", Toast.LENGTH_LONG)
					.show();
			finish();
		}
		
		GridView gridView = (GridView) findViewById(R.id.grid_view);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Picture selected = (Picture) adapter.getItem(position);
				onPictureSelected(selected, position);
			}
		});
		super.onResume();
	}

	protected abstract ImageAdapter getAdapter();

	protected abstract void onPictureSelected(Picture p, int position);
}