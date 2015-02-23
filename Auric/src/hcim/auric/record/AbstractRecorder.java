package hcim.auric.record;

import android.content.Context;

public abstract class AbstractRecorder implements IRecorder{
	protected Context context;

	public AbstractRecorder(Context c) {
		context = c;
	}

	@Override
	public String toString() {
		return type();
	}
}
