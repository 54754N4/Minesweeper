package model;

public class Mark {
	private boolean h1, h2;	// mark for each hypothesis
	
	public Mark() {
		reset();
	}
	
	public boolean first() {
		return h1;
	}
	
	public boolean second() {
		return h2;
	}
	
	public boolean toggleFirst() {
		return h1 = !h1;
	}
	
	public boolean toggleSecond() {
		return h2 = !h2;
	}
	
	public void reset() {
		h1 = h2 = false;
	}
	
	public String toString() {
		if (h1 && h2) return "12";
		else if (h1) return "1";
		else if (h2) return "2";
		return "";
	}
}