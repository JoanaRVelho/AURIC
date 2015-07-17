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
import java.util.List;
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
public class LabelRepository {
	private static final String FILENAME = "faces.txt";

	private List<Label> list;
	private String path;

	public LabelRepository(String path) {
		this.path = path;
		load();
	}

	public boolean isEmpty() {
		return list == null || list.isEmpty();
	}

	public int numberOfSubjects() {
		// int m = 0;
		// for (Label l : list) {
		// if (l.number > m)
		// m = l.number;
		// }
		// return m;
		return list.size();
	}

	public void addLabel(String s, int n) {
		list.add(new Label(s, n));
	}

	public void removeLabel(String label) {
		Label l;
	
		boolean found = false;
		for (int i = 0; i < list.size(); i++) {
			l = list.get(i);
	
			if (found) {
				l.number--;
			} else {
				if (l.name.equals(label)) {
					list.remove(i);
					found = true;
				}
			}
		}
		store();
	}

	public String getLabelName(int number) {
		for (Label l : list) {
			if (l.number == number)
				return l.name;
		}
		return "";
	}

	public int getLabelNumber(String label) {
		for (Label l : list) {
			if (l.name.equalsIgnoreCase(label))
				return l.number;
		}
		return -1;
	}

	public void load() {
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

	public void store() {
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
}
