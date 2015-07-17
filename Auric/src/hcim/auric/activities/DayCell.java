package hcim.auric.activities;

public class DayCell {
	protected int resourceColor;
	protected String day;
	protected String month;
	protected String year;
	protected boolean today;

	public DayCell(int res, String day, String month, String year) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.resourceColor = res;
		today = false;
	}
}
