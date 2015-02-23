package hcim.auric.recognition;


public class RecognitionResult {
	private boolean faceDetected;
	private boolean faceRecognized;
	private String match;
	private int difference;

	public RecognitionResult(boolean faceDetected, boolean faceRecognized,
			String match, int difference) {
		this.faceDetected = faceDetected;
		this.faceRecognized = faceRecognized;
		this.match = match;
		this.difference = difference;
	}

	public RecognitionResult() {
		this.faceDetected = false;
		this.faceRecognized = false;
		this.match = null;
		this.difference = -1;
	}

	public boolean isFaceDetected() {
		return faceDetected;
	}

	public boolean isFaceRecognized() {
		return faceRecognized;
	}

	public String getMatch() {
		return match;
	}

	public int getDifference() {
		return difference;
	}

	public String description() {
		StringBuilder builder = new StringBuilder();
		builder.append("Face Detected: ");
		builder.append(faceDetected);
		builder.append(", Face Recognized as Owner: ");
		builder.append(faceRecognized);

		if (faceRecognized) {
			builder.append(", Difference: ");
			builder.append(difference);
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return "Face Recognition Result -> [faceDetected=" + faceDetected
				+ ", faceRecognized=" + faceRecognized + ", match=" + match
				+ ", difference=" + difference + "]";
	}

	public boolean matchOwner() {
		return FaceRecognition.matchsOwnerName(match);
	}

}
