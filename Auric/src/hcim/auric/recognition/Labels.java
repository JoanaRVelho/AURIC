package hcim.auric.recognition;

import hcim.auric.utils.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * 
 * @author Joana Velho
 * 
 *         An adaptation of {@link https
 *         ://github.com/ayuso2013/face-recognition/
 *         blob/master/src/org/opencv/javacv/facerecognition/labels.java}
 * 
 */
public class Labels {
	private static final String FILENAME = "faces.txt";

	private ArrayList<Label> list;
	private String path;

	class Label {
		String label;
		int number;

		public Label(String s, int n) {
			label = s;
			number = n;
		}

		@Override
		public String toString() {
			return label + "," + number;
		}
	}

	public Labels(String path) {
		this.path = path;
		read();
	}

	public boolean isEmpty() {
		return list != null && list.isEmpty();
	}

	public void add(String s, int n) {
		list.add(new Label(s, n));
	}

	public String get(int i) {
		Iterator<Label> Ilabel = list.iterator();
		while (Ilabel.hasNext()) {
			Label l = Ilabel.next();
			if (l.number == i)
				return l.label;
		}
		return "";
	}

	public int get(String s) {
		Iterator<Label> iterator = list.iterator();

		while (iterator.hasNext()) {
			Label l = iterator.next();
			if (l.label.equalsIgnoreCase(s))
				return l.number;
		}
		return -1;
	}

	public void save() {
		try {
			File f = new File(path, FILENAME);
			if (!f.exists()) {
				f.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));

			for (Label l : list) {
				bw.write(l.toString());
				bw.newLine();
			}

			bw.close();

		} catch (IOException e) {
			LogUtils.exception(e);
		}
	}

	public void read() {
		File f = new File(path, FILENAME);
		list = new ArrayList<Label>();

		if (f.exists()) {
			try {
				FileInputStream fstream = new FileInputStream(f);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fstream));

				String strLine;

				while ((strLine = br.readLine()) != null) {
					StringTokenizer tokens = new StringTokenizer(strLine, ",");
					String s = tokens.nextToken();
					String sn = tokens.nextToken();

					list.add(new Label(s, Integer.parseInt(sn)));
				}
				br.close();
				fstream.close();
			} catch (IOException e) {
				LogUtils.exception(e);
			}
		}
	}

	public int max() {
		int m = 0;
		Iterator<Label> Ilabel = list.iterator();
		while (Ilabel.hasNext()) {
			Label l = Ilabel.next();
			if (l.number > m)
				m = l.number;
		}
		return m;
	}

	public void remove(String picID) {
		// read();
		int idx = -1;
		Label l;
		for (int i = 0; i < list.size(); i++) {
			l = list.get(i);

			if (idx != -1) {
				l.number--;
			} else {
				if (l.label.equals(picID)) {
					idx = i;
				}
			}
		}
		list.remove(idx);

		save();
	}
}
