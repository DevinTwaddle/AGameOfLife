package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class GameOfLife extends JFrame {

	//TODO: Need to research the use and implementation of layouts, and clean up the interface currently present.
	
	private Board mBoard;
	private JButton mEvolveBtn, mAutoEvolveBtn, mCentreCamera;
	private JLabel mIterationLbl, mLivingCellsLbl, mCameraPosLbl, mSpeedLbl;
	private JSlider mSpeedSlider;
	private boolean mAutoLoop, mDragging, mStateOfSelectedCell;
	private final HashSet<Integer> pressedKeys = new HashSet<>();

	// =================================================================
	// CONSTRUCTORS / MAIN METHOD
	// =================================================================
	public GameOfLife(String title, int width, int height) {
		this.setTitle(title);
		this.setSize(width, height);
	}

	public GameOfLife() {
		this("A Game Of Life", 1000, 1000);
	}

	// Main method used to create an instance of the game.
	public void createGame() {
		createFrame(this.getTitle(), getWidth(), getHeight());
		instantiateComponents();
		initialiseComponents();
		addComponentsToFrame();
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
		int boardWidth = 900, boardHeight = 900, minSpeed = 0, maxSpeed = 50, defaultSpeed = 1;
		double boardOffset = 40, cellOffset = 1, cellResolution = 20;

		mEvolveBtn = new JButton();
		mAutoEvolveBtn = new JButton();
		mCentreCamera = new JButton();
		mIterationLbl = new JLabel();
		mLivingCellsLbl = new JLabel();
		mCameraPosLbl = new JLabel();
		mSpeedLbl = new JLabel();
		mSpeedSlider = new JSlider(JSlider.HORIZONTAL, minSpeed, maxSpeed, defaultSpeed);
		mBoard = new Board(boardWidth, boardHeight, boardOffset, cellOffset, cellResolution);
	}

	// Assigns specific values to the initialised components.
	private void initialiseComponents() {

		mEvolveBtn.setText("Evolve Once");
		mAutoEvolveBtn.setText("Auto Evolve");
		mCentreCamera.setText("Centre Camera");

		mEvolveBtn.setFocusable(false);
		mAutoEvolveBtn.setFocusable(false);
		mCentreCamera.setFocusable(false);
		mSpeedSlider.setFocusable(false);

		mIterationLbl.setForeground(Color.ORANGE);
		mLivingCellsLbl.setForeground(Color.ORANGE);
		mCameraPosLbl.setForeground(Color.ORANGE);
		mSpeedLbl.setForeground(Color.ORANGE);

		mSpeedSlider.setPaintTicks(true);
		mSpeedSlider.setPaintLabels(true);
		mSpeedSlider.setSnapToTicks(true);
		mSpeedSlider.setMinorTickSpacing(1);
		mSpeedSlider.setMajorTickSpacing(10);

	}

	private void addComponentsToFrame() {
		setContentPane(mBoard);
		add(mEvolveBtn);
		add(mAutoEvolveBtn);
		add(mCentreCamera);
		add(mIterationLbl);
		add(mLivingCellsLbl);
		add(mCameraPosLbl);
		add(mSpeedLbl);
		add(mSpeedSlider);
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
		setAlwaysOnTop(false);
	}

	// =================================================================
	// CREATE LISTENERS
	// =================================================================

	private void createButtonListeners() {
		// When clicked evolve board once.
		mEvolveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mBoard.processNextEvolution();
			}
		});

		// When selected begin auto evolving.
		mAutoEvolveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Invert selection.
				mAutoLoop = !mAutoLoop;

				// Update display to represent selection.
				if (mAutoLoop)
					setButtonActive(mAutoEvolveBtn);
				else
					setButtonInActive(mAutoEvolveBtn);
			}
		});

		mCentreCamera.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mBoard.returnCameraToOrigin();
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
				mBoard.createVisibleGrid();
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
				pressedKeys.remove(e.getKeyCode());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				pressedKeys.add(e.getKeyCode());
				processPressedKeys(pressedKeys);
			}
		});
	}
	
	// =================================================================
	// KEY INPUT
	// =================================================================

	//
	private void processPressedKeys(HashSet<Integer> keys) {
		int speed = mSpeedSlider.getValue();

		for (int key : keys) {
			Movement movement = determineSelectedKey(key);
			if (movement != null)
				mBoard.moveCamera(movement, speed);
		}
	}
	
	private Movement determineSelectedKey(int key) {
		Movement movement = null;
		switch (key) {
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			movement = Movement.UP;
			break;
		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			movement = Movement.DOWN;
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
		movement = Movement.RIGHT;
		break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			movement = Movement.LEFT;
			break;
		case KeyEvent.VK_SPACE:
			mBoard.returnCameraToOrigin();
			break;
		}
		return movement;
	}
	
	// =================================================================
	// CELL SELECTION
	// =================================================================

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
	// UPDATE BUTTON DISPLAY
	// =================================================================

	private void setButtonActive(JButton btn) {
		btn.setBackground(Color.DARK_GRAY);
		btn.setForeground(Color.WHITE);
	}

	private void setButtonInActive(JButton btn) {
		btn.setBackground(Color.WHITE);
		btn.setForeground(Color.BLACK);
	}

	// =================================================================
	// UPDATE
	// =================================================================
	int i = 0;

	public void update() {
		// Update the frame.
		super.repaint();
		super.validate();
		updateLabels();

		// While autoLoop is active, wait for some time, before calling the evolve
		// method.
		if (mAutoLoop && i >= 5) {
			mBoard.processNextEvolution();
			i = 0;
		}
		i++;
	}

	private void updateLabels() {
		mIterationLbl.setText(" | Iteration: " + mBoard.getIteration() + " | ");
		mLivingCellsLbl.setText(" | No. Of living cells: " + mBoard.getTotalLivingCells() + " | ");
		Dimension cameraPosition = mBoard.calculateCameraPosition();
		mCameraPosLbl
				.setText(" | Camera Position: X: " + cameraPosition.getWidth() + ", Y: " + cameraPosition.getHeight());
		mSpeedLbl.setText(" | Move by " + mSpeedSlider.getValue() + " Cell(s) | ");
	}
}
