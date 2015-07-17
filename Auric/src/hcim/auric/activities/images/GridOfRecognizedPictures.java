package hcim.auric.activities.images;

import hcim.auric.Picture;
import hcim.auric.activities.face.DetectionActivity;
import hcim.auric.data.PicturesDatabase;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

public class GridOfRecognizedPictures extends Activity {
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

	protected ImageAdapter getAdapter() {
		PicturesDatabase db = PicturesDatabase.getInstance(this);
		List<Picture> list = db.getAllPictures();

		return new ImageAdapter(this, list);
	}

	public void addPictures(View v) {
		Intent i = new Intent(this, DetectionActivity.class);
		startActivity(i);
	}

	protected void onPictureSelected(Picture p, int position) {
		Intent i = new Intent(getApplicationContext(), FullPicture.class);
		i.putExtra(FullPicture.EXTRA_ID, p.getID());
		startActivity(i);
	}
}
