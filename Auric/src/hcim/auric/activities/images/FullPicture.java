package hcim.auric.activities.images;

import hcim.auric.database.configs.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hcim.intrusiondetection.R;

public class FullPicture extends Activity {
	public static final String EXTRA_ID = "extra";

	private Picture picture;
	private PicturesDatabase db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);

		Bundle extras = getIntent().getExtras();
		String id = extras.getString(EXTRA_ID);

		db = PicturesDatabase.getInstance(FullPicture.this);
		picture = db.getPicture(id);

		ImageView img = (ImageView) findViewById(R.id.full_image_view);
		img.setImageBitmap(picture.getImage());

		ImageView typeImg = (ImageView) findViewById(R.id.type_img);

		String type = picture.getType();

		if (type != null) {
			if (type.equals(FaceRecognition.INTRUDER_PICTURE_TYPE))
				typeImg.setImageResource(R.drawable.red);

			if (type.equals(FaceRecognition.MY_PICTURE_TYPE))
				typeImg.setImageResource(R.drawable.green);

			if (type.equals(FaceRecognition.UNKNOWN_PICTURE_TYPE))
				typeImg.setImageResource(R.drawable.black);
		}
	}

	private void trashButtonWarning() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(FullPicture.this);
		alertDialog.setTitle("Delete Picture");
		alertDialog.setMessage("You can't delete all of your pictures!");
		alertDialog.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}

				});
		alertDialog.show();
	}

	public void trashButton(View v) {
		List<Picture> list = db.getMyPictures();
		if (list == null || list.size() == 1) {
			trashButtonWarning();
		} else {
			AlertDialog.Builder alertDialog;
			alertDialog = new AlertDialog.Builder(FullPicture.this);
			alertDialog.setTitle("Delete Picture");
			alertDialog
					.setMessage("Are you sure that you want to delete this picture?");
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							delete();
							finish();
						}

					});
			alertDialog.setNegativeButton("NO", null);
			alertDialog.show();
		}
	}

	private void delete() {
		FaceRecognition fr = FaceRecognition.getInstance(FullPicture.this);
		fr.untrainPicture(picture.getID());
		PicturesDatabase db = PicturesDatabase.getInstance(FullPicture.this);
		db.removePicture(picture);
	}

}