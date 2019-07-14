package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.print.attribute.ResolutionSyntax;
import javax.swing.JPanel;

public class Board extends JPanel {

	private final int WIDTH, HEIGHT;
	private final double CELL_RESOLUTION, CELL_OFFSET, BOARD_OFFSET;

	private ArrayList<Cell> mLivingCells, mNewLivingCells, mCellsToBeProcessed;
	protected Cell[][] mCurrentCellGrid, mNextCellGrid;
	protected int mRows, mColumns;
	private GameCamera mCamera;
	private int mCurrentIteration = 1;

	// =================================================================
	// CONSTRUCTORS / START GAME
	// =================================================================

	public Board(int width, int height, double boardOffset, double cellOffset, double cellResolution) {
		HEIGHT = width;
		WIDTH = height;
		BOARD_OFFSET = boardOffset;
		CELL_OFFSET = cellOffset;
		CELL_RESOLUTION = cellResolution;
	}

	public Board() {
		this(900, 900, 40, 1, 100);
	}

	public void startGame() {
		initialiseVariables();
		createVisibleGrid();
	}

	// =================================================================
	// INITIALISE VARIABLES
	// =================================================================

	private void initialiseVariables() {
		mRows = HEIGHT / (int) CELL_RESOLUTION;
		mColumns = WIDTH / (int) CELL_RESOLUTION;
		mCurrentCellGrid = new Cell[mRows][mColumns];
		mLivingCells = new ArrayList<>();
		mNewLivingCells = new ArrayList<>();
		mCellsToBeProcessed = new ArrayList<>();
		mCamera = new GameCamera((int) BOARD_OFFSET, (int) BOARD_OFFSET);
	}

	// =================================================================
	// CREATE GRID
	// =================================================================

	// ----------------------
	// CELL VISIBILITY
	// ----------------------

	// Determines whether the cell is currently present within the visible grid, and
	// sets the active status to reflect the desired outcome.
	public boolean checkVisibility(Cell cell) {
		if (cell.getX() < BOARD_OFFSET || cell.getX() > (WIDTH + BOARD_OFFSET) - CELL_RESOLUTION
				|| cell.getY() < BOARD_OFFSET || cell.getY() > (HEIGHT + BOARD_OFFSET) - CELL_RESOLUTION)
			return false;
		return true;
	}

	// Simple method to cut down on code duplication.
	private void updateCellVisibility(Cell cell) {
		cell.setVisible(checkVisibility(cell));
	}

	public void createVisibleGrid() {
		// Creates blank grid.
		for (int y = 0; y < mRows; y++)
			for (int x = 0; x < mColumns; x++)
				mCurrentCellGrid[x][y] = new Cell(x * CELL_RESOLUTION + BOARD_OFFSET,
						y * CELL_RESOLUTION + BOARD_OFFSET, CELL_RESOLUTION - CELL_OFFSET);

		// Draw living cells if in view.
		for (Cell cell : mLivingCells) {
			cell.setVisible(checkVisibility(cell));
			if (cell.isVisible())
				mCurrentCellGrid[(int) ((cell.getX() - BOARD_OFFSET)
						/ CELL_RESOLUTION)][(int) ((cell.getY() - BOARD_OFFSET) / CELL_RESOLUTION)] = cell;
		}
	}

	// =================================================================
	// GRID NAVIGATION
	// =================================================================

	// Main movement method, interfacing between different directions.
	public void moveCamera(Movement movement, int speed) {
		if (movement == Movement.LEFT)
			moveCameraAlongXAxis(CELL_RESOLUTION * speed);
		else if (movement == Movement.RIGHT)
			moveCameraAlongXAxis(-CELL_RESOLUTION * speed);
		else if (movement == Movement.UP)
			moveCameraAlongYAxis(CELL_RESOLUTION * speed);
		else if (movement == Movement.DOWN)
			moveCameraAlongYAxis(-CELL_RESOLUTION * speed);

		// After movement, redraw grid.
		createVisibleGrid();
	}

	private void moveCameraAlongXAxis(double distance) {
		mCamera.moveCameraX(distance);
		for (Cell cell : mLivingCells) {
			cell.setX(cell.getX() + distance);
			updateCellVisibility(cell);
		}
	}

	private void moveCameraAlongYAxis(double distance) {
		mCamera.moveCameraY(distance);
		for (Cell cell : mLivingCells) {
			cell.setY(cell.getY() + distance);
			updateCellVisibility(cell);
		}
	}

	// =================================================================
	// CAMERA MANIPULATION
	// =================================================================

	// Calculates currentIndex position of the camera, and stores it within a
	// Dimension, as it is a single object with two accessible variables.
	public Dimension calculateCameraPosition() {
		Dimension cameraPosition = new Dimension();
		cameraPosition.setSize((mCamera.getX() - BOARD_OFFSET) / CELL_RESOLUTION,
				(mCamera.getY() - BOARD_OFFSET) / CELL_RESOLUTION);
		return cameraPosition;
	}

	// The idea of this method is to essentially undo the movements made by the
	// users. If previously moved 5 times left. Then move 5 times right.
	public void returnCameraToOrigin() {
		if (mCamera.getX() == 0 && mCamera.getY() == 0)
			return;

		Movement directionX = Movement.LEFT, directionY = Movement.DOWN;

		// Camera to left.
		if (mCamera.getX() < BOARD_OFFSET)
			directionX = Movement.RIGHT;
		// Camera is down.
		if (mCamera.getY() < BOARD_OFFSET)
			directionY = Movement.UP;

		Dimension cameraPos = calculateCameraPosition();
		if (mCamera.getX() != 0)
			for (int i = 0; i < Math.abs(cameraPos.getWidth()); i++)
				moveCamera(directionX, -1);
		if (mCamera.getY() != 0)
			for (int i = 0; i < Math.abs(cameraPos.getHeight()); i++)
				moveCamera(directionY, 1);
	}

	// =================================================================
	// EVOLVE GRID
	// =================================================================

	// Re-populates the "livingCells" array with cells which need to be processed.
	// This includes all living cells, and their neighbouring cells.
	public void updateProcessingArray() {
		mCellsToBeProcessed.clear();
		ArrayList<Cell> tempLivingCells = new ArrayList<>(mLivingCells); // Create and reference a duplicate of
																			// mLivingCells, as to prevent a concurrent
																			// modification error.

		// For every living cell, add it and its surrounding neighbours.
		for (Cell cell : tempLivingCells)
			addNeighbouringCells(tempLivingCells, cell);
	}

	private void addNeighbouringCells(ArrayList<Cell> livingCellList, Cell cell) {
		double cellPosX = cell.getX(), cellPosY = cell.getY();
		for (double y = cellPosY - CELL_RESOLUTION; y <= cellPosY + CELL_RESOLUTION; y += CELL_RESOLUTION) {
			for (double x = cellPosX - CELL_RESOLUTION; x <= cellPosX + CELL_RESOLUTION; x += CELL_RESOLUTION) {
				Cell tempCell = searchForCell(livingCellList, x, y);
				if (tempCell == null)
					tempCell = new Cell(x, y, CELL_RESOLUTION - CELL_OFFSET);

				if (!mCellsToBeProcessed.contains(tempCell)) {
					mCellsToBeProcessed.add(tempCell);
				}
			}
		}
	}

	public void processNextEvolution() {
		updateProcessingArray();
		mNewLivingCells = new ArrayList<>();

		for (Cell cell : mCellsToBeProcessed) {
			int totalLivingNeighbours = calculateLivingNeighbours(cell);
			if (cell.isLiving() && (totalLivingNeighbours < 2 || totalLivingNeighbours > 3)) {
				mNewLivingCells.remove(cell);
			} else if (!cell.isLiving() && totalLivingNeighbours == 3) {
				cell.setLivingState(true);
				mNewLivingCells.add(cell);
			} else if (cell.isLiving()) {
				mNewLivingCells.add(cell);
			}
		}
		mLivingCells = mNewLivingCells;
		createVisibleGrid();
	}

	private int calculateLivingNeighbours(Cell cell) {
		int total = 0;

		double posX = cell.getX(), posY = cell.getY();

		for (double x = posX - CELL_RESOLUTION; x <= posX + CELL_RESOLUTION; x += CELL_RESOLUTION) {
			for (double y = posY - CELL_RESOLUTION; y <= posY + CELL_RESOLUTION; y += CELL_RESOLUTION) {
				for (Cell tempCell : mLivingCells) {
					if (tempCell == cell || tempCell == null)
						continue;

					if (tempCell.getX() == x && tempCell.getY() == y) {
						total++;
					}
				}
			}
		}
		return total;
	}

	protected int calculateLivingNeighbours(int x, int y) {
		int totalLivingNeighbours = 0;
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {

				if (i == x && j == y)
					continue;

				if (mCurrentCellGrid[(i + mRows) % mRows][(j + mColumns) % mColumns].isLiving())
					totalLivingNeighbours++;
			}
		}
		return totalLivingNeighbours;
	}

	// =================================================================
	// SEARCH METHODS
	// =================================================================

	protected Cell searchForCell(ArrayList<Cell> cells, double x, double y) {
		for (Cell cell : cells) {
			Cell temp = new Cell(x, y, (CELL_RESOLUTION - CELL_OFFSET));
			if (temp.equals(cell))
				return cell;
		}
		return null;
	}

	public Cell searchForSelectedCell(MouseEvent e) {
		int offsetX = 10, offsetY = 40;
		for (Cell[] cells : mCurrentCellGrid)
			for (Cell cell : cells)
				if (cell.contains(e.getX() - offsetX, e.getY() - offsetY))
					return cell;
		return null;
	}

	// =================================================================
	// DRAW
	// =================================================================

	public void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;

		if (mCurrentCellGrid == null)
			return;
		drawGrid(graphics);
	}

	private void drawGrid(Graphics2D graphics) {
		for (Cell[] cells : mCurrentCellGrid) {
			for (Cell cell : cells) {
				if (cell == null)
					continue;
				if (cell.isVisible()) {
					graphics.setColor(cell.isLiving() ? Color.RED : Color.BLUE);
					graphics.fill(cell);
				}
			}
		}
	}

	// =================================================================
	// GETTERS
	// =================================================================

	public int getIteration() {
		return mCurrentIteration;
	}

	public double getResolution() {
		return CELL_RESOLUTION;
	}

	protected int getTotalLivingCells() {
		return mLivingCells.size();
	}

	// =================================================================
	// ADD/REMOVE FROM ARRAYLIST
	// =================================================================
	// Public methods which allow interactions between the main game class, and the
	// board.

	public void addCellToLivingArray(Cell cell) {
		if (!mLivingCells.contains(cell))
			mLivingCells.add(cell);
	}

	public void removeCellFromLivingArray(Cell cell) {
		if (mLivingCells.contains(cell))
			mLivingCells.remove(cell);
	}
}
