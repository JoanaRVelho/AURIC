package hcim.auric.activities.images;

import hcim.auric.Picture;
import hcim.auric.recognition.FaceRecognition;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.hcim.intrusiondetection.R;

@SuppressLint("InflateParams")
public class ImageAdapter extends BaseAdapter {
	private Context context;
	private List<Picture> pictures;

	public ImageAdapter(Context c, List<Picture> list) {
		context = c;
		pictures = list;
	}

	@Override
	public int getCount() {
		return pictures.size();
	}

	public void setPictureType(String type, int idx) {
		pictures.get(idx).setType(type);
	}

	@Override
	public Object getItem(int position) {
		return pictures.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		Picture p = pictures.get(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView = new View(context);

		gridView = inflater.inflate(R.layout.grid_item, null);

		ImageView face = (ImageView) gridView.findViewById(R.id.img_face);
		ImageView typeIcon = (ImageView) gridView.findViewById(R.id.type_icon);

		face.setImageBitmap(p.getImage());
		face.setScaleType(ImageView.ScaleType.CENTER_CROP);

		String type = p.getType();
		if (type != null) {
			if (type.equals(FaceRecognition.getMyPictureType())) {
				typeIcon.setImageResource(R.drawable.green);
			} else if (type.equals(FaceRecognition.getIntruderPictureType())) {
				typeIcon.setImageResource(R.drawable.red);
			}else{
				typeIcon.setVisibility(View.INVISIBLE);
			}
		}
		gridView.setLayoutParams(new GridView.LayoutParams(300, 300));

		return gridView;
	}

	/*
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { Picture picture = pictures.get(position); String type =
	 * picture.getType();
	 * 
	 * ImageView img = new ImageView(context);// :(
	 * img.setImageBitmap(picture.getImage()); img.setLayoutParams(new
	 * GridView.LayoutParams(300, 300));
	 * img.setScaleType(ImageView.ScaleType.CENTER_CROP);
	 * 
	 * if (type == null) { img.setBackgroundResource(R.drawable.border_black); }
	 * else if (type.equals(FaceRecognition.MY_PICTURE_TYPE)) {
	 * img.setBackgroundResource(R.drawable.border_green); } else if
	 * (type.equals(FaceRecognition.INTRUDER_PICTURE_TYPE)) {
	 * img.setBackgroundResource(R.drawable.border_red); }
	 * 
	 * return img;
	 * 
	 * }
	 */
}