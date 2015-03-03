package com.code2play.grid.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.code2play.grid.GameMain;
import com.code2play.grid.game.Swipe;
import com.code2play.grid.game.GridBox.Color;

/**
 * Initialization and storing grid information
 * Grid class contains data about each grid, as well as 
 * updating all instances of each unit of grid box.
 * to be used by the graphics classes
 * @author BoatNad
 *
 */
public class Grid {

	/* State of the game */
	private GameMain game;
	
	/* Current level */
	private int level;

	/** Initial game state (from level load in CHALLENGE MODE) **/
	private List<GridBox> defaultGrid;

	/** Initial numMovesLeft **/
	private int defaultMovesLeft;

	/** Initial minimum moveleft to have to receive Gold reward */
	private int defaultMinGoldMovesLeft;

	/** Initial minimum moveleft to have to receive Silver reward */
	private int defaultMinSilverMovesLeft;

	/** Iniitial maxLevelTime **/
	private float defaultLevelTime;

	/** Initial swaps left **/
	private int defaultSwapsLeft;
	
	/** Initial number of spawned colored boxes **/
	private int defaultNumSpawned;

	/* Number of maximum moves allowed before gameover */
	private int numMovesLeft;			

	/** Minimum moveleft to have to receive Gold reward */
	private int minGoldMovesLeft;

	/** Minimum moveleft to have to receive Silver reward */
	private int minSilverMovesLeft;

	/* Time in millisec in this level before gameover. 
	 * Set as negative for infinite */
	private float maxLevelTime;
	
	/* Starting number of swaps in this level */
	private int numSwapsLeft;
	
	/* Number of undo moves left (max is 9) */
	private int undoCount;
	
	/* all the gridboxes in this grid */
	private List<GridBox> grid;
	private int width;
	private int height;

	private Random random;
	private int numBoxSpawned;

	private Array<Group> colorGroups;
	private static final int MIN_CHAIN_SIZE = 3;
	private int numMinChainGroup;
	
	

	/******************************************************************************************/
	/************************************ INIT METHODS **************************************/

	/**
	 * Constructs an initially empty grid of dimension specified 
	 * by the width and height
	 * @param width Number of gridboxes wide
	 * @param height Number of gridboxes high
	 */
	public Grid(GameMain g, int width, int height) {
		this(g);
		System.out.println("Initializing grid with dimension " + 
				width + "x" + height);
		int totalNumBox = width*height;
		this.width = width;
		this.height = height;
		int currId = 1;
		grid = new ArrayList<GridBox>(totalNumBox);

		for (int i = 0; i < totalNumBox; i++) {
			currId = i+1;
			GridBox box = new GridBox(currId);
			grid.add(box);
		}
		colorGroups = new Array<Group>();
	}

	private Grid(GameMain g) {
		game = g;
		random = new Random();
	}

	/**
	 * Loads a level file with information about grid size,
	 * spawn colored tiles, starting time, starting swaps, and
	 * starting max moves allowed.
	 * This method is used to construct grid object in Challenge mode
	 * @param file Path in the assets to the level file
	 */
	public static Grid load(GameMain game, FileHandle file) {
		Grid g = new Grid(game);			// set to SINGLETON instance
		g.numMovesLeft = 0;
		g.maxLevelTime = 0f;
		g.numSwapsLeft = 0;
		// load undo move stored for this user TODO
		g.undoCount = 9;

		// read the file and split the string into extractable content
		// initialize grid properties
		String content = file.readString();
		String[] lines = content.split("\n");
		//		String[] gridInfo = new String[]{};
		int lineNum = 0;
		int totalNumBox = 0;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("//"))
				continue;
			else {
				String[] tokens = lines[i].split(",");

				// first line is grid details
				if (lineNum == 0) {
					g.width = Integer.parseInt(tokens[0]);
					g.height = Integer.parseInt(tokens[1]);
					g.numMovesLeft = Integer.parseInt(tokens[2]);
					g.minGoldMovesLeft = Integer.parseInt(tokens[3]);
					g.minSilverMovesLeft = Integer.parseInt(tokens[4]);
					g.maxLevelTime = Float.parseFloat(tokens[5]);
					g.numSwapsLeft = Integer.parseInt(tokens[6]);

					// initialize grid data structure
					// with dimensions
					System.out.println("Initializing grid with dimension " + 
							g.width + "x" + g.height);
					totalNumBox = g.width*g.height;
					int currId = 1;
					g.grid = new ArrayList<GridBox>(totalNumBox);

					for (int j = 0; j < totalNumBox; j++) {
						currId = j+1;
						GridBox box = new GridBox(currId);
						g.grid.add(box);
					}
					g.colorGroups = new Array<Group>();

					lineNum++;
				}

				// second line is grid tile info
				else if (lineNum == 1) {
					for (int j = 0; j < totalNumBox; j++) {
						Color color = Color.NONE;
						if (tokens[j].equals("x") || tokens[j].equals("^"))
							continue;
						else if (tokens[j].equals("b"))
							color = Color.BLUE;
						else if (tokens[j].equals("g"))
							color = Color.GREEN;
						else if (tokens[j].equals("r"))
							color = Color.RED;
						else if (tokens[j].equals("y"))
							color = Color.YELLOW;
						else if (tokens[j].equals("?"))
							color = g.getRandomColor();

						g.spawnGridBoxAt(j+1, color);
					} 
				}
			}
		}

		// spawn colored tiles from file
		//		g.spawnGridBoxAt(1, Color.GREEN);
		//		for (int i = 0; i < 12; i++) 
		//			g.spawnRandomGridBox();

		// clone all the grid objects to save this default state
		g.defaultGrid = new ArrayList<GridBox>(g.width*g.height);
		for (GridBox gridBox : g.grid) {
			GridBox temp = new GridBox(gridBox.getId(), gridBox.getColor());
			temp.setPrevId(gridBox.getPrevId());
			g.defaultGrid.add(temp);
		}
		g.defaultMovesLeft = g.numMovesLeft;
		g.defaultMinGoldMovesLeft = g.minGoldMovesLeft;
		g.defaultMinSilverMovesLeft = g.minSilverMovesLeft;
		g.defaultLevelTime = g.maxLevelTime;
		g.defaultSwapsLeft = g.numSwapsLeft;
		g.defaultNumSpawned = g.numBoxSpawned;
		g.level = Integer.parseInt( file.name().substring(0, file.name().indexOf('.')) );
		return g;
	}
	
	/**
	 * Restores the default state of this level
	 * This method is to be called when the player reloads the state of the default level
	 * in the CHALLENGE mode
	 * TODO load from cache
	 * @return
	 */
	public boolean restoreDefaultState() {
		grid.clear();
		for (GridBox gridBox : defaultGrid) {
			GridBox temp = new GridBox(gridBox.getId(), gridBox.getColor());
			temp.setPrevId(gridBox.getPrevId());
			grid.add(temp);
		}
		numMovesLeft = defaultMovesLeft;
		minGoldMovesLeft = defaultMinGoldMovesLeft;
		minSilverMovesLeft = defaultMinSilverMovesLeft;
		maxLevelTime = defaultLevelTime;
		numSwapsLeft = defaultSwapsLeft;
		numBoxSpawned = defaultNumSpawned;

		return true;
	}
	
	public void undoMove() {
		if (undoCount >= 1) undoCount--;
	}
	
	public void addUndoCount() {
		if (undoCount < 9) undoCount++;
	}

	/******************************************************************************************/
	/************************************ GETTER METHODS **************************************/
	
	public int getMinGoldMoves() {
		return minGoldMovesLeft;
	}
	
	public int getMinSilverMoves() {
		return minSilverMovesLeft;
	}
	
	public int getLevel() {
		return level;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public List<GridBox> getGrid() {
		return grid;
	}

	public int getSize() {
		return grid.size();
	}

	/**
	 * Returns the number of possible moves (eg. swipe, swap) allowed left
	 * in this level
	 * @return
	 */
	public int getMovesLeft() {
		return numMovesLeft;
	}

	public float getTimeLeft() {
		return maxLevelTime;
	}

	public int getNumSwapsLeft() {
		return numSwapsLeft;
	}
	
	public int getUndoCount() {
		return undoCount;
	}


	/**
	 * Returns the number of same-color gridbox groups with number of members 
	 * greater than or equal to MIN_CHAIN_SIZE found 
	 * @return
	 */
	public int getNumColorGroups() {
		return numMinChainGroup;
	}

	/**
	 * Returns next random integer in range specified
	 * @param min inclusive
	 * @param max exclusive
	 * @return
	 */
	private int getRandomInt(int min, int max) {
		return random.nextInt(max-min) + min;
	}

	/**
	 * Returns non-diagonal neighboring gridboxes
	 * @param g
	 * @return
	 */
	private Array<GridBox> getNeighBors(GridBox g) {
		Array<GridBox> neighbors = new Array<GridBox>();
		int index = g.getId()-1;

		// check boundary conditions
		// add left neighbor
		// except left columns
		if (index % width != 0) 
			neighbors.add(grid.get( index-1 ));

		// add top neighbor
		// except top row
		if (index >= width)
			neighbors.add(grid.get( index-width ));

		// add right neighbor
		// except right column
		if (index % width != (width-1))
			neighbors.add(grid.get( index+1 ));

		// add bottom neighbor
		// except bottom row
		if (index < (height-1)*width)
			neighbors.add(grid.get( index+width ));

		return neighbors;
	}

	/******************************************************************************************/
	/************************************ UPDATE METHODS **************************************/

	boolean firstMove = true;
	boolean done = false;
	/**
	 * Updates all instances of gridboxes and logic in the grid form swipe direction
	 * @param deltaTime
	 * @return True if any change in the grid occurs
	 */
	public boolean update(float deltaTime, Swipe direction) {
		// clear from last rendered frame
		// TODO SWIPE TO BEGIN LEVEL
		//		if (!firstMove) move(direction);
		//		firstMove = false;
		boolean hasMoved = move(direction);

		// update all group of color matches
		updateColorMatchCounts();
		numMinChainGroup = 0;

		for (Group g : colorGroups) {
			if (g.size >= MIN_CHAIN_SIZE)
				numMinChainGroup++;
		}

		// spawn new gridbox
		if (numMinChainGroup == 0 
				&& game.getGameMode() == GameMode.CLASSIC)	
			spawnRandomGridBox();
		//				if (!done) {
		//					spawnGridBoxAt(13, Color.RED);
		//					done = true;
		//				}
		return hasMoved;
	}

	/**
	 * Updates gridboxes that are swapped
	 * @param deltaTime
	 * @param firstSwapID
	 * @param secondSwapID
	 */
	public void update(float deltaTime, int firstSwapID, int secondSwapID) {
		swap(firstSwapID, secondSwapID);

		// spawn new gridbox
		if (game.getGameMode() == GameMode.CLASSIC)	
			spawnRandomGridBox();
	}

	/**
	 * Swaps two specified IDs in the grid
	 * @param firstSwapID
	 * @param secondSwapID
	 */
	private void swap(int firstSwapID, int secondSwapID) {
		GridBox first = grid.get(firstSwapID-1);
		GridBox second = grid.get(secondSwapID-1);

		if (first.isEmpty() || second.isEmpty()) 
			return;
		else {
			// move first box to second
			Color secondColor = second.getColor();
			second.setColor(first.getColor());
			second.setPrevId(first.getId());
			first.setColor(secondColor);
			first.setPrevId(second.getId());
		}
		numSwapsLeft--;
	}

	/**
	 * Call this method to decrement the move counts from the maximum
	 * possible moves allowed in a level
	 */
	public void updateMoveCount() {
		if (numMovesLeft > 0) numMovesLeft--;
	}

	/**
	 * Spawns a random color on a random grid box that is empty
	 */
	public GridBox spawnRandomGridBox() {
		if (numBoxSpawned >= grid.size()) return null;

		int id = getRandomInt(1, grid.size()+1);
		GridBox.Color color = getRandomColor();

		// if id is taken, re-randomize id
		while (!grid.get(id-1).isEmpty()) 
			id = getRandomInt(1, grid.size()+1);

		GridBox spawn = spawnGridBoxAt(id, color);
		return spawn;
	}

	public int getNumBoxSpawned() {
		return numBoxSpawned;
	}

	public GridBox.Color getRandomColor() {
		GridBox.Color[] colors = GridBox.Color.values();
		int colorLimit = 1;
		if (width == 4 && height == 4) colorLimit = 2;
		return colors[getRandomInt(1, colors.length-colorLimit)];
	}

	/**
	 * Updates the gridbox with new color at a specified position.
	 * @param id
	 * @param color
	 * @return
	 */
	public GridBox spawnGridBoxAt(int id, Color color) {
		if (id < 1 || id > grid.size()) return null;

		GridBox box = grid.get(id-1);
		box.setColor(color);
		box.setPrevId(-1);
		System.out.println("Spawned " + (id-1) + " color " + color + " ");
		numBoxSpawned++;
		return box;
	}

	/**
	 * Moves all the spawned boxes in a direction specified
	 * TODO check all gridboxes whose group has size >= MIN_CHAIN_SIZE 
	 * set their color to NONE (empty)
	 * @param direction
	 */
	public boolean move(Swipe direction) {

		switch(direction) {
		case DOWN:
			return moveDown();
		case LEFT:
			return moveLeft();
		case RIGHT:
			return moveRight();
		case UP:
			return moveUp();
		default:
			return false;
		}
	}

	/**
	 * Move all boxes leftward
	 */
	private boolean moveLeft() {
		// iterate through all colored boxes, starting at 
		// the second top row
		//		int secondTopLeftIndex = 1;
		boolean hasMoved = false;
		int secondTopLeftIndex = 0;
		GridBox toMove = null;
		GridBox destination = null;
		for (int i = secondTopLeftIndex; i < width*height; i++) {
			toMove = grid.get(i);

			if (toMove.isEmpty() 
					//					|| i%width == 0
					) continue;
			if (toMove.getGroup() != null) {
				if (toMove.getGroup().size >= MIN_CHAIN_SIZE) {
					toMove.setColor(Color.REMOVED);
					toMove.setPrevId(-3);
					numBoxSpawned--;
					hasMoved = true;
					continue;
				}
			}

			// find the leftmost non-colored box to move to
			for (int j = 0;  j < width; j++) {
				// find the index that is in the same row
				int index = i - (i%width) + j;
				if (index == i) break;
				else {
					destination = grid.get(index);
					if (destination.isEmpty() 
							|| destination.getColor() == Color.REMOVED
							) {
						destination.setPrevId( toMove.getId() );
						destination.setColor( toMove.getColor() );
						toMove.clearColor();
						hasMoved = true;
						break;
					}
				}
			}
		}
		return hasMoved;
	}

	/**
	 * Move all boxes rightward
	 */
	private boolean moveRight() {
		// iterate through all colored boxes, starting at 
		// the second rightmost bottom row
		//		int secondRightBottomIndex = width*height - 2;
		boolean hasMoved = false;
		int secondRightBottomIndex = width*height - 1;
		GridBox toMove = null;
		GridBox destination = null;
		for (int i = secondRightBottomIndex; i >= 0; i--) {
			toMove = grid.get(i);

			// also ignore rightmost column
			if (toMove.isEmpty()
					//					|| i%width == width-1
					) continue;
			if (toMove.getGroup() != null) {
				if (toMove.getGroup().size >= MIN_CHAIN_SIZE) {
					toMove.setColor(Color.REMOVED);
					toMove.setPrevId(-3);
					numBoxSpawned--;
					hasMoved = true;
					continue;
				}
			}


			// find the rightmost non-colored box to move to
			for (int j = 0;  j < width; j++) {
				// find the index that is in the same row
				int index = (width - ((i%width) + 1)) - j + i;
				if (index == i) break;
				else {
					destination = grid.get(index);
					if (destination.isEmpty()
							|| destination.getColor() == Color.REMOVED
							) {
						hasMoved = true;
						destination.setPrevId( toMove.getId() );
						destination.setColor( toMove.getColor() );
						toMove.clearColor();
						break;
					}
				}

			}
		}
		return hasMoved;
	}

	/**
	 * Move all boxes downward
	 */
	private boolean moveDown() {
		// iterate through all colored boxes, starting at 
		// the second most bottom row
		//		int secondLastRowIndex = width*(height-1) - 1;
		boolean hasMoved = false;
		int secondLastRowIndex = width*height - 1;
		GridBox toMove = null;
		GridBox destination = null;
		for (int i = secondLastRowIndex; i >= 0; i--) {
			toMove = grid.get(i);
			if (toMove.isEmpty()) continue;

			if (toMove.getGroup() != null) {
				if (toMove.getGroup().size >= MIN_CHAIN_SIZE) {
					toMove.setColor(Color.REMOVED);
					toMove.setPrevId(-3);
					numBoxSpawned--;
					hasMoved = true;
					continue;
				}
			}

			// find the most bottom non-colored box to move to
			for (int j = 1; j <= height; j++) {
				// find the index that is in the same column
				int index = width*(height-j) + (i%width);	
				if (index < i) break;
				else {
					destination = grid.get(index);
					if (destination.isEmpty()
							|| destination.getColor() == Color.REMOVED
							) {
						destination.setPrevId( toMove.getId() );
						destination.setColor( toMove.getColor() );
						toMove.clearColor();
						hasMoved = true;
						break;
					}
				}
			}

		}
		return hasMoved;
	}

	/**
	 * Move all boxes upward
	 */
	private boolean moveUp() {
		// iterate through all colored boxes, starting at
		// the second most top row
		//		int secondTopRowIndex = width;
		boolean hasMoved = false;
		int secondTopRowIndex = 0;
		GridBox toMove = null;
		GridBox destination = null;
		for (int i = secondTopRowIndex; i < width*height; i++) {
			toMove = grid.get(i);
			if (toMove.isEmpty()) continue;

			if (toMove.getGroup() != null) {
				if (toMove.getGroup().size >= MIN_CHAIN_SIZE) {
					toMove.setColor(Color.REMOVED);
					toMove.setPrevId(-3);
					numBoxSpawned--;
					hasMoved = true;
					continue;
				}
			}

			// find the topmost non-colored box to move to
			for (int j = height; j >= 1; j--) {
				// find the index that is in the same column
				int index = width*(height-j) + (i%width);
				if (index > i) break;
				else {
					destination = grid.get(index);
					if (destination.isEmpty() 
							|| destination.getColor() == Color.REMOVED
							) {
						destination.setPrevId( toMove.getId() );
						destination.setColor( toMove.getColor() );
						toMove.clearColor();
						hasMoved = true;
						break;
					}
				}
			}

		}
		return hasMoved;
	}

	/**
	 * Checks and updates all neighboring gridboxes if there are color matches
	 * This is called after all gridboxes are moved.
	 */
	private Array<Group> updateColorMatchCounts() {
		colorGroups.clear();
		Array<GridBox> neighbors = new Array<GridBox>();

		// clear all thr groups made from previous update
		for (GridBox g : grid) {
			g.setGroup(null);
		} 

		// iterate through all gridboxes to find non-diagonal match-colored neighbors 
		// and update their groups
		for (int i = 0; i < grid.size(); i++) {
			GridBox g = grid.get(i);
			if (!g.isEmpty() && g.getColor() != Color.REMOVED) {
				neighbors.clear();
				neighbors = getNeighBors(g);

				for (GridBox n : neighbors) {

					// is n the same color ?
					if (n.getColor() == g.getColor()) {

						// is n already added into a group ?
						if (n.getGroup() == null) {

							// if g has a group
							if (g.getGroup() != null) {
								g.getGroup().add(n);
								n.setGroup(g.getGroup());
								//								if (colorGroups.contains(g.getGroup(), false)) colorGroups.add(g.getGroup());
							}
							else {
								Group group = new Group(g.getColor());
								group.add(g);
								group.add(n);
								g.setGroup(group);
								n.setGroup(group);
								colorGroups.add(group);
							}
						}

						// this means that we need to join the groups together as one
						// if n's group does not contain this gridbox
						else if (n.getGroup() != null 
								&& !n.getGroup().contains(g, true)) {
							if (g.getGroup() != null) {
								g.getGroup().addAll(n.getGroup());
								colorGroups.removeValue(n.getGroup(), true);
								for (Object gridBox : n.getGroup()) 
									((GridBox)gridBox).setGroup(g.getGroup());
							}
							else {
								n.getGroup().add(g);
								g.setGroup(n.getGroup());
							}
						}
					}
				}
			}
		}
		return colorGroups;
	}
	
	/**
	 * Call this method to update and transition game state accordingly
	 */
	public void updateGameState() {
		int movesLeft = getMovesLeft();
//		System.out.println("Moves left: " + movesLeft + " Box: " + numBoxSpawned);
		
		// CHALLENGE Mode
		if (game.getGameMode() == GameMode.CHALLENGE) {
			
			if (numBoxSpawned > 0) {
				// if no more move is allowed, we set the game state to be GAMEOVER
				// and tell the Render class to update things accordingly
				if (movesLeft == 0 && getNumColorGroups() == 0) {
					game.actionResolver.showLongToast("No more move left. Game is over!");
					game.setGameState(GameState.GAMEOVER);
				}
			}
			
			else {
				// if no more colored tiles are left, we transition to COMPLETE
				if (movesLeft >= 0 && numBoxSpawned == 0) {
					game.actionResolver.showLongToast("Level comeplete!");
					game.setGameState(GameState.COMPLETE);
				}
			}
		}
	}
	

	/**************************************************************************************************/
	/*************************************** AUXILIARY METHODS ****************************************/

	public void debugDraw() {
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				//				System.out.println(this.width*h + w);
				System.out.print( grid.get(this.width*h + w) + " | ");
			}
			System.out.println();
		}
		System.out.println();
	}

	@Override
	public String toString() {
		return "Grid size: " + numBoxSpawned + "\n" + grid.toString();
	}
}
