package model;

public enum Value {
	EMPTY(" "), ONE("1"), TWO("2"), THREE("3"), FOUR("4"), 
	FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), MINE("*");
	
	public final String value;
	
	private Value(String value) {
		this.value = value;
	}
	
	public static Value of(int count) {
		if (count > values().length)
			return null;
		return values()[count];
	}
	
	public String toString() {
		return value;
	}
}