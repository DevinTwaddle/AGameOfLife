package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Board extends JPanel {

	private final int WIDTH, HEIGHT;
	private final double CELL_RESOLUTION, CELL_OFFSET, BOARD_OFFSET;

	private ArrayList<Cell> mLivingCells;
	protected Cell[][] mCurrentCellGrid, mNextCellGrid;
	protected int mRows, mColumns;

	private int mCurrentIteration = 1;
	private GameCamera camera;

	// =================================================================
	// CONSTRUCTORS
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

	// =================================================================
	// INITIALISE VARIABLES
	// =================================================================

	public void startGame() {
		initialiseVariables();
		drawGrid();
	}

	private void initialiseVariables() {
		mRows = HEIGHT / (int) CELL_RESOLUTION;
		mColumns = WIDTH / (int) CELL_RESOLUTION;
		mCurrentCellGrid = new Cell[mRows][mColumns];
		mLivingCells = new ArrayList<>();
		camera = new GameCamera();
	}

	// =================================================================
	// CREATE GRID
	// =================================================================

	public void drawGrid() {
		// Draw desired Grid, however also create outer ring.
		for (int x = 0; x < mColumns; x++) {
			for (int y = 0; y < mRows; y++) {
				double size, xPos, yPos;
				size = CELL_RESOLUTION - CELL_OFFSET;
				xPos = (x * CELL_RESOLUTION) + camera.getX() + BOARD_OFFSET;
				yPos = (y * CELL_RESOLUTION) + camera.getY() + BOARD_OFFSET;

				Cell currentCell = searchForCell(mLivingCells, xPos, yPos);
				// If no tile exists, create one within the specified position.
				if (currentCell == null) {
					currentCell = new Cell(xPos - camera.getX(), yPos - camera.getY(), size);
				} else {
					currentCell.setRect(xPos, yPos, size, size);
				}

				if (x != -1 && x != mRows && y != -1 && y != mColumns)
					mCurrentCellGrid[x][y] = currentCell;
			}
		}
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
	// EVOLVE GRID
	// =================================================================

	protected Cell[][] copyToNextGrid(Cell[][] currentGrid, Cell[][] nextGrid) {
		nextGrid = new Cell[mRows][mColumns];
		
		for (int x = 0; x < mRows; x++) {
			for (int y = 0; y < mColumns; y++) {
				Cell currentCell = currentGrid[x][y];
				nextGrid[x][y] = new Cell(currentCell.getX(), currentCell.getY(), CELL_RESOLUTION - CELL_OFFSET);
			}
		}
		return nextGrid;
	}

	public void iterateEvolution() {
		mLivingCells.clear();
		mNextCellGrid = copyToNextGrid(mCurrentCellGrid, mNextCellGrid);

		for (int x = 0; x < mColumns; x++) {
			for (int y = 0; y < mRows; y++) {
				Cell cell = mCurrentCellGrid[x][y];
				int totalLivingNeighbours = calculateLivingNeighbours(x, y);
				
				

				if (!cell.isLiving() && totalLivingNeighbours == 3) {
					mNextCellGrid[x][y].setLivingState(true);
					mLivingCells.add(mNextCellGrid[x][y]);
				} else if (cell.isLiving() && (totalLivingNeighbours < 2 || totalLivingNeighbours > 3)) {
					mNextCellGrid[x][y].setLivingState(false);
				} else {
					mNextCellGrid[x][y].setLivingState(cell.isLiving());
					if (cell.isLiving() && !mLivingCells.contains(cell))
						mLivingCells.add(mNextCellGrid[x][y]);
				}
			}
		}
		mCurrentCellGrid = mNextCellGrid;
		mCurrentIteration++;
		repaint();
	}

	// =================================================================
	// SEARCH METHODS
	// =================================================================

	protected Cell searchForCell(ArrayList<Cell> cells, double x, double y) {
		for (Cell cell : cells)
			if (cell.getX() == x && cell.getY() == y)
				return cell;
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
	// CALCULATE METHODS
	// =================================================================

	protected int calculateTotalLivingCells() {
		int total = 0;
		for (Cell[] cells : mCurrentCellGrid)
			for (Cell cell : cells)
				if (cell.isLiving())
					total++;
		return total;
	}

	// =================================================================
	// PAINT METHOD
	// =================================================================

	public void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;

		if (mCurrentCellGrid == null)
			return;

		for (Cell[] cells : mCurrentCellGrid) {
			for (Cell cell : cells) {
				graphics.setColor(cell.isLiving() ? Color.RED : Color.BLUE);
				graphics.fill(cell);
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

	public GameCamera getCamera() {
		return camera;
	}

	// =================================================================
	// ADD/REMOVE FROM ARRAYLIST
	// =================================================================

	public void addCellToLivingArray(Cell cell) {
		mLivingCells.add(cell);
	}

	public void removeCellFromLivingArray(Cell cell) {
		mLivingCells.remove(cell);
	}
}
