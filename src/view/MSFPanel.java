package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import model.Minesweeper;
import model.OnMineClickedListener;
import model.Point;
import model.Minesweeper.Cell;

public class MSFPanel extends JPanel implements OnMineClickedListener, MouseListener  {
	private static final long serialVersionUID = 4973684974330205487L;
	private static final Color SELECTED_COLOR = Color.RED, 
			HIDDEN_COLOR = Color.LIGHT_GRAY, 
			HIGHLIGHT_COLOR_1 = Color.RED, 
			HIGHLIGHT_COLOR_2 = Color.BLUE;
	public final int BLOCK_SIZE, SLIM = 1, THICK = 3;
	private final Font font; 
	public int rows, cols;
	public final Dimension SIZE;
	public final Minesweeper ms;
	private ActionMap actionMap;
	private InputMap inputMap;
	private Point selected, wrapAround;
	
	public MSFPanel(int rows, int cols, int mines, int blockSize) {
		setLayout(null);
		this.rows = rows;
		this.cols = cols;
		BLOCK_SIZE = blockSize;
		int width = (int) (cols*BLOCK_SIZE),
			height = (int) (rows*BLOCK_SIZE);
		SIZE = new Dimension(width, height);
		ms = new Minesweeper(rows, cols, mines, this);
		selected = new Point(cols/2-1, rows/2-1);
		wrapAround = new Point(cols, rows);
		font = new Font("Verdana", Font.BOLD, BLOCK_SIZE);
		setBounds(0, 0, width, height);
		setKeyBindings();
		addMouseListener(this);
	}
	
	public Point pointFromCoords(int x, int y) {
		Point p = new Point((x - x%BLOCK_SIZE)/BLOCK_SIZE, 
				(y - y%BLOCK_SIZE)/BLOCK_SIZE);
		return p;
	}
	
	@Override
	public void mineClicked() {
		System.out.println("Mine selected. Lost");
	}
	
	@Override
	public Dimension getPreferredSize() {
		return SIZE;
	}
	
	// Drawing code
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawCells(g);
		drawGrid(g);
		drawSelected(g);
	}
	
	private void drawCells(Graphics g) {
		for (int x=0;x<cols;x++) 
			for (int y=0; y<rows;y++) 
				drawCell(g, x, y);
	}

	private void drawCell(Graphics g, int x, int y) {
		Cell cell = ms.get(x, y);
		if (cell.isVisible())
			writeText(g, x, y, cell.toString());
		else {
			Color color = HIDDEN_COLOR;
			if (cell.isFlagged())
				color = Color.GREEN;
			else if (cell.isMarked()) {
				if (cell.getMark().first() && cell.getMark().second()) {
					drawBoth(g, x, y);
					return;
				} else if (cell.getMark().first())
					color = HIGHLIGHT_COLOR_1;
				else
					color = HIGHLIGHT_COLOR_2;
			}
			drawRectangle(g, x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, color);
		}
	}
	
	private void drawBoth(Graphics g, int x, int y) {
		x *= BLOCK_SIZE;
		y *= BLOCK_SIZE;
		int half = BLOCK_SIZE/2;
		drawRectangle(g, x, y, half, BLOCK_SIZE, Color.RED);
		drawRectangle(g, x+half, y, half, BLOCK_SIZE, Color.BLUE);
	}
	
	private void drawRectangle(Graphics g, int x, int y, int width, int height, Color color) {
		Color previous = g.getColor();
		g.setColor(color);
		g.fillRect(x, y, width, height);
		g.setColor(previous);
	}
	
	private void writeText(Graphics g, int i, int j, String text) {
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		int x =  i*BLOCK_SIZE + (BLOCK_SIZE - metrics.stringWidth(text))/2, 
			y =  j*BLOCK_SIZE + ((BLOCK_SIZE - metrics.getHeight()) / 2) + metrics.getAscent();
		writeStr(g, text, x, y);
	}

	private void writeStr(Graphics g, String s, int x, int y) {
		g.drawString(s, x, y);
	}

	private void drawSelected(Graphics g) {
		drawSquare(g, selected.x, selected.y, SELECTED_COLOR);
	}
	
	private void drawSquare(Graphics g, int i, int j, Color color) {
		g.setColor(color);
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(THICK));
        int x = i*BLOCK_SIZE, y = j*BLOCK_SIZE;
        g2.draw(new Line2D.Float(x, y, x+BLOCK_SIZE, y));
        g2.draw(new Line2D.Float(x, y, x, y+BLOCK_SIZE));
        g2.draw(new Line2D.Float(x+BLOCK_SIZE, y, x+BLOCK_SIZE, y+BLOCK_SIZE));
        g2.draw(new Line2D.Float(x, y+BLOCK_SIZE, x+BLOCK_SIZE, y+BLOCK_SIZE));
	}
	
	private void drawGrid(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(THICK));
        g2.setColor(Color.black);
        for (int i=1; i<=rows; i++)
        	g2.draw(new Line2D.Float(0, BLOCK_SIZE*i, SIZE.width, BLOCK_SIZE*i));
		for (int i=1; i<=cols; i++)
            g2.draw(new Line2D.Float(BLOCK_SIZE*i, 0, BLOCK_SIZE*i, SIZE.height));
	}
	
	// Click handling
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (ms.isLost())
			return;
		Point at = new Point(event.getPoint());
		selected = pointFromCoords(at.x, at.y);
		Cell cell = ms.get(selected);
		if (SwingUtilities.isRightMouseButton(event))
			cell.toggleFlag();
		else if (SwingUtilities.isLeftMouseButton(event)) {
			if (event.isControlDown())
				cell.toggleHypothesis(true);
			else if (event.isShiftDown())
				cell.toggleHypothesis(false);
			else if (event.getClickCount() == 2 && cell.isVisible()) 
				cell.spreadFrom(selected); 
			else if (!cell.isFlagged() && !cell.isMarked()) 
				ms.get(selected).reveal(); 
		}
		repaint();
	}
	
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseExited(MouseEvent arg0) {}
	@Override public void mousePressed(MouseEvent arg0) {}
	@Override public void mouseReleased(MouseEvent arg0) {}
	
	// Key handling

	private void setKeyBindings() {
		inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		actionMap = getActionMap();
		bind("SPACE", getKey(KeyEvent.VK_SPACE), new RevealAction());
		bind("CTRL_SPACE", getKey(KeyEvent.VK_SPACE, ActionEvent.CTRL_MASK), new FlagAction());
		bind("SHIFT_SPACE", getKey(KeyEvent.VK_SPACE, ActionEvent.SHIFT_MASK), new QuickRevealAction());
		bind("Z", getKey(KeyEvent.VK_Z), new Mark1Action());
		bind("X", getKey(KeyEvent.VK_X), new Mark2Action());
		// directional bindings
		int[] masks = {0, ActionEvent.CTRL_MASK, ActionEvent.SHIFT_MASK};	// make us move no matter what
		for (int mask : masks) {
			bind("LEFT_"+mask, getKey(KeyEvent.VK_LEFT, mask), new MoveAction(-1, 0));
			bind("RIGHT_"+mask, getKey(KeyEvent.VK_RIGHT, mask), new MoveAction(1, 0));
			bind("UP_"+mask, getKey(KeyEvent.VK_UP, mask), new MoveAction(0, -1));
			bind("DOWN_"+mask, getKey(KeyEvent.VK_DOWN, mask), new MoveAction(0, 1));
		}
		bind("HELP", getKey(KeyEvent.VK_H), new HelpAction());
		bind("QUIT", getKey(KeyEvent.VK_ESCAPE), new QuitAction());
	}
	
	public void bind(String name, KeyStroke ks, AbstractAction aa) {
		inputMap.put(ks, name);
		actionMap.put(name, aa);
	}
	
	public KeyStroke getKey(int keyCode) {
		return getKey(keyCode, 0);
	}
	
	public KeyStroke getKey(int keyCode, int modifiers) {
		return KeyStroke.getKeyStroke(keyCode, modifiers);
	}
	
	private abstract class MSAction extends AbstractAction {
		private static final long serialVersionUID = -7269093027030875306L;
		
		protected abstract void execute();
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			execute();
			repaint();
		}
	}
	
	private class MoveAction extends MSAction {
		private static final long serialVersionUID = 2038420213006370108L;
		private int dx,dy;
		
		private MoveAction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
		
		@Override
		public void execute() {
			selected.plusEquals(dx, dy, wrapAround);
		}
	}
	
	private class Mark1Action extends MSAction {
		private static final long serialVersionUID = -7881313598045892881L;

		@Override
		public void execute() {
			ms.get(selected).toggleHypothesis(true);
		}
	}
	
	private class Mark2Action extends MSAction {
		private static final long serialVersionUID = -5405925156376224325L;

		@Override
		public void execute() {
			ms.get(selected).toggleHypothesis(false);
		}
	}
	
	private class FlagAction extends MSAction {
		private static final long serialVersionUID = 9148108684408194959L;

		@Override
		protected void execute() {
			ms.get(selected).toggleFlag();
		}
	}
	
	private class RevealAction extends MSAction {
		private static final long serialVersionUID = 1973762085031674567L;

		@Override
		protected void execute() {
			ms.get(selected).reveal();
		}	
	}
	
	private class QuickRevealAction extends MSAction {
		private static final long serialVersionUID = -5601575813199020159L;

		@Override
		protected void execute() {
			Cell cell = ms.get(selected);
			if (cell.isVisible()) 
				cell.spreadFrom(selected); 
		}
	}
	
	private class HelpAction extends MSAction {
		private static final long serialVersionUID = -1837589342349519008L;
		private StringBuilder sb;
		
		public HelpAction() {
			sb = new StringBuilder();
			add("Space", "Reveals cell");
			add("Ctrl + Space", "Flags cell");
			add("Shift + Space", "Reveals surrounding cells");
			add("Ctrl + Z", "Mark type 1 cell");
			add("Ctrl + X", "Mark type 2 cell");
			add("Shift + Z", "New game");
			add("Shift + X", "Set mines");
			add("Shift + C", "Set size");
			add("Shift + V", "Set block size");
			add("H", "Show this help");
		}
		
		private void add(String key, String msg) {
			sb.append(String.format("%s: %s%n", key, msg));
		}
		
		@Override
		protected void execute() {
			JOptionPane.showMessageDialog(null, sb.toString());
		}
	}
	
	private class QuitAction extends MSAction {
		private static final long serialVersionUID = 635008946230939732L;

		@Override
		protected void execute() {
			System.exit(0);
		}
	}
}
