package game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GameOfLife extends JFrame {
	
	private final String TITLE;
	private final int WIDTH, HEIGHT;

	private Board mBoard;
	private JButton mEvolveBtn, mAutoEvolveBtn;
	private JLabel mIterationLbl, mLivingCellsLbl;
	private boolean mAutoLoop, mDragging, mStateOfSelectedCell;

	// =================================================================
	// CONSTRUCTORS / MAIN METHOD
	// =================================================================
	public GameOfLife(String title, int width, int height) {
		TITLE = title;
		WIDTH = width;
		HEIGHT = height;
	}
	
	public GameOfLife() {
		this("A Game Of Life", 1000, 1000);
	}

	// Main method used to create an instance of the game.
	public void createGame() {
		createFrame(TITLE, WIDTH, HEIGHT);
		instantiateComponents();
		initialiseComponents();
		createButtonListeners();
		createFrameListeners();
		mBoard.startGame();
		updateLabels();
	}

	// =================================================================
	// INSTANTIATE/INITIALISE VARIABLES
	// =================================================================

	// Initialise Variables with new instances.
	private void instantiateComponents() {
		int boardWidth = 900, boardHeight = 900;
		double boardOffset = 40, cellOffset = 1, cellResolution = 20;
		mEvolveBtn = new JButton();
		mAutoEvolveBtn = new JButton();
		mIterationLbl = new JLabel();
		mLivingCellsLbl = new JLabel();
		mBoard = new Board(boardWidth, boardHeight, boardOffset, cellOffset, cellResolution);
	}

	// Assigns specific values to the initialised components.
	private void initialiseComponents() {
		mEvolveBtn.setText("Evolve Once");
		mAutoEvolveBtn.setText("Auto Evolve");

		mEvolveBtn.setFocusable(false);
		mAutoEvolveBtn.setFocusable(false);

		mIterationLbl.setForeground(Color.ORANGE);
		mLivingCellsLbl.setForeground(Color.ORANGE);

		setContentPane(mBoard);
		add(mEvolveBtn);
		add(mAutoEvolveBtn);
		add(mIterationLbl);
		add(mLivingCellsLbl);
	}

	// Assigns desired values to this frame.
	private void createFrame(String title, int width, int height) {
		setTitle(title);
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(true);
		setLocationRelativeTo(null);
		setBackground(Color.black);
	}

	// =================================================================
	// CREATE LISTENERS
	// =================================================================

	private void createButtonListeners() {
		// When clicked evolve board once.
		mEvolveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				evolveGameBoard();
			}
		});

		// When selected begin auto evolving.
		mAutoEvolveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Invert selection.
				mAutoLoop = !mAutoLoop;
				// Update display to represent selection.
				if (mAutoLoop) {
					mAutoEvolveBtn.setBackground(Color.DARK_GRAY);
					mAutoEvolveBtn.setForeground(Color.WHITE);
				} else {
					mAutoEvolveBtn.setBackground(Color.WHITE);
					mAutoEvolveBtn.setForeground(Color.BLACK);
				}
			}
		});
	}

	private void createFrameListeners() {
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				return;
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				updateSelectedCell(e);

			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				updateSelectedCell(e);
				mDragging = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mDragging = false;
			}
		});

		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				GameCamera camera = mBoard.getCamera();
				double cameraX = camera.getX(), cameraY = camera.getY(),
						resolution = mBoard.getResolution();

				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					camera.setCameraPosition(cameraX, cameraY + resolution);
					break;

				case KeyEvent.VK_S:
					camera.setCameraPosition(cameraX, cameraY - resolution);
					break;

				case KeyEvent.VK_A:
					camera.setCameraPosition(cameraX + resolution, cameraY);
					break;

				case KeyEvent.VK_D:
					camera.setCameraPosition(cameraX - resolution, cameraY);
					break;

				}
				mBoard.drawGrid();
			}
		});
	}

	// Determines selected Cell, and inverts living status if appropriate.
	private void updateSelectedCell(MouseEvent e) {
		Cell cell = mBoard.searchForSelectedCell(e);
		if (cell == null)
			return;

		// Store status of first selected cell.
		if (!mDragging) 
			mStateOfSelectedCell = cell.isLiving();
		
		// If cell matches that of the initial cell, then also convert it.
		if (cell.isLiving() == mStateOfSelectedCell)
			cell.invertLivingState();

		// Add or remove cells from the array when brought to life, or die.
		if (cell.isLiving())
			mBoard.addCellToLivingArray(cell);
		else
			mBoard.removeCellFromLivingArray(cell);
	}

	// =================================================================
	// UPDATE
	// =================================================================

	private int i = 0;
	
	public void update() {
		// Update the frame.
		super.repaint();
		super.validate();

		// While autoLoop is active, wait for some time, before calling the evolve method.
		if (mAutoLoop && i > 1000 * 1000) {
			evolveGameBoard();
			i = 0;
		} else
			i++;

	}

	// Collective methods to remove repetition.
	private void evolveGameBoard() {
		mBoard.iterateEvolution();
		updateLabels();
	}

	private void updateLabels() {
		mIterationLbl.setText(" | Iteration: " + mBoard.getIteration() + " | ");
		mLivingCellsLbl.setText(" | No. Of living cells: " + mBoard.calculateTotalLivingCells() + " | ");
	}

}
