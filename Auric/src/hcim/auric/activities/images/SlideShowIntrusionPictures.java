package hcim.auric.activities.images;

import hcim.auric.database.intrusions.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.os.Bundle;
import android.view.View;

public class SlideShowIntrusionPictures extends SlideShowActivity {
	public static final String EXTRA_ID = "extra";

	@Override
	protected List<Picture> getPictures() {
		Bundle extras = getIntent().getExtras();
		String intrusionID = extras.getString(EXTRA_ID);

		IntrusionsDatabase db = IntrusionsDatabase.getInstance(this);
		Intrusion i = db.getIntrusion(intrusionID);

		return i.getImages();
	}

	@Override
	protected int startAt() {
		return 0;
	}

	@Override
	protected void refresh() {
		typeIcon.setVisibility(View.GONE);
		setMessageVisibility(true);
		Picture p = pictures.get(current);
		setMessage(p.getDescription());

		super.refresh();
	}
}
