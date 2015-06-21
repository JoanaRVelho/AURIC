package hcim.auric.camera.inconspicuous;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.AuditTask;
import hcim.auric.audit.TaskMessage;
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

		TaskMessage t = new TaskMessage(AuditTask.ACTION_NEW_PICTURE);
		t.setData(data);
		queue.addTaskMessage(t);
	}
}
