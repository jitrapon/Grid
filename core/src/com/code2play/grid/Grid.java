package com.code2play.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	/* all the gridboxes in this grid */
	private List<GridBox> grid;
	private int width;
	private int height;

	private Random random;
	private int numBoxSpawned;

	/**
	 * Constructs an initially empty grid of dimension specified 
	 * by the width and height
	 * @param width Number of gridboxes wide
	 * @param height Number of gridboxes high
	 */
	public Grid(int width, int height) {
		random = new Random();
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

	boolean firstMove = true;
	/**
	 * Updates all instances of gridboxes and logic in the grid
	 * @param deltaTime
	 */
	public void update(float deltaTime, Swipe direction) {
		// clear from last rendered frame
		if (!firstMove) move(direction);

		// spawn new gridbox
		spawnRandomGridBox();

		firstMove = false;
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
		return colors[getRandomInt(1, colors.length)];
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

	/**
	 * Moves all the spawned boxes in a direction specified
	 * @param direction
	 */
	public void move(Swipe direction) {

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
		int secondTopLeftIndex = 1;
		GridBox toMove = null;
		GridBox destination = null;
		for (int i = secondTopLeftIndex; i < width*height; i++) {
			toMove = grid.get(i);

			// also ignore leftmost column
			if (toMove.isEmpty() || i%width == 0) continue;
			else {
				// find the leftmost non-colored box to move to
				for (int j = 0;  j < width; j++) {
					// find the index that is in the same row
					int index = i - (i%width) + j;
					if (index == i) break;
					else {
						destination = grid.get(index);
						if (destination.isEmpty()) {
							destination.setPrevId( toMove.getId() );
							destination.setColor( toMove.getColor() );
							toMove.clearColor();
						}
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
		int secondRightBottomIndex = width*height - 2;
		GridBox toMove = null;
		GridBox destination = null;
		for (int i = secondRightBottomIndex; i >= 0; i--) {
			toMove = grid.get(i);

			// also ignore rightmost column
			if (toMove.isEmpty() || i%width == width-1) continue;
			else {
				// find the rightmost non-colored box to move to
				for (int j = 0;  j < width; j++) {
					// find the index that is in the same row
					int index = (width - ((i%width) + 1)) - j + i;
					if (index == i) break;
					else {
						destination = grid.get(index);
						if (destination.isEmpty()) {
							destination.setPrevId( toMove.getId() );
							destination.setColor( toMove.getColor() );
							toMove.clearColor();
						}
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
		int secondLastRowIndex = width*(height-1) - 1;
		GridBox toMove = null;
		GridBox destination = null;
		for (int i = secondLastRowIndex; i >= 0; i--) {
			toMove = grid.get(i);
			if (toMove.isEmpty()) continue;
			else {
				// find the most bottom non-colored box to move to
				for (int j = 1; j <= height; j++) {
					// find the index that is in the same column
					int index = width*(height-j) + (i%width);	
					if (index < i) break;
					else {
						destination = grid.get(index);
						if (destination.isEmpty()) {
							destination.setPrevId( toMove.getId() );
							destination.setColor( toMove.getColor() );
							toMove.clearColor();
						}
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
		int secondTopRowIndex = width;
		GridBox toMove = null;
		GridBox destination = null;
		for (int i = secondTopRowIndex; i < width*height; i++) {
			toMove = grid.get(i);
			if (toMove.isEmpty()) continue;
			else {
				// find the topmost non-colored box to move to
				for (int j = height; j >= 1; j--) {
					// find the index that is in the same column
					int index = width*(height-j) + (i%width);
					if (index > i) break;
					else {
						destination = grid.get(index);
						if (destination.isEmpty()) {
							destination.setPrevId( toMove.getId() );
							destination.setColor( toMove.getColor() );
							toMove.clearColor();
						}
					}
				}
			}
		}
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
