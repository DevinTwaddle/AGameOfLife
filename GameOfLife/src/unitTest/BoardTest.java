package unitTest;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Dimension;
import java.util.ArrayList;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;

import game.Cell;

class BoardTest {
	BoardTestObject board;

	@BeforeEach
	public void initialise() {
		board = new BoardTestObject();
	}

	// =================================================================
	// EXISTANCE TESTS
	// =================================================================

	@Test
	public void boardExists() {
		assertNotNull(board);
	}

	@Test
	public void boardCellGridExists() {
		assertNotNull(board.getCellGrid());
	}

	// =================================================================
	// CALCULATE NEIGHBOUR TESTS
	// =================================================================

	@Test
	public void calculateLivingNeighbours_NoLivingNegibours() {
		assertSame(0, board.calculateLivingNeighbours(1, 1));
	}

	@Test
	public void calculateLivingNeighbours_CellsSurroundingLivingCell() {
		// Set Cell at index (1,1) to Living.
		board.getCellGrid()[1][1].invertLivingState();

		// Check all Cells surrounding the cell at (1,1).
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				if (x != 1 && y != 1)
					assertSame(1, board.calculateLivingNeighbours(x, y));
	}

	@Test
	public void calculateLivingNeighbours_AllLivingNegihbours() {
		Cell[][] cellGrid = board.getCellGrid();
		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				cellGrid[x][y].invertLivingState();

		assertSame(8, board.calculateLivingNeighbours(1, 1));
	}

	@Test
	public void calculateLivingNeigbours_LivingCellNoLivingNeigbours() {
		board.getCellGrid()[1][1].invertLivingState();
		assertSame(0, board.calculateLivingNeighbours(1, 1));
	}
	
	// =================================================================
	// COPY TESTS
	// =================================================================
	
	@Test
	public void copyGrid_SizeTest() {
		Dimension dimension = board.getRowsColumns();
		Cell[][] emptyGrid = new Cell[dimension.width][dimension.height];
		emptyGrid = board.copyGrid(board.getCellGrid(), emptyGrid);
		assertSame(board.getCellGrid().length, emptyGrid.length);
	}
	
	@Test
	public void copyGrid_FirstElementTest() {
		Dimension dimension = board.getRowsColumns();
		Cell[][] emptyGrid = new Cell[dimension.width][dimension.height];
		emptyGrid = board.copyGrid(board.getCellGrid(), emptyGrid);
		assertEquals(board.getCellGrid()[0][0], emptyGrid[0][0]);
	}
	
	// =================================================================
	// TOTAL LIVING CELLS TESTS
	// =================================================================
	
	@Test
	public void calculateTotalLivingCells_NoneAlive() {
		assertSame(0, board.calculateLivingCells());
	}
	
	@Test
	public void calculateTotalLivingCells_AllAlive() {
		Cell[][] cellGrid = board.getCellGrid();
		int size = 0;
		
		for (Cell[] cells: cellGrid) {
			for (Cell cell: cells) {
				cell.invertLivingState();
				size++;
			}
		}
		assertEquals(size, board.calculateLivingCells());
	}
	
	// =================================================================
	// SEARCH TESTS
	// =================================================================
	
	@Test
	public void searchForCell_Correct() {
		ArrayList<Cell> cells = new ArrayList<>();
		cells.add(board.getCellGrid()[0][0]);
		
		Cell found = board.searchForCell(cells, 40, 40);
		assertEquals(board.getCellGrid()[0][0], found);
	}
	
	@Test
	public void searchForCell_Incorrect() {
		ArrayList<Cell> cells = new ArrayList<>();
		
		Cell found = board.searchForCell(cells, 40, 40);
		assertNotEquals(board.getCellGrid()[0][0], found);
	}
	
	// =================================================================
	// EVOLVE TEST
	// =================================================================
	
	@Test
	public void evolve_3x1Pattern() {
		Cell[][] grid = board.getCellGrid();
		grid[1][1].invertLivingState();
		grid[1][2].invertLivingState();
		grid[1][3].invertLivingState();
		
		board.processNextEvolution();
		grid = board.getCellGrid();
		
		assertTrue(grid[0][2].isLiving());
		assertTrue(grid[1][2].isLiving());
		assertTrue(grid[2][2].isLiving());
	}
}
