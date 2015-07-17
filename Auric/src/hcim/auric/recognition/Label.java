package hcim.auric.recognition;

public class Label {
	String name;
	int number;

	public Label(String s, int n) {
		name = s;
		number = n;
	}

	@Override
	public String toString() {
		return name + "," + number;
	}
}