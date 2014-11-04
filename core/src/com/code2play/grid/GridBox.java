package com.code2play.grid;

/**
 * Unit of grid. Contains data about each grid.
 * @author Boat
 *
 */
public class GridBox {
	
	/* color of the gridbox */
	public enum Color {
		NONE, BLUE, GREEN, RED, YELLOW, REMOVED;
	}
	
	/* unique number for easy identification */
	private int id;
	
	/* color of the grid box */
	private Color color;
	
	/* previous id of this grid box */
	/* Previous Id is used to identify which gridBox this is moved from */
	private int prevId;
	
	/* group of neighboring same-color gridboxes */
	private Group group;
	
	/* whether or not this 
	
	/**
	 * Constructs an initially empty gridbox with a specified id
	 */
	public GridBox(int id) {
		this.id = id;
		color = Color.NONE;
		prevId = -1;
		group = null;
	}
	
	/**
	 * Constructs a gridbox with a specified color assigned
	 * @param id
	 * @param color
	 */
	public GridBox(int id, Color color) {
		this(id);
		this.setColor(color);
		prevId = -1;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
	
	public Group getGroup() {
		return group;
	}
	
	/**
	 * Sets gridbox Id
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Checks whether or not the gridbox is empty (no color assigned)
	 * @return True iff empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.color == Color.NONE ;
	}
	
	/**
	 * Retrieves the id of this gridbox
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets this grid box color
	 * @param color The color to be assigned to this grid
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setPrevId(int id) {
		prevId = id;
	}
	
	public int getPrevId() {
		return prevId;
	}
	
	/**
	 * Clears the color, if there is one assigned
	 */
	public void clearColor() {
		color = Color.NONE;
		prevId = -1;
		group = null;
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		String color = "";
		String id = "";
		String prevId = this.prevId-1 + "";
		if (this.id > 9) id = this.id-1 + "";
		else id = this.id-1 + " ";
		
		switch(this.color) {
		case BLUE:
			color = "BLUE  ";
			break;
		case GREEN:
			color = "GREEN ";
			break;
		case NONE:
			color = "NONE  ";
			break;
		case RED:
			color = "RED   ";
			break;
		case YELLOW:
			color = "YELLOW";
			break;
		default:
			break;
		}
		return id + "(" + prevId + ")" +  ": " + color;
	}
}
