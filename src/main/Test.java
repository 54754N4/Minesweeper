package main;

import view.MSFrame;

public class Test {
	public static void main(String[] args) {
		int rows = 15,
			cols = 15,
			mines = 20,
			blockSize = 50;
		new MSFrame(rows, cols, mines, blockSize)
			.setVisible(true);
	}
}
