package hcim.auric.recognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hcim.intrusiondetection.R;

public class FaceDatabase {
	private static int[][] RESOURCES = {
			{ R.drawable.s6p1, R.drawable.s6p10, R.drawable.s6p2,
					R.drawable.s6p3, R.drawable.s6p4, R.drawable.s6p5,
					R.drawable.s6p6, R.drawable.s6p7, R.drawable.s6p8,
					R.drawable.s6p9 },

			{ R.drawable.s7p1, R.drawable.s7p10, R.drawable.s7p2,
					R.drawable.s7p3, R.drawable.s7p4, R.drawable.s7p5,
					R.drawable.s7p6, R.drawable.s7p7, R.drawable.s7p8,
					R.drawable.s7p9 },

			{ R.drawable.s9p1, R.drawable.s9p10, R.drawable.s9p2,
					R.drawable.s9p3, R.drawable.s9p4, R.drawable.s9p5,
					R.drawable.s9p6, R.drawable.s9p7, R.drawable.s9p8,
					R.drawable.s9p9 },

			{ R.drawable.s10p1, R.drawable.s10p10, R.drawable.s10p2,
					R.drawable.s10p3, R.drawable.s10p4, R.drawable.s10p5,
					R.drawable.s10p6, R.drawable.s10p7, R.drawable.s10p8,
					R.drawable.s10p9 },

			{ R.drawable.s12p1, R.drawable.s12p10, R.drawable.s12p2,
					R.drawable.s12p3, R.drawable.s12p4, R.drawable.s12p5,
					R.drawable.s12p6, R.drawable.s12p7, R.drawable.s12p8,
					R.drawable.s12p9 },

			{ R.drawable.s14p1, R.drawable.s14p10, R.drawable.s14p2,
					R.drawable.s14p3, R.drawable.s14p4, R.drawable.s14p5,
					R.drawable.s14p6, R.drawable.s14p7, R.drawable.s14p8,
					R.drawable.s14p9 },

			{ R.drawable.s15p1, R.drawable.s15p10, R.drawable.s15p2,
					R.drawable.s15p3, R.drawable.s15p4, R.drawable.s15p5,
					R.drawable.s15p6, R.drawable.s15p7, R.drawable.s15p8,
					R.drawable.s15p9 },

			{ R.drawable.s18p1, R.drawable.s18p10, R.drawable.s18p2,
					R.drawable.s18p3, R.drawable.s18p4, R.drawable.s18p5,
					R.drawable.s18p6, R.drawable.s18p7, R.drawable.s18p8,
					R.drawable.s18p9 },

			{ R.drawable.s20p1, R.drawable.s20p10, R.drawable.s20p2,
					R.drawable.s20p3, R.drawable.s20p4, R.drawable.s20p5,
					R.drawable.s20p6, R.drawable.s20p7, R.drawable.s20p8,
					R.drawable.s20p9 },

			{ R.drawable.s21p1, R.drawable.s21p10, R.drawable.s21p2,
					R.drawable.s21p3, R.drawable.s21p4, R.drawable.s21p5,
					R.drawable.s21p6, R.drawable.s21p7, R.drawable.s21p8,
					R.drawable.s21p9 },

			{ R.drawable.s22p1, R.drawable.s22p10, R.drawable.s22p2,
					R.drawable.s22p3, R.drawable.s22p4, R.drawable.s22p5,
					R.drawable.s22p6, R.drawable.s22p7, R.drawable.s22p8,
					R.drawable.s22p9 },

			{ R.drawable.s23p1, R.drawable.s23p10, R.drawable.s23p2,
					R.drawable.s23p3, R.drawable.s23p4, R.drawable.s23p5,
					R.drawable.s23p6, R.drawable.s23p7, R.drawable.s23p8,
					R.drawable.s23p9 },

			{ R.drawable.s27p1, R.drawable.s27p10, R.drawable.s27p2,
					R.drawable.s27p3, R.drawable.s27p4, R.drawable.s27p5,
					R.drawable.s27p6, R.drawable.s27p7, R.drawable.s27p8,
					R.drawable.s27p9 },

			{ R.drawable.s30p1, R.drawable.s30p10, R.drawable.s30p2,
					R.drawable.s30p3, R.drawable.s30p4, R.drawable.s30p5,
					R.drawable.s30p6, R.drawable.s30p7, R.drawable.s30p8,
					R.drawable.s30p9 },

			{ R.drawable.s33p1, R.drawable.s33p10, R.drawable.s33p2,
					R.drawable.s33p3, R.drawable.s33p4, R.drawable.s33p5,
					R.drawable.s33p6, R.drawable.s33p7, R.drawable.s33p8,
					R.drawable.s33p9 },

			{ R.drawable.s38p1, R.drawable.s38p10, R.drawable.s38p2,
					R.drawable.s38p3, R.drawable.s38p4, R.drawable.s38p5,
					R.drawable.s38p6, R.drawable.s38p7, R.drawable.s38p8,
					R.drawable.s38p9 } };

	public static void build(Context c) {
		FaceRecognition faceRecognition = FaceRecognition.getInstance(c);
		String name = "s";

		for (int i = 0; i < RESOURCES.length; i++) {
			for (int j = 0; j < RESOURCES[i].length; j++) {
			//	Bitmap gray = BitmapFactory.decodeResource(c.getResources(),
			//			RESOURCES[i][j]);
			//	faceRecognition.trainGrayPicture(gray, name + (i + 1));

			//	gray.recycle();
			}
		}
	}
}
