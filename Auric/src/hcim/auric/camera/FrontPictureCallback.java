package hcim.auric.camera;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.IAuditTask;
import hcim.auric.audit.TaskMessage;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;

public class FrontPictureCallback implements Camera.PictureCallback {
	protected AuditQueue queue;

	public FrontPictureCallback(AuditQueue queue) {
		this.queue = queue;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		camera.stopPreview();
		camera.release();

		BitmapFactory.Options config = new BitmapFactory.Options();
		config.inPreferredConfig = Bitmap.Config.RGB_565;
		config.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, config);

		bm = rotateBitmap(bm, 270);

		TaskMessage t = new TaskMessage(IAuditTask.ACTION_NEW_PICTURE);
		t.setPic(bm);
		queue.addTaskMessage(t);

	}

	public static Bitmap rotateBitmap(Bitmap source) {
		return rotateBitmap(source, 270);
	}

	private static Bitmap rotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}
}
