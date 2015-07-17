package hcim.auric.activities.images;

import hcim.auric.Intrusion;
import hcim.auric.Picture;
import hcim.auric.data.SessionDatabase;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

public class SlideShowIntrusionPictures extends SlideShowActivity {
	public static final String EXTRA_ID = "extra";

	@Override
	protected List<Picture> getPictures() {
		Bundle extras = getIntent().getExtras();
		ArrayList<String> intrusions = extras.getStringArrayList(EXTRA_ID);
		List<Picture> result = new ArrayList<Picture>();

		SessionDatabase db = SessionDatabase.getInstance(this);
		for (String intrusion : intrusions) {
			Intrusion i = db.getIntrusion(intrusion);
			result.addAll(i.getImages());
		}
		return result;
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
