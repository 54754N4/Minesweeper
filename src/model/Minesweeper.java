package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Minesweeper {
	private static final Random rand = new Random();
	private final OnMineClickedListener listener;
	public final int rows;
	public final int cols;
	private boolean lost;
	private Cell[][] grid;
	private Set<Point> mines;
	
	public Minesweeper(int rows, int cols, int mines, OnMineClickedListener listener) {
		this.listener = listener;
		this.rows = rows;
		this.cols = cols;
		lost = false;
		this.mines = createBombs(mines);
		init();
	}
	
	private Set<Point> createBombs(int mines) {
		Set<Point> bombs = new HashSet<>();
		while (mines-->0)
			bombs.add(new Point(rand.nextInt(cols), rand.nextInt(rows)));
		return bombs;
	}
	
	private void init() {
		Point point;
		grid = new Cell[cols][rows];
		for (int x=0; x<cols; x++) {
			for (int y=0; y<rows; y++) {
				point = new Point(x,y);
				if (isMine(point))
					grid[x][y] = new Cell(point, Value.MINE, true);
				else 
					grid[x][y] = new Cell(point, Value.EMPTY, false);
			}
		}
		for (int x=0; x<cols; x++) 
			for (int y=0; y<rows; y++)
				if (!grid[x][y].isMine())
					grid[x][y].updateValue();
	}
	
	private boolean isMine(Point point) {
		for (Point mine : mines)
			if (mine.equals(point))
				return true;
		return false;
	}
	
	public List<Point> neighboursOf(Point point) {
		List<Point> neighbours = new ArrayList<>();
		Point current;
		for (Cardinal cardinal : Cardinal.values())
			if (inBounds(current = point.plus(cardinal)))
				neighbours.add(current);
		return neighbours;
	}
	
	public int count(Point point) {
		int sum = 0;
		for (Point current : neighboursOf(point))
			if (isMine(current))
				sum++;
		return sum;
	}
	
	private boolean inBounds(Point point) {
		return (0<=point.x && point.x<cols && 0<=point.y && point.y<rows);
	}
	
	public Cell get(Point point) {
		return get(point.x, point.y);
	}
	
	public Cell get(int x, int y) {
		return grid[x][y];
	}
	
	private String debug() {
		StringBuilder sb = new StringBuilder();
		for (int row=0; row<rows; row++) {
			for (int col=0; col<cols; col++)
				sb.append(get(col,row).toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private void lost() {
		listener.mineClicked();
		lost = true;
		for (Cell[] row : grid)
			for (Cell cell : row)
				if (!cell.isVisible())
					cell.reveal();
	}
	
	public static void main(String[] args) {
		Minesweeper ms = new Minesweeper(5,15,20, null);
		System.out.println(ms.debug());
	}

	public boolean isLost() {
		return lost;
	}
	
	public class Cell {
		private Value value;
		private final Point point;
		private boolean visible, flagged, mine;
		private String print;
		private Mark mark;
		
		public Cell(Point point, Value value, boolean mine) {
			this.value = value;
			this.point = point;
			this.mine = mine;
			visible = false;
			print = value.toString();
			mark = new Mark();
		}
		
		public boolean isVisible() {
			return visible;
		}
		
		public Value getValue() {
			return value;
		}
		
		public int count() {
			return Minesweeper.this.count(point);
		}
		
		public boolean isMine() {
			return mine;
		}
		
		public boolean isFlagged() {
			return flagged;
		}
		
		public boolean isMarked() {
			return getMark().first() || getMark().second();
		}
		
		public void toggleFlag() {
			if (!visible) {
				if (flagged = !flagged) print = "F";
				else print = value.toString();
			}
		}
		
		public void reveal() {
			visible = true;
			if (isMine()) {
				if (!isLost()) lost();
			} else if (value == Value.EMPTY) spreadFrom(point);
			else print = value.toString();
		}
		
		public void spreadFrom(Point point) {
			Cell cell;
			for (Point neighbour : neighboursOf(point)) {
				cell = grid[neighbour.x][neighbour.y];
				if (!cell.visible && !cell.flagged) 
					cell.reveal();
			}
		}
		
		public void toggleHypothesis(boolean first) {
			if (first) getMark().toggleFirst();
			else getMark().toggleSecond();
		}
		
		public String toString() {
			return print;
		}

		public void updateValue() {
			value = Value.of(count());
			print = value.toString();
		}

		public Mark getMark() {
			return mark;
		}
	}
}