package com.code2play.grid;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;

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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.code2play.grid.GridBox.Color;

public class GameScreen implements Screen {

	// Grid constants
	/** Game stage: contains all game elements onscreen **/
	private Stage gameStage;

	/** Input multiplexer for game stage **/
	private InputMultiplexer inMultiplexer;

	/** HUD stage: contains all control elements of the HUD onscreen **/
	private Stage hudStage;

	private OrthographicCamera camera;
	public static final int width = 720;
	public static final int height = 1280;
	public static final int maxWidth = 1080;
	public static final int maxHeight = 1920 ;
	private static final int REMOVED = -3;
	private float boxWidth;
	private float boxHeight;

	/** Instance of the Game object that contains game state information **/
	private GameMain game;

	/** Grid instance that contains all underlying data structure of the grid **/
	private Grid grid;

	/** Skin of the rendered objects onscreen **/
	private Skin skin;	//TODO

	/** Calculated coordinates of all the grid boxes for easy reference **/
	private Map<Integer, Vector2> gridCoordinates;

	/** Group of background images **/
	private Group backgroundGroup;

	/** Group that contains all spawned images of colored tiles **/
	private Group gridGroup;

	/** Swipe direction determined from drag direction **/
	Swipe swipeDir = null;

	/** Animation time in seconds of the grid movements **/
	private float gridBoxMoveAnimTime = 0.15f; 

	/** Animation time in seconds in clearing gridboxes **/
	private float gridBoxClearAnimTime = 0f;

	/** Image ID that will be swapped to **/
	private int firstSwapID;

	/** Image ID that will be swapped with **/
	private int secondSwapID;

	/** Keeps track of disabling touch input to colored tiles when 
	 * player has already swiped in a direction
	 */
	private boolean hasDragged;

	/** Indicates whether a swap has happened **/
	private boolean hasSwapped;

	/** Minimum distance in virtual coordinates to determine 
	 * dragging direction
	 */
	private static float DRAG_MIN_THRESHOLD = 50f;

	/**
	 * UI-reset button
	 */
	private Button resetBtn;
	
	/** Force render once **/
	private boolean forceRender = true;


	/** All the swipe directions for easy reference **/
	enum Swipe {
		UP, LEFT, RIGHT, DOWN;
	}

	/**
	 * Ctor of this game screen, using the Game object as game state information
	 * @param g
	 */
	public GameScreen(GameMain g) {
		// game instance is the same one as the first created
		game = g;
		grid = game.getGrid();
		camera = new OrthographicCamera();
		//		camera.setToOrtho(true);
		inMultiplexer = new InputMultiplexer();
		initHUDStage();
		initGameStage();

		Gdx.input.setInputProcessor(inMultiplexer);
	}

	/**
	 * Initialize the HUD stage
	 */
	private void initHUDStage() {
		// create a new HUD stage to hold for buttons
		hudStage = new Stage( new ExtendViewport(width, height, maxWidth, maxHeight, camera) );

		// initialize RESET button
		skin = new Skin();
		ButtonStyle resetBtnStyle = new ButtonStyle();
		Drawable resetBtnDrawable = new Image(Assets.getResetBtn()).getDrawable();
		resetBtnStyle.up = resetBtnDrawable;
		resetBtnStyle.down = resetBtnDrawable;
		resetBtn = new Button(resetBtnStyle);
		resetBtn.setOrigin(resetBtn.getWidth()/2, resetBtn.getHeight()/2);
		resetBtn.setBounds(hudStage.getWidth()-120, hudStage.getHeight()-120, resetBtn.getWidth()*.8f, 
				resetBtn.getHeight()*.8f);
		resetBtn.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			// scene2d ui elements cannot be rotated
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				restartLevel();
			}
		});
		hudStage.addActor(resetBtn);


		inMultiplexer.addProcessor(hudStage);
	}
	
	/**
	 * Reset all the UIs and grid boxes to initial loaded level
	 */
	private void restartLevel() {
		if (game.getGameMode() == GameMode.CHALLENGE) {
			removeAllColoredGridBoxes();
			grid.restoreDefaultState();
			forceRender = true;
			game.actionResolver.showShortToast("Restarted level");
		}
	}

	/**
	 * Initialize the Game stage
	 */
	private void initGameStage() {
		gameStage = new Stage( new ExtendViewport(width, height, maxWidth, maxHeight, camera) );
		gridCoordinates = new HashMap<Integer, Vector2>();
		firstSwapID = -1;
		secondSwapID = -1;

		// create all groups to hold the actors
		backgroundGroup = new Group();
		backgroundGroup.setBounds(0, 0, gameStage.getWidth(), gameStage.getHeight());
		gridGroup = new Group();
		gridGroup.setBounds(0, 0, gameStage.getWidth(), gameStage.getHeight());

		// create all actors
		Image backgroundImg = new Image( Assets.getBackground() );
		backgroundImg.setSize(gameStage.getWidth(), gameStage.getHeight());
		backgroundGroup.addActor(backgroundImg);

		// create blank grids and initializes their positions
		createGridFromMargins(0.05f, 0.25f, 20f, 20f);

		// add all group to the gameStage
		gameStage.addActor(backgroundGroup);
		gameStage.addActor(gridGroup);

		// set gameStage inputlistener
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

				// only detects swiping direction when the player has not 
				// selected any colored tile
				if (firstSwapID != -1) {
					swipeDir = null;
					this.cancel();
				}
				else {

					// left drag
					if (x-startDragX < -1*DRAG_MIN_THRESHOLD) {
						swipeDir = Swipe.LEFT;
						hasDragged = true;
						firstSwapID = -1;
						secondSwapID = -1;
						this.cancel();
					}

					// right drag
					else if (x-startDragX > DRAG_MIN_THRESHOLD) {
						swipeDir = Swipe.RIGHT;
						hasDragged = true;
						firstSwapID = -1;
						secondSwapID = -1;
						this.cancel();
					}

					// drag up
					else if (y-startDragY > DRAG_MIN_THRESHOLD) {
						swipeDir = Swipe.UP;
						hasDragged = true;
						firstSwapID = -1;
						secondSwapID = -1;
						this.cancel();
					}

					// drag down
					else if (y-startDragY < -1*DRAG_MIN_THRESHOLD) {
						swipeDir = Swipe.DOWN;
						hasDragged = true;
						firstSwapID = -1;
						secondSwapID = -1;
						this.cancel();
					}
				}
			}
		};
		listener.setTapSquareSize(10);	// TODO change this to percentage of screen size
		gameStage.addListener(listener);

		// set input processor 
		inMultiplexer.addProcessor(gameStage);
	}


	public void createGridFromMargins(float percentWidth, float percentHeight, 
			float widthSpacing, float heightSpacing) {
		float widthMargin = percentWidth*gameStage.getWidth();
		float heightMargin  = (percentHeight) *gameStage.getHeight();
		float startingHeight = gameStage.getHeight() - heightMargin;

		// calculate each invididual box's width and height
		boxWidth = ((gameStage.getWidth() - widthMargin*2f) - 
				( widthSpacing * (grid.getWidth()+1) )) / grid.getWidth();
		boxHeight = ((gameStage.getHeight() - heightMargin*2f) - 
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
			if (forceRender) {
				drawGrid(grid);
				forceRender = false;
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
					System.out.println("Moves left: " + movesLeft);

					// if no more move is allowed, we set the game state to be GAMEOVER
					// and render things accordingly
					if (movesLeft == 0) {
						game.actionResolver.showLongToast("No more move left. Game is over!");
						game.setGameState(GameState.GAMEOVER);
					}
				}
			}

			// process input
			//TODO double elimination case
			if ((swipeDir != null || grid.getNumColorGroups() > 0 || hasSwapped) 

					//disable swipe directions
					//proceed when animation finishes for all gridboxes (after animationTime)
					&& deltaTime >= (gridBoxMoveAnimTime + gridBoxClearAnimTime)) {

				// update gridbox - swipe
				if (!hasSwapped) {
					if (grid.getNumColorGroups() > 0 && prevSwipeDir != null) 
						grid.update(delta, prevSwipeDir);
					else if (swipeDir != null) {
						if (grid.update(delta, swipeDir))
							grid.updateMoveCount();
					}

					// we add slight delay for the animation of claring gridboxes to proceed
					// before continuing
					if (grid.getNumColorGroups() > 0)
						gridBoxClearAnimTime = 0.35f;
					else
						gridBoxClearAnimTime = 0f;

					prevSwipeDir = swipeDir == null ? prevSwipeDir : swipeDir;
				}

				// update gridbox - swap
				else {
					grid.update(delta, firstSwapID, secondSwapID);
					grid.updateMoveCount();
					firstSwapID = -1;
					secondSwapID = -1;
				}

				// process moves to be rendered
				// and set necessary actions for gameStage to draw
				drawGrid(grid);

				// reset values
				swipeDir = null;
				hasSwapped = false;

				//				System.out.println(grid.getGrid());
				deltaTime = 0f;
			}

			// update the gameStage and draw accordingly
			gameStage.act(delta);
			gameStage.draw();

			// update and draw the HUD
			hudStage.act(delta);
			hudStage.draw();

			// update time
			deltaTime += delta;

			break;

		case TIMEOUT:

			break;

		case PAUSED:

			break;

		case GAMEOVER:

			// clear screen
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			// update the gameStage and draw accordingly
			// remove input processor 
			if (inMultiplexer.getProcessors().contains(gameStage, false)) 
				inMultiplexer.removeProcessor(gameStage);
			
			if (inMultiplexer.getProcessors().contains(hudStage, false)) 
				inMultiplexer.removeProcessor(hudStage);
			
			gameStage.act(delta);
			gameStage.draw();
			
			hudStage.act(delta);
			gameStage.draw();

			break;

		default:

			break;
		}
	}

	/**
	 * Removes all colored grid boxes onscreen
	 */
	private void removeAllColoredGridBoxes() {
		Iterator<Actor> iter = gridGroup.getChildren().iterator();
		while (iter.hasNext()) {
			Actor gridBoxImg = iter.next();
			GridBox temp = (GridBox) gridBoxImg.getUserObject();
			if (temp != null) {
				iter.remove();
			}
		}
	}

	/**
	 * Renders all the stuff that needs to be drawn from the grid
	 * @param grid
	 */
	Image firstSwapImg = null;
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
									scaleTo(boxWidth, boxHeight, gridBoxMoveAnimTime, Interpolation.linear),
									moveTo(pos.x, pos.y, gridBoxMoveAnimTime, Interpolation.linear)
									));
					gridBox.setPrevId(-2); 										// not -1 again to avoid replaying spawning animation
					//					System.out.println("Added Color " + gridBox.getColor());
					gridBoxImg.setUserObject( new GridBox(gridBox.getId(), gridBox.getColor()) );

					// add touch listener
					gridBoxImg.addListener(new InputListener() {
						@Override
						public boolean touchDown(InputEvent event, float x, float y,
								int pointer, int button) {
							hasDragged = false;
							return true;
						}

						@Override
						public void touchUp (InputEvent event, float x, float y, 
								int pointer, int button) {
							Image img = (Image) event.getListenerActor();
							int touchID = ((GridBox)img.getUserObject()).getId();

							// if player has not selected any other tile before this
							// make this image ID the first swap image ID
							if (firstSwapID == -1 && !hasDragged) {
								img.addAction(
										alpha (.3f, .2f)
										);
								firstSwapID = touchID;
								Gdx.app.log("Swap 1", "Selected image ID " + firstSwapID);
								hasDragged = false;
								firstSwapImg = img;
							}

							// if player has selected the same image that was selected as the first swap,
							// reset the first swap image
							else if (firstSwapID == touchID) {
								img.addAction(
										alpha (1f, .2f)
										);
								Gdx.app.log("Swap 1", "Deselected image ID " + firstSwapID);
								firstSwapID = -1;
								firstSwapImg = null;
							}

							// if the first swap image has already been selected, then set this image
							// to be the second swap and change their previous IDs accordingly
							else if (firstSwapID != -1) {
								secondSwapID = touchID;

								// remove effect for the first swap image
								firstSwapImg.addAction(
										alpha (1f, .2f)
										);
								hasSwapped = true;

								Gdx.app.log("Swap 2", "Swap " + firstSwapID + " with " + secondSwapID);
							}


						}
					});
					gridGroup.addActor(gridBoxImg);
				}

				// recently moved
				else {

					// get gameStage actor under gridGroup of userobjecttype with id = move.originId and 
					// update it to move.destId
					// add move transition animation
					// cannot call removeActor while iterating
					iter = gridGroup.getChildren().iterator();
					while (iter.hasNext()) {
						Actor gridBoxImg = iter.next();
						GridBox temp = (GridBox) gridBoxImg.getUserObject();
						if (temp != null) {
							if ( temp.getId() == gridBox.getPrevId()  
									&& temp.getColor() == gridBox.getColor()
									//									&& temp.getPrevId() != REMOVED 
									) {
								Vector2 pos = gridCoordinates.get(gridBox.getId()-1);
								gridBoxImg.addAction(
										moveTo(pos.x, pos.y, gridBoxMoveAnimTime, Interpolation.linear)
										);
								//								System.out.println("Move from " + (gridBox.getPrevId()-1) + " to " + (gridBox.getId()-1));
								gridBox.setPrevId(-2);							// not -1 again to avoid replaying spawning animation

								// update reference object
								temp.setPrevId(gridBox.getPrevId());
								temp.setId(gridBox.getId());
								gridBoxImg.setUserObject(temp);
							}

							// eliminates old gridbox color in group
							//TODO BUG: when new gridbox of the same color is moved to
							// the tile that will be removed
							else if ( temp.getId() == gridBox.getId()
									&& temp.getColor() != gridBox.getColor() ) {
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
				if (temp.getPrevId() == REMOVED) {
					// if remove action is not set for this gridbox, add it
					if (!temp.removed) {
						gridBoxImg.addAction(
								parallel(
										scaleTo(0, 0, .08f, Interpolation.linear),
										moveTo(gridBoxImg.getX() + boxHeight/2, gridBoxImg.getY() + boxWidth/2, 
												.08f, Interpolation.linear)
										));
						temp.removed = true;
						//						Gdx.app.log("ADD ACTION", "GRID ID " + temp.getId() + " ADDED ACTION! (" 
						//								+ gridBoxClearAnimTime + ")");
					}

					// else if action is completed, remove it from list to be rendered
					else {
						if (gridBoxImg.getActions().size == 0) {
							//							Gdx.app.log("REMOVE", "GRID ID " + temp.getId() + " REMOVED!");
							iter.remove();
						}
					}
				}
		}
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("Resizing screen to " + width + " and " + height);
		gameStage.getViewport().update(width, height, true);
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
		gameStage.dispose();
		skin.dispose();
	}

}

