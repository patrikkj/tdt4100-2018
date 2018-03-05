package sokoban;

import java.util.Stack;

public class Game {
	//Instance vars
	private int numOfMoves;			//Number of moves
	private Cell[][] grid;			//Game board
	private int height, width;		//Grid dimensions
	private int playerX, playerY;	//Player coordinates
	
	
	private Stack<Direction> undoStack, redoStack;
	
	//Constructors
	//Constructs grid of default cells
	public Game(int width, int height) {
		//Retrive grid dimensions
		this.width = width;
		this.height = height;
		
		//Initialize empty grid
		grid = new Cell[height][width];
		
		//Fill grid with default cell
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				grid[y][x] = new Cell();
	}
	
	//Constructs grid from input table of characters (sokoban.Levels)
	public Game(char[][] charGrid) {
		//Retrive grid dimensions
		height = charGrid.length;
		width = charGrid[0].length;
		
		//Initialize empty grid
		grid = new Cell[height][width];
		
		//Fill grid with corresponding cell
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				
				
				switch (charGrid[y][x]) {
				case Cell.NONE:
					grid[y][x] = new Cell();
					break;
				case Cell.EMPTY:
					grid[y][x] = new Cell(x, y, false, false, false);
					break;
				case Cell.BLOCK_ENDPOINT:
					grid[y][x] = new Cell(x, y, false, true, true);
					break;
				case Cell.BLOCK:
					grid[y][x] = new Cell(x, y, false, true, false);
					break;
				case Cell.WALL:
					grid[y][x] = new Cell(x, y, true, false, false);
					break;
				case Cell.ENDPOINT:
					grid[y][x] = new Cell(x, y, false, false, true);
					break;
				case Cell.PLAYER:
					grid[y][x] = new Cell(x, y, false, false, false);
					playerX = x;
					playerY = y;
					break;
				case Cell.PLAYER_ENDPOINT:
					grid[y][x] = new Cell(x, y, false, false, true);
					playerX = x;
					playerY = y;
					break;
					
				//If no specifications, set noneCell
				default:
					grid[y][x] = new Cell();
				}
			}
	}
	
	
	//Validation
	public boolean isFinished() {
		//Iterate through grid and look for uncovered endpoint tiles
		for (Cell[] cellArray : grid)
			for (Cell cell : cellArray)
				if (cell.isEndpoint()  &&  !cell.isBlock())
					return false;
		
		return true;
	}
	
	private boolean isValidMove(Direction direction) {
		//Check indices
		int newX = playerX + direction.VALUE[0];
		int newY = playerY + direction.VALUE[1];
		if ((newX < 0)  ||  (newX >= width)) return false;
		if ((newY < 0)  ||  (newY >= height)) return false;
		
		//Check adjacent cell
		if (getAdjacent(direction).isEmpty())
			return true;
		
		//If cell contains block, check if block can be pushed
		if (getAdjacent(direction).isBlock())
			if (getAdjacent2(direction).isEmpty())
				return true;
			
		return false;
	}
	
	
	//Actions
	//Move block from initial cell to adjacent cell
	private void pushBlock(Direction direction) {
		getAdjacent(direction).setBlock(false);
		getAdjacent2(direction).setBlock(true);
	}
	
	//Attempts to make a move in given direction
	public boolean move(Direction direction) {
		//Used to mess around when finished, have fun :)
		if (isFinished()) {
			movePlayer(direction, false);
			return true;
		}

		//Break if move is invalid
		if (!isValidMove(direction)) 
			return false;
		
		//If adjacent cell is a block, push block and move player
		if (getAdjacent(direction).isBlock()) {
			pushBlock(direction);
			movePlayer(direction, true);
			return true;
		}
		
		//If adjacent cell is empty, move player
		else if (getAdjacent(direction).isEmpty()) {
			movePlayer(direction, true);
			return true;
		}
		
		return false;
	}
	
	//Moves player coordinates
	public void movePlayer(Direction direction, boolean incrementMoveCount) {
		playerX += direction.VALUE[0];
		playerY += direction.VALUE[1];
		
		if (incrementMoveCount) 
			numOfMoves++;
	}
	
	
	//Undo Actions
	//Redo action if possible
	public void redo() {
		
	}

	//Undo action if possible
	public void undo() {
		
	}
	
	
	//Getters
	//Returns cell at (x, y)
	public Cell get(int x, int y) {
		return grid[y][x];
	}
	
	//Returns cell at distance 1 in given direction relative to player coordinates
	public Cell getAdjacent(Direction direction) {
		return get(playerX + direction.VALUE[0], playerY + direction.VALUE[1]);
	}
	
	//Returns cell at distance 2 in given direction relative to player coordinates
	public Cell getAdjacent2(Direction direction) {
		return get(playerX + 2*direction.VALUE[0], playerY + 2*direction.VALUE[1]);
	}
	
	//Returns player coordinates
	public int[] getPlayerCoords() {
		return new int[] {playerX, playerY};
	}
	
	//Returns number of player moves
	public int getNumOfMoves() {
		return numOfMoves;
	}
	
	//Getters for board size
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	
	//Other
	//Prints grid as table
	public void printGrid() {
		System.out.println(toString());
	}
	
	//Returns string generated by asTable()
	@Override
	public String toString() {
		String outputStr = "";
		for (Cell[] cellArray : grid) {
			for (Cell cell : cellArray)
				outputStr += cell.toString() + " ";
			outputStr += "\n";
		}
		return outputStr.trim();
	}
	
	//Main
	public static void main(String[] args) {
		Game game1 = new Game(Levels.getLevel(1));
		
		game1.printGrid();
	}

	
	
}

enum Direction {
	UP 		(new int[] {0, -1}),
	DOWN 	(new int[] {0, 1}),
	LEFT 	(new int[] {-1, 0}),
	RIGHT 	(new int[] {1, 0});
	
	public final int[] VALUE;
	
	private Direction(int[] dirArr) {
		this.VALUE = dirArr;
	}
	
	// Return inversed direction
	public Direction getInverse() {
		switch (this) {
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		}
		
		// Never gonna happen bruh
		return null;
	}
}
