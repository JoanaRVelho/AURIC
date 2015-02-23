package hcim.auric.record.events;

import android.view.View;

public interface Adapter {

	public int getCount();

	public Object getItem(int position);

	public View getView(int position);
}
