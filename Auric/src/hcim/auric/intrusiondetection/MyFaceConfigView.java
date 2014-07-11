package hcim.auric.intrusiondetection;

import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

public class MyFaceConfigView extends Activity {
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private ImageView img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// View decorView = getWindow().getDecorView();
		// int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		// decorView.setSystemUiVisibility(uiOptions);
		// ActionBar actionBar = getActionBar();
		// actionBar.hide();

		setContentView(R.layout.my_pic_config);

		Button myPicture = (Button) findViewById(R.id.change_picture_button);
		img = (ImageView) findViewById(R.id.image_view);

		Bitmap bitmap = null;
		Picture p = ConfigurationDatabase.getMyPicture();

		if (p != null)
			bitmap = p.getImage();

		if (bitmap != null) {
			img.setImageBitmap(bitmap);
		}

		myPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
			}
		});
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			String name = ConfigurationDatabase.MY_PICTURE_ID;

			FaceRecognition recognition = FaceRecognition.getInstance();
			boolean result = recognition.trainPicture(imageBitmap, name);

			if (result) {
				img.setImageBitmap(imageBitmap);

				// store
				Picture p = new Picture(name, imageBitmap);
				ConfigurationDatabase.setMyPicture(p);
				if (ConfigurationDatabase.getNegativePicture() != null)
					recognition.stopTrain();

			} else {
				Toast.makeText(getApplicationContext(),
						"Face Detection Failed! Take another picture!",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
