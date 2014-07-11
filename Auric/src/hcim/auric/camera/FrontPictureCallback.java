package hcim.auric.camera;

import hcim.auric.authentication.AuditTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;

public class FrontPictureCallback implements Camera.PictureCallback {
	private AuditTask task;

	public FrontPictureCallback(AuditTask task) {
		this.task = task;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		BitmapFactory.Options config = new BitmapFactory.Options();
		config.inPreferredConfig = Bitmap.Config.RGB_565;
		config.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, config);

		bm = rotateBitmap(bm, 270);

		if(bm == null){
			Log.d("SCREEN", "NULL BITMAP");
		}
		
		Log.d("SCREEN", "face captured");
		task.addTaskMessage(AuditTask.ACTION_INTRUSION_DETECTION, bm);
		
		camera.stopPreview();
		camera.release();
	}

	private static Bitmap rotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

}
