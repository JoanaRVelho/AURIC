package hcim.auric.record;

import hcim.auric.database.configs.PicturesDatabase;
import hcim.auric.database.intrusions.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import hcim.auric.utils.StringGenerator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

public abstract class RunInteraction extends Activity {

	protected Intrusion intrusion;
	private Spinner spinnerSeverity;
	protected IntrusionsDatabase intDB;

	@Override
	public void finish() {
		if (intrusion.isChecked()) {
			super.finish();
		} else {
			markIntrusionAlertDialog();
		}
	}

	private void markIntrusionAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(RunInteraction.this);
		alertDialog.setTitle("Severity of the intrusion");
		alertDialog.setView(spinnerView());
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						int tag = spinnerSeverity.getSelectedItemPosition();
						intrusion.setTag(tag);
						intDB.updateIntrusion(intrusion);
						updateFaceRecognitionData(tag);

						RunInteraction.super.finish();
					}
				});
		alertDialog.show();
	}

	@SuppressLint("InflateParams")
	private View spinnerView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout view = new RelativeLayout(this);
		view = (RelativeLayout) inflater.inflate(R.layout.severity, null);

		spinnerSeverity = (Spinner) view.findViewById(R.id.severity_spinner);
		spinnerSeverity.setAdapter(new SeverityAdapter(RunInteraction.this));

		return view;
	}

	private void updateFaceRecognitionData(int tag) {
		boolean result = (tag == Intrusion.NONE) ? trainAsOwner()
				: trainAsIntruder();

		if (result)
			Toast.makeText(RunInteraction.this, "Face Recognition updated!",
					Toast.LENGTH_SHORT).show();
	}

	private boolean trainAsOwner() {
		FaceRecognition faceRecognition = FaceRecognition.getInstance(this);
		PicturesDatabase picsDB = PicturesDatabase.getInstance(this);

		for (Picture p : intrusion.getImages()) {
			if (faceRecognition.trainPicture(p.getImage(), p.getID())) {
				p.setID(StringGenerator.generateOwnerName());
				p.setType(FaceRecognition.MY_PICTURE_TYPE);
				picsDB.addPicture(p);
				intDB.updatePictureType(p);
				return true;
			}
		}
		return false;
	}

	private boolean trainAsIntruder() {
		FaceRecognition faceRecognition = FaceRecognition.getInstance(this);
		PicturesDatabase picsDB = PicturesDatabase.getInstance(this);

		for (Picture p : intrusion.getImages()) {
			if (faceRecognition.trainPicture(p.getImage(), p.getID())) {
				p.setType(FaceRecognition.INTRUDER_PICTURE_TYPE);
				picsDB.addPicture(p);
				intDB.updatePictureType(p);
				return true;
			}
		}
		return false;
	}

	protected void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(RunInteraction.this);
		alertDialog.setTitle("Delete Intrusion Log");
		alertDialog
				.setMessage("Are you sure that you want to delete this intrusion log?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delete();
						superFinish();
					}

				});
		alertDialog.setNegativeButton("NO", null);
		alertDialog.show();
	}
	
	private void superFinish() {
		super.finish();
	}

	protected abstract void delete();
}
