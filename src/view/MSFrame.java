package view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MSFrame extends JFrame {
	private static final long serialVersionUID = -4066530300892921711L;
	private MSFPanel panel;
	private int rows, cols, mines, blockSize;
	private final AbstractAction newGame, setMines, setSize, setBlockSize; 
	
	public MSFrame(int rows, int cols, int mines, int blockSize) {
		this.rows = rows; 
		this.cols = cols;
		this.mines = mines;
		this.blockSize = blockSize;
		newGame = new NewGameAction();
		setMines = new SetMinesAction();
		setSize = new SetSizeAction();
		setBlockSize = new SetBlockSizeAction();
		setTitle("Minesweeper - Press H for controls");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		reset();
	}
	
	private void createActions() {
		panel.bind("SHIFT_Z", panel.getKey(KeyEvent.VK_Z, ActionEvent.SHIFT_MASK), newGame);
		panel.bind("SHIFT_X", panel.getKey(KeyEvent.VK_X, ActionEvent.SHIFT_MASK), setMines);
		panel.bind("SHIFT_C", panel.getKey(KeyEvent.VK_C, ActionEvent.SHIFT_MASK), setSize);
		panel.bind("SHIFT_V", panel.getKey(KeyEvent.VK_V, ActionEvent.SHIFT_MASK), setBlockSize);
	}
	
	private void reset() {
		if (panel != null) remove(panel);
		setContentPane(panel = new MSFPanel(rows, cols, mines, blockSize));
		createActions();
		pack();
	}
	
	private void cry(String text) {
		JOptionPane.showMessageDialog(new JFrame(), text);
	}
	
	public class NewGameAction extends AbstractAction {
		private static final long serialVersionUID = 2943676029905810728L;

		public NewGameAction() {
			super("New Game");
			putValue(SHORT_DESCRIPTION, "Creates a new random map");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			reset();
		}
	}
	
	public class SetMinesAction extends AbstractAction {
		private static final long serialVersionUID = -842112445953116406L;

		public SetMinesAction() {
			super("Set Mines");
			putValue(SHORT_DESCRIPTION, "Sets the number of mines");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String string = JOptionPane.showInputDialog("Give mines number plz : ");
			try { 
				if (string == null || string.equals("")) return;
				mines = Integer.parseInt(string);
				reset();
			} catch (NumberFormatException e) { cry("Invalid number bruh."); }
		}
	}
	
	public class SetSizeAction extends AbstractAction {
		private static final long serialVersionUID = 2617739297700015642L;

		public SetSizeAction() {
			super("Set Size");
			putValue(SHORT_DESCRIPTION, "Sets new rows/cols for map");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String input = JOptionPane
					.showInputDialog("Give new size as such row,cols (e.g. 10,10): ");
			if (input == null || input.equals(""))
				return;
			String[] string = input.split(",");
			if (string.length != 2 && string.length != 1) {
				cry("Please give only 2 values separated by a comma.");
				return;
			} else if (string.length == 1) return;
			try { 
				rows = Integer.parseInt(string[0]);
				cols = Integer.parseInt(string[1]);
				reset();
			} catch (NumberFormatException e) { cry("Invalid number bruh."); }
		}
	}
	
	public class SetBlockSizeAction extends AbstractAction {
		private static final long serialVersionUID = -7626906665943739811L;

		public SetBlockSizeAction() {
			super("Set Block Size");
			putValue(SHORT_DESCRIPTION, "Sets new block size for map");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String string = JOptionPane.showInputDialog("Give new block size plz : ");
			try { 
				if (string == null || string.equals("")) return;
				blockSize = Integer.parseInt(string);
				reset();
			} catch (NumberFormatException ex) { cry("Invalid number bruh."); }
		}
		
	}
}