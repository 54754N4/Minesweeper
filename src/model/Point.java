package model;

public class Point {
	public int x, y;
	
	public Point(java.awt.Point p) {
		x = p.x;
		y = p.y;
	}
	
	public Point(Point p) {
		x = p.x;
		y = p.y;
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point plusEquals(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Point plus(Cardinal cardinal) {
		return new Point(x+cardinal.dx, y+cardinal.dy);
	}
	
	public Point plusEquals(int i, int j, Point wrap) {
		plusEquals(i,j);
		if (x >= wrap.x) x = 0;
		else if (x < 0) x = wrap.x-1;
		if (y >= wrap.y) y = 0;
		else if (y < 0) y = wrap.y-1;
		return this;
	}
	
	public Point plus(Point p) {
		return new Point(x+p.x, y+p.y);
	}
	
	public boolean equals(Point p) {
		if (p == null) return false;
		return x==p.x && y==p.y;
	}
	
	public String toString() {
		return String.format("(%d, %d)", x, y);
	}
}