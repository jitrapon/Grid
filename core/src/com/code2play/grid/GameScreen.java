package com.code2play.grid;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.code2play.grid.GridBox.Color;

public class GameScreen implements Screen {

	private Stage stage;
	private OrthographicCamera camera;
	public static final int width = 720;
	public static final int height = 1280;
	public static final int maxWidth = 1080;
	public static final int maxHeight = 1920 ;
	private static final int REMOVED = -3;

	private float boxWidth;
	private float boxHeight;

	private GameMain game;
	private Grid grid;
	private Skin skin;	//TODO

	private Map<Integer, Vector2> gridCoordinates;

	private Group backgroundGroup;
	private Group gridGroup;

	Swipe swipeDir = null;
	private float animationTime = 0.15f; 

	/** Image ID that will be swapped to **/
	private int firstSwapID;

	/** Image ID that will be swapped with **/
	private int secondSwapID;


	// swipe direction
	enum Swipe {
		UP, LEFT, RIGHT, DOWN;
	}


	public GameScreen(GameMain g) {

		// game instance is the same one as the first created
		game = g;
		grid = game.getGrid();
		camera = new OrthographicCamera();
		//		camera.setToOrtho(true);
		stage = new Stage( new ExtendViewport(width, height, maxWidth, maxHeight, camera) );
		gridCoordinates = new HashMap<Integer, Vector2>();
		firstSwapID = -1;
		secondSwapID = -1;

		// create all groups to hold the actors
		backgroundGroup = new Group();
		backgroundGroup.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		gridGroup = new Group();
		gridGroup.setBounds(0, 0, stage.getWidth(), stage.getHeight());

		// create all actors
		Image backgroundImg = new Image( Assets.getBackground() );
		backgroundImg.setSize(stage.getWidth(), stage.getHeight());
		backgroundGroup.addActor(backgroundImg);

		// create blank grids and initializes their positions
		createGridFromMargins(0.05f, 0.25f, 20f, 20f);

		// add all group to the stage
		stage.addActor(backgroundGroup);
		stage.addActor(gridGroup);

		// set stage inputlistener
		DragListener listener = new DragListener() {
			private float startDragX;
			private float startDragY;

			@Override
			public void dragStart(
					InputEvent event, float x, float y, int pointer) {
				startDragX = x;
				startDragY = y;
			}

			@Override
			// as soon as threshold is reached, figure out the swipe direction and set
			// swipeDir, then return
			public void drag(InputEvent event, float x, float y, int pointer) {
				float dragThresHold = 100;

				// left drag
				if (x-startDragX < -1*dragThresHold) {
					swipeDir = Swipe.LEFT;
					this.cancel();
				}

				// right drag
				else if (x-startDragX > dragThresHold) {
					swipeDir = Swipe.RIGHT;
					this.cancel();
				}

				// drag up
				else if (y-startDragY > dragThresHold) {
					swipeDir = Swipe.UP;
					this.cancel();
				}

				// drag down
				else if (y-startDragY < -1*dragThresHold) {
					swipeDir = Swipe.DOWN;
					this.cancel();
				}
			}
		};
		listener.setTapSquareSize(10);	// TODO change this to percentage of screen size
		stage.addListener(listener);

		// set input processor 
		InputMultiplexer inMultiplexer = new InputMultiplexer();

		inMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inMultiplexer);
	}

	public void createGridFromMargins(float percentWidth, float percentHeight, 
			float widthSpacing, float heightSpacing) {
		float widthMargin = percentWidth*stage.getWidth();
		float heightMargin  = (percentHeight) *stage.getHeight();
		float startingHeight = stage.getHeight() - heightMargin;

		// calculate each invididual box's width and height
		boxWidth = ((stage.getWidth() - widthMargin*2f) - 
				( widthSpacing * (grid.getWidth()+1) )) / grid.getWidth();
		boxHeight = ((stage.getHeight() - heightMargin*2f) - 
				( heightSpacing * (grid.getHeight()+1) )) / grid.getHeight();

		// lay out each box onto the grid layout, while calculating a new x,y coordinates for each box
		// on the grid
		// map each coordinate to the box's ID
		for (int col = 0; col < grid.getHeight(); col++) {
			for (int row = 0; row < grid.getWidth(); row++) {
				Image blankBox = new Image( Assets.getBlankBox() );
				blankBox.setSize(boxWidth, boxHeight);
				float x = (widthMargin+widthSpacing) + ((boxWidth+widthSpacing)*row);
				float y = (startingHeight-heightSpacing-boxHeight) - ((boxHeight+heightSpacing)*col);
				blankBox.setPosition(x, y);

				// remember this position for this id, so we don't need to calculate again
				gridCoordinates.put( row+(grid.getWidth()*col), new Vector2(x,y) );

				// add actor to the group
				gridGroup.addActor(blankBox);
			}
		}
	}

	boolean firstMove = true;
	float deltaTime = 0f;
	Swipe prevSwipeDir = null;
	int prevMovesLeft = -1;
	@Override
	/**
	 * Calls upon World instance to update its entities, then 
	 * renders them by WorldView
	 */
	public void render(float delta) {

		switch(game.getCurrentState()) {

		case PLAYING:

			// clear screen
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			// render grid for the first time
			if (firstMove) {
				drawGrid(grid);
				firstMove = false;
			}

			//TODO challenge mode
			// draw timer, draw undo count, draw swap count, draw retry, draw pop (make one tile disappear),
			// draw relocate (move one tile to empty tile)
			// check input for tap on the colored tiles
			// if there is a tap, then we don't process swipe direction events
			// until the colored tile is tapped again
			if (game.getGameMode() == GameMode.CHALLENGE) {
				int movesLeft = grid.getMovesLeft();
				if (prevMovesLeft != movesLeft) {
					prevMovesLeft = movesLeft;
					System.out.println("Moves left: " + grid.getMovesLeft());
				}
			}

			// check input
			//TODO double elimination case
			if ((swipeDir != null
					|| grid.getNumColorGroups() > 0
					) && deltaTime >= animationTime //disable swipe directions
					//proceed when animation finishes for all gridboxes (after animationTime)
					) {
				// update gridbox
				if (grid.getNumColorGroups() > 0) grid.update(delta, prevSwipeDir);
				else {
					grid.update(delta, swipeDir);
					grid.updateMoveCount();
				}

				prevSwipeDir = swipeDir == null ? prevSwipeDir : swipeDir;

				// process moves to be rendered
				drawGrid(grid);
				swipeDir = null;

				System.out.println(grid.getGrid());
				deltaTime = 0f;
			}

			// update
			stage.act(delta);

			// draw
			stage.draw();

			// update time
			deltaTime += delta;

			break;

		case TIMEOUT:

			break;

		case PAUSED:

			break;

		case GAMEOVER:

			break;

		default:

			break;
		}


	}

	/**
	 * Renders all the stuff that needs to be drawn from the grid
	 * @param grid
	 */
	private void drawGrid(Grid grid) {
		Iterator<Actor> iter = null;
		for (GridBox gridBox : grid.getGrid()) {
			if (!gridBox.isEmpty()) {

				// recently spawned
				if (gridBox.getPrevId() == -1) {
					Image gridBoxImg = new Image( Assets.getColoredBox(gridBox.getColor()) );
					//					newGridBox.setSize(boxWidth, boxHeight);
					gridBoxImg.setSize(1, 1);
					Vector2 pos = gridCoordinates.get(gridBox.getId()-1);
					gridBoxImg.setPosition(pos.x + (boxWidth/2f), pos.y + (boxHeight/2));
					gridBoxImg.addAction(
							parallel(
									scaleTo(boxWidth, boxHeight, animationTime, Interpolation.linear),
									moveTo(pos.x, pos.y, animationTime, Interpolation.linear)
									));
					gridBox.setPrevId(-2); 										// not -1 again to avoid replaying spawning animation
					//					System.out.println("Added Color " + gridBox.getColor());
					gridBoxImg.setUserObject( new GridBox(gridBox.getId(), gridBox.getColor()) );

					// add touch listener
					gridBoxImg.addListener(new InputListener() {
						@Override
						public boolean touchDown(InputEvent event, float x, float y,
								int pointer, int button) {
							//TODO
							return true;
						}

						@Override
						public void touchUp (InputEvent event, float x, float y, 
								int pointer, int button) {
							Image img = (Image) event.getListenerActor();
							int touchID = ((GridBox)img.getUserObject()).getId();

							// if player has not selected any other tile before this
							// make this image ID the first swap image ID
							if (firstSwapID == -1) {
								//								img.addAction(
								//										scaleTo(20f, 20f, .1f)
								//										);
								firstSwapID = touchID;
								Gdx.app.log("Swap 1", "Selected image ID " + firstSwapID);
							}

							// if player has selected the same image that was selected as the first swap,
							// reset the first swap image
							else if (firstSwapID == touchID) {
								//								img.addAction(
								//										scaleTo(-.5f, -.5f, .1f)
								//										);
								Gdx.app.log("Swap 1", "Deselected image ID " + firstSwapID);
								firstSwapID = -1;
							}

							// if the first swap image has already been selected, then set this image
							// to be the second swap and change their previous IDs accordingly
							else {
								secondSwapID = touchID;
								Gdx.app.log("Swap 2", "Swap " + firstSwapID + " with " + secondSwapID);


								firstSwapID = -1;
								secondSwapID = -1;
							}


						}
					});
					gridGroup.addActor(gridBoxImg);
				}

				// recently moved
				else {
					// get stage actor under gridGroup of userobjecttype with id = move.originId and 
					// update it to move.destId
					// add move transition animation
					// cannot call removeActor while iterating
					iter = gridGroup.getChildren().iterator();
					while (iter.hasNext()) {
						Actor gridBoxImg = iter.next();
						GridBox temp = (GridBox) gridBoxImg.getUserObject();
						if (temp != null) {
							if ( temp.getId() == gridBox.getPrevId()  
									&& temp.getColor() == gridBox.getColor() ) {
								Vector2 pos = gridCoordinates.get(gridBox.getId()-1);
								gridBoxImg.addAction(
										moveTo(pos.x, pos.y, animationTime, Interpolation.linear)
										);
								//								System.out.println("Move from " + (gridBox.getPrevId()-1) + " to " + (gridBox.getId()-1));
								gridBox.setPrevId(-2);							// not -1 again to avoid replaying spawning animation

								// update reference object
								temp.setPrevId(gridBox.getPrevId());
								temp.setId(gridBox.getId());
								gridBoxImg.setUserObject(temp);
							}

							// eliminates old gridbox color in group
							else if ( temp.getId() == gridBox.getId()
									&& temp.getColor() != gridBox.getColor() ) {
								//TODO eliminate it
								temp.setPrevId(REMOVED);							// mark as removed 
								if (gridBox.getColor() == Color.REMOVED) {
									gridBox.setColor(Color.NONE);
									gridBox.setPrevId(-1);
								}
							}
						}
					}
				}
			}
		}

		// remove all grids marked as removed
		iter = gridGroup.getChildren().iterator();
		while (iter.hasNext()) {
			Actor gridBoxImg = iter.next();
			GridBox temp = (GridBox) gridBoxImg.getUserObject();
			if (temp != null) 
				if (temp.getPrevId() == REMOVED) 
					iter.remove();
		}
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("Resizing screen to " + width + " and " + height);
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		//		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	// never called automatically
	public void dispose() {
		stage.dispose();
		//		skin.dispose();
	}

}

