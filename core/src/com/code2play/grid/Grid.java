package com.code2play.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.code2play.grid.GameScreen.Swipe;
import com.code2play.grid.GridBox.Color;

/**
 * Initialization and storing grid information
 * Grid class contains data about each grid, as well as 
 * updating all instances of each unit of grid box.
 * to be used by the graphics classes
 * @author BoatNad
 *
 */
public class Grid {

	/* state of the game */
	private GameMain game;
	private int numMovesLeft;
	private float maxLevelTime;
	private int numSwapsLeft;
	
	/* all the gridboxes in this grid */
	private List<GridBox> grid;
	private int width;
	private int height;

	private Random random;
	private int numBoxSpawned;

	private Array<Group> colorGroups;
	private static final int MIN_CHAIN_SIZE = 3;
	private int numMinChainGroup;

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
		Grid g = new Grid(game);
		
		// read off values
		g.numMovesLeft = 5;
		g.maxLevelTime = 120f;
		g.numSwapsLeft = 5;
		int width = 5;
		int height = 5;
		
		// initialize grid data structure
		// with dimensions
		System.out.println("Initializing grid with dimension " + 
				width + "x" + height);
		int totalNumBox = width*height;
		g.width = width;
		g.height = height;
		int currId = 1;
		g.grid = new ArrayList<GridBox>(totalNumBox);

		for (int i = 0; i < totalNumBox; i++) {
			currId = i+1;
			GridBox box = new GridBox(currId);
			g.grid.add(box);
		}
		g.colorGroups = new Array<Group>();
		
		// spawn colored tiles from file
//		g.spawnGridBoxAt(1, Color.GREEN);
		for (int i = 0; i < 12; i++) 
			g.spawnRandomGridBox();
		
		return g;
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
	
	public int getMovesLeft() {
		return numMovesLeft;
	}

	boolean firstMove = true;
	boolean done = false;
	/**
	 * Updates all instances of gridboxes and logic in the grid
	 * @param deltaTime
	 */
	public void update(float deltaTime, Swipe direction) {
		// clear from last rendered frame
		// TODO SWIPE TO BEGIN LEVEL
//		if (!firstMove) move(direction);
//		firstMove = false;
		move(direction);

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
		numBoxSpawned++;
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
		return box;
	}
	
	public void decrNumMovesLeft() {
		if (numMovesLeft > 0) numMovesLeft--;
	}

	/**
	 * Moves all the spawned boxes in a direction specified
	 * TODO check all gridboxes whose group has size >= MIN_CHAIN_SIZE 
	 * set their color to NONE (empty)
	 * @param direction
	 */
	public void move(Swipe direction) {
		
		//TODO handle swap logic here

		switch(direction) {
		case DOWN:
			moveDown();
			break;
		case LEFT:
			moveLeft();
			break;
		case RIGHT:
			moveRight();
			break;
		case UP:
			moveUp();
			break;
		default:
			break;
		}
	}

	/**
	 * Move all boxes leftward
	 */
	private void moveLeft() {
		// iterate through all colored boxes, starting at 
		// the second top row
		//		int secondTopLeftIndex = 1;
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
						break;
					}
				}
			}

		}
	}

	/**
	 * Move all boxes rightward
	 */
	private void moveRight() {
		// iterate through all colored boxes, starting at 
		// the second rightmost bottom row
		//		int secondRightBottomIndex = width*height - 2;
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
						destination.setPrevId( toMove.getId() );
						destination.setColor( toMove.getColor() );
						toMove.clearColor();
						break;
					}
				}

			}
		}
	}

	/**
	 * Move all boxes downward
	 */
	private void moveDown() {
		// iterate through all colored boxes, starting at 
		// the second most bottom row
		//		int secondLastRowIndex = width*(height-1) - 1;
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
						break;
					}
				}
			}

		}
	}

	/**
	 * Move all boxes upward
	 */
	private void moveUp() {
		// iterate through all colored boxes, starting at
		// the second most top row
		//		int secondTopRowIndex = width;
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
						break;
					}
				}
			}

		}
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
