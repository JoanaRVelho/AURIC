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
		this.match = "";
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
		if (!faceDetected)
			return "Face not detected";

		//face detected
		StringBuilder builder = new StringBuilder("Face detected");

		if (matchOwner()) {
			builder.append(", Match Owner");
			builder.append(", Difference: ");
			builder.append(difference);
		} else {
			builder.append(", Don't Match");
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

	public String smallDescription() {
		StringBuilder builder = new StringBuilder();

		if (matchOwner()) {
			builder.append("Match Owner");
			builder.append(", Difference: ");
			builder.append(difference);
		} else {
			builder.append("Don't Match");
		}
		return builder.toString();
	}

}
