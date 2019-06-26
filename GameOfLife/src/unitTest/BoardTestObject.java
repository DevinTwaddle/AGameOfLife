package unitTest;

import java.awt.Dimension;
import java.util.ArrayList;

import game.Board;
import game.Cell;

public class BoardTestObject extends Board{
	
	public BoardTestObject() {
		super(900, 900, 40, 1, 100);
		super.startGame();
	}
	
	@Override
	public int calculateLivingNeighbours(int x, int y) {
		return super.calculateLivingNeighbours(x, y);
	}
	
	public Dimension getRowsColumns() {
		Dimension rowColumns = new Dimension(mColumns, mRows);
		return rowColumns;
	}
	
	public Cell[][] getCellGrid() {
		return mCurrentCellGrid;
	}
	
	public Cell[][] copyGrid(Cell[][] currentGrid, Cell[][] newGrid){
		return super.copyToNextGrid(currentGrid, newGrid);
	}
	
	public int calculateLivingCells() {
		return super.calculateTotalLivingCells();
	}
	
	public Cell searchForCell(ArrayList<Cell> cells, double x, double y) {
		return super.searchForCell(cells, x, y);
	}

}
