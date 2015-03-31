package com.code2play.grid;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.code2play.grid.game.CoinType;
import com.code2play.grid.game.GameMode;
import com.code2play.grid.game.GameState;
import com.code2play.grid.game.Grid;
import com.code2play.grid.game.GridBox;
import com.code2play.grid.game.GridBox.Color;
import com.code2play.grid.game.Swipe;
import com.code2play.grid.ui.BlurImage;
import com.code2play.grid.ui.GameDialog;
import com.code2play.grid.ui.MoveImage;
import com.code2play.grid.ui.MoveLabel;
import com.code2play.grid.ui.SwapLabel;
import com.code2play.grid.ui.UndoLabel;

/**
 * Controls how the game elements are rendered onto the game application view.
 * 
 * @author Jitrapon Tiachunpun
 *
 */
public class GameScreen implements Screen {

	/** Shared spritebatch between stages **/
	private Batch batch;

	/** Stage that contains all game elements on-screen **/
	private Stage gameStage;

	/** Input handler that handles input to the game elements and the HUD **/
	private InputMultiplexer inMultiplexer;

	/** Stage that contains all control elements of the HUD on-screen **/
	private Stage hudStage;

	/** Our camera on this game screen **/
	private OrthographicCamera camera;

	/** Lower maximum x values (width) in world coordinates of the game screen **/
	public static final int width = 720;

	/** Lower maximum y values (height) in world coordinates of the game screen **/
	public static final int height = 1280;

	/** Upper maximum x values (width) in world coordinates of the game screen **/
	public static final int maxWidth = 1080;

	/** Upper maximum y values (height) in world coordinates of the game screen **/
	public static final int maxHeight = 1920 ;

	/** Temporary flag for the renderer to know that this colored box will be removed */
	private static final int REMOVED = -3;

	/** The grid box width **/
	private float boxWidth;

	/** The grid box height **/
	private float boxHeight;

	/** Instance of the Game object that contains game state information **/
	private GameMain game;

	/** Grid instance that contains all underlying data structure of the grid **/
	private Grid grid;

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
	 * BUTTONS
	 */

	/** UI-reset button */
	private Image resetBtn;

	/** UI-moveleft button */
	private MoveImage moveBtn;

	/** UI-undo button */
	private Image undoBtn;

	/** UI-swap button */
	private Image swapBtn;

	/** UI-settings button */
	private Image settingsBtn;

	/**
	 * FONTS
	 */

	/** UI-font for in-game dialog title */
	private BitmapFont dialogTitleFont;

	/** UI-font for in-game dialog content */
	private BitmapFont dialogContentFont;

	/** UI-font for in-game dialog buttons */
	private BitmapFont dialogButtonFont;

	/** UI-font for in-game game dialog */
	private BitmapFont gameDialogTitleFont;

	/** UI-font for the gamescreen */
	private BitmapFont moveBtnFont;

	/** UI-font for the Undo button */
	private BitmapFont undoBtnFont;

	/** UI-font for the Swap button */
	private BitmapFont swapBtnFont;

	/** DEBUG font **/
	private BitmapFont debugFont;

	/** Font generator **/
	private FreeTypeFontGenerator fontGenerator;

	/** Dialog button font parameter **/
	private FreeTypeFontParameter dialogFontParam;

	/** Move button font parameter **/
	private FreeTypeFontParameter moveBtnFontParam;

	/** Undo button font parameter **/
	private FreeTypeFontParameter undoBtnFontParam;

	/** Swap button font parameter **/
	private FreeTypeFontParameter swapBtnFontParam;

	/*
	 * COLORS
	 */

	/** Move Button Font color **/
	private final String moveBtnFontColor = "FF5656";

	/** Undo/Swap Button Font color **/
	private final String vanillaFontColor = "FFF1bF";

	/** Blank box tint **/
	private final String blankBoxColor = "9AA096";

	/** UI-move label */
	private MoveLabel moveLabel;

	/** UI-undo label */
	private UndoLabel undoLabel;

	/** UI-swap label */
	private SwapLabel swapLabel;

	/** Parent Group for ui buttons 
	 * (to easily manage enabling/disabling touch events **/
	private Group btnGroup;

	/** DEBUG: FPS Logger **/
	private Label fpsLabel;

	/** Force render once **/
	private boolean forceRender = true;

	/** Whether or not game elements are responsive to touch events **/
	private boolean isGamePlaying;

	/** Temporary holder for screenshot texture to display as blurred **/
	private TextureRegion screen;

	/** Screenshot display image **/
	private Image screenshot;

	/** Framebuffer to store screenshot **/
	private FrameBuffer blurTargetA;

	private FrameBuffer blurTargetB;

	private FrameBuffer blurTargetC;

	/** Shader program to blur screen **/
	private ShaderProgram blurShader;

	/** Shader program to grey screen **/
	private ShaderProgram overlayShader;

	/** Frame buffer for storing game screen capture **/
	private TextureRegion fboRegion;

	/** Texture of the screenshot **/
	private Texture screenTexture;

	/** Popup dialog for the pause menu **/
	private GameDialog pauseMenu;

	/** Popup dialog for gameover **/
	private GameDialog gameOverMenu;

	/** STRINGS **/
	/** title of the pause menu **/
	public static final String PAUSE_MENU_TITLE = "PAUSED";

	/** title of the game over menu **/
	public static final String GAMEOVER_MENU_TITLE = "Game Over";

	/** title of a level completed with gold score **/
	private Array<String> levelCompleteGoldTitles = new Array<String>();

	/** title of a level completed with silver score **/
	private Array<String> levelCompleteSilverTitles = new Array<String>();

	/** title of a level completed with bronze score **/
	private Array<String> levelCompleteBronzeTitles = new Array<String>();

	/** Level complate label **/
	private Label levelCompleteLabel;
	
	/** Level complete game dialog **/
	private GameDialog levelCompleteDialog;

	final String VERT =  
			"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
					"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
					"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +

					"uniform mat4 u_projTrans;\n" + 
					" \n" + 
					"varying vec4 vColor;\n" +
					"varying vec2 vTexCoord;\n" +

					"void main() {\n" +  
					"	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
					"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
					"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
					"}";

	final String BLUR_FRAG =
			"#ifdef GL_ES\n" + 
					"#define LOWP lowp\n" + 
					"precision mediump float;\n" + 
					"#else\n" + 
					"#define LOWP \n" + 
					"#endif\n" + 
					"varying LOWP vec4 vColor;\n" + 
					"varying vec2 vTexCoord;\n" + 
					"\n" + 
					"uniform sampler2D u_texture;\n" + 
					"uniform float resolution;\n" + 
					"uniform float radius;\n" + 
					"uniform vec2 dir;\n" + 
					"\n" + 
					"void main() {\n" + 
					"	vec4 sum = vec4(0.0);\n" + 
					"	vec2 tc = vTexCoord;\n" + 
					"	float blur = radius/resolution; \n" + 
					"    \n" + 
					"    float hstep = dir.x;\n" + 
					"    float vstep = dir.y;\n" + 
					"    \n" +
					"	sum += texture2D(u_texture, vec2(tc.x - 4.0*blur*hstep, tc.y - 4.0*blur*vstep)) * 0.05;\n" + 
					"	sum += texture2D(u_texture, vec2(tc.x - 3.0*blur*hstep, tc.y - 3.0*blur*vstep)) * 0.09;\n" + 
					"	sum += texture2D(u_texture, vec2(tc.x - 2.0*blur*hstep, tc.y - 2.0*blur*vstep)) * 0.12;\n" + 
					"	sum += texture2D(u_texture, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.15;\n" + 
					"	\n" + 
					"	sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.16;\n" + 
					"	\n" + 
					"	sum += texture2D(u_texture, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.15;\n" + 
					"	sum += texture2D(u_texture, vec2(tc.x + 2.0*blur*hstep, tc.y + 2.0*blur*vstep)) * 0.12;\n" + 
					"	sum += texture2D(u_texture, vec2(tc.x + 3.0*blur*hstep, tc.y + 3.0*blur*vstep)) * 0.09;\n" + 
					"	sum += texture2D(u_texture, vec2(tc.x + 4.0*blur*hstep, tc.y + 4.0*blur*vstep)) * 0.05;\n" + 
					"\n" + 
					"	gl_FragColor = vColor * vec4(sum.rgb, 1.0);\n" + 
					"}";

	final String GREY_FRAG =
			"#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" + //
			"varying LOWP vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" +
			"uniform sampler2D u_texture;\n" +                     
			"uniform float grayscale;\n" +
			"void main() {\n" +  
			"       vec4 texColor = texture2D(u_texture, vTexCoord);\n" +
			"       \n" +
			"       float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));\n" +
			"       texColor.rgb = mix(vec3(gray), texColor.rgb, grayscale);\n" +
			"       \n" +
			"       gl_FragColor = texColor * vColor;\n" +
			"}";

	/**
	 * Ctor of this game screen, using the Game object as game state information
	 * @param g
	 */
	public GameScreen(GameMain g) {
		// first loaded this screen
		// game instance is the same one as the first created
		game = g;
		grid = game.getGrid();
		camera = new OrthographicCamera();
		//		camera.setToOrtho(true);
		inMultiplexer = new InputMultiplexer();

		batch = new SpriteBatch();					// recycle spritebatch for performance
		initHUDStage(batch);
		initGameStage(batch);							

		// handle back button pressed on Android to show pause menu
		InputProcessor backProcessor = new InputAdapter() {

			@Override
			public boolean keyDown(int keycode)
			{
				if (keycode == Keys.BACK) {
					Gdx.app.log("BACKKEY", "Pressed");
					if (game.getCurrentState() == GameState.PLAYING) 
						game.setGameState(GameState.PAUSED);
					else if (game.getCurrentState() == GameState.PAUSED) {
						hidePauseMenu();
						game.setGameState(GameState.PLAYING);
					}
					else {
						game.actionResolver.showShortToast("Next back button will exit the game");
						Gdx.input.setCatchBackKey(false);
					}
				}

				return true;
			}
		};

		// compile shader programs
		// important since we aren't using some uniforms and attributes that SpriteBatch expects
		ShaderProgram.pedantic = false;

		// initializes blur shader program
		blurShader = new ShaderProgram(VERT, BLUR_FRAG);

		// initialize grey shader program
		overlayShader = new ShaderProgram(VERT, GREY_FRAG);

		inMultiplexer.addProcessor(backProcessor);
		Gdx.input.setInputProcessor(inMultiplexer);
		Gdx.input.setCatchBackKey(true);
		game.actionResolver.showShortToast("Level " + grid.getLevel());
	}

	/**
	 * Initialize the HUD stage
	 */
	private void initHUDStage(Batch batch) {
		// create a new HUD stage to hold for buttons
		hudStage = new Stage( new ExtendViewport(width, height, maxWidth, maxHeight, camera), batch );
		Gdx.app.log("Stage", "Setting up HUD stage of size " + hudStage.getWidth() + " by " + 
				hudStage.getHeight());

		// initialize RESET button
		resetBtn = new Image(Assets.getResetBtn());
		float resetBtnScale = 0.8f;
		resetBtn.setOrigin(resetBtn.getWidth()/2*resetBtnScale, resetBtn.getHeight()/2*resetBtnScale);
		resetBtn.setBounds(hudStage.getWidth()-120, hudStage.getHeight()-120, resetBtn.getWidth()*resetBtnScale, 
				resetBtn.getHeight()*resetBtnScale);
		resetBtn.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			// scene2d ui elements cannot be rotated
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (gridBoxClearAnimTime == 0f) {
					restartLevel();
					event.getListenerActor().addAction(
							rotateBy(-360, .25f)
							);
				}
			}
		});

		// init font generator
		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/BlackoakStd.otf"));

		// initialize MOVE button
		moveBtn = new MoveImage(grid, Assets.getGoldMoveBtn(), Assets.getSilverMoveBtn(), 
				Assets.getBronzeMoveBtn());
		float moveBtnScale = .4f;
		float moveBtnWidth = moveBtn.getWidth()/2*moveBtnScale;
		moveBtn.setOrigin(moveBtnWidth, moveBtn.getHeight()/2*moveBtnScale);
		moveBtn.setBounds(hudStage.getWidth()/2 - moveBtnWidth, 
				hudStage.getHeight()-300, moveBtn.getWidth()*moveBtnScale, 
				moveBtn.getHeight()*moveBtnScale);
		moveBtn.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			// scene2d ui elements cannot be rotated
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

			}
		});

		// initialize undo button
		undoBtn = new Image(Assets.getUndoBtn());
		float undoBtnScale = 1f;
		undoBtn.setOrigin(undoBtn.getWidth()/2*undoBtnScale, undoBtn.getHeight()/2*undoBtnScale);
		undoBtn.setBounds(moveBtn.getX() + 195, moveBtn.getY() + 25, undoBtn.getWidth()*undoBtnScale, 
				undoBtn.getHeight()*undoBtnScale);
		undoBtn.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			// scene2d ui elements cannot be rotated
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (gridBoxClearAnimTime == 0f) {
					undoMove();
					event.getListenerActor().addAction(
							rotateBy(360, .25f)
							);
				}
			}
		});
		undoBtnFontParam = new FreeTypeFontParameter();
		undoBtnFontParam.minFilter = Texture.TextureFilter.Nearest;
		undoBtnFontParam.magFilter = Texture.TextureFilter.MipMapLinearNearest;
		undoBtnFontParam.size = (int)Math.ceil(45);
		fontGenerator.scaleForPixelHeight((int)Math.ceil(45));
		undoBtnFont = fontGenerator.generateFont(undoBtnFontParam);
		undoBtnFont.setScale(.5f, 1.2f);

		LabelStyle undoLabelStyle = new LabelStyle(undoBtnFont, GameScreen.parseColor(vanillaFontColor));
		undoLabel = new UndoLabel("0", undoLabelStyle, grid);
		undoLabel.setBounds(undoBtn.getX() + 40, undoBtn.getY(), undoBtn.getWidth(), undoBtn.getHeight());

		Group undoBtnLabel = new Group();
		undoBtnLabel.addActor(undoLabel);
		undoBtnLabel.addActor(undoBtn);

		// initialize swap number indicator
		swapBtn = new Image(Assets.getSwapBtn());
		float swapBtnScaleX = 1f;
		float swapBtnScaleY = 0.8f;
		swapBtn.setOrigin(swapBtn.getWidth()/2*swapBtnScaleX, swapBtn.getHeight()/2*swapBtnScaleY);
		swapBtn.setBounds(moveBtn.getX() - 160, moveBtn.getY() + 25, swapBtn.getWidth()*swapBtnScaleX, 
				swapBtn.getHeight()*swapBtnScaleY);
		swapBtnFontParam = new FreeTypeFontParameter();
		swapBtnFontParam.minFilter = Texture.TextureFilter.Nearest;
		swapBtnFontParam.magFilter = Texture.TextureFilter.MipMapLinearNearest;
		swapBtnFontParam.size = (int)Math.ceil(45);
		fontGenerator.scaleForPixelHeight((int)Math.ceil(45));
		swapBtnFont = fontGenerator.generateFont(swapBtnFontParam);
		swapBtnFont.setScale(.4f, .6f);

		LabelStyle swapLabelStyle = new LabelStyle(undoBtnFont, GameScreen.parseColor(vanillaFontColor));
		float singleDigitPos = swapBtn.getX()+50;
		float doubleDigitPos = swapBtn.getX()+40;
		swapLabel = new SwapLabel("0", swapLabelStyle, grid, singleDigitPos, doubleDigitPos);
		swapLabel.setBounds(swapBtn.getX()+40, swapBtn.getY()+55, swapBtn.getWidth(), swapBtn.getHeight());

		Group swapBtnLabel = new Group();
		swapBtnLabel.addActor(swapLabel);
		swapBtnLabel.addActor(swapBtn);

		// generate move button font
		// specific to CHALLENGE mode only
		moveBtnFontParam = new FreeTypeFontParameter();
		moveBtnFontParam.minFilter = Texture.TextureFilter.Nearest;
		moveBtnFontParam.magFilter = Texture.TextureFilter.MipMapLinearNearest;
		moveBtnFontParam.size = (int)Math.ceil(40);
		fontGenerator.scaleForPixelHeight((int)Math.ceil(40));
		moveBtnFont = fontGenerator.generateFont(moveBtnFontParam);
		moveBtnFont.setScale(1f, 2f);

		LabelStyle moveLableStyle = new LabelStyle(moveBtnFont, GameScreen.parseColor(moveBtnFontColor));
		singleDigitPos = moveBtn.getX() + 50;
		doubleDigitPos = moveBtn.getX() + 36;
		moveLabel = new MoveLabel("0", moveLableStyle, grid, singleDigitPos, doubleDigitPos);
		moveLabel.setBounds(doubleDigitPos, moveBtn.getY(), 
				moveBtnWidth, moveBtn.getHeight());

		Group moveBtnLabel = new Group();
		moveBtnLabel.addActor(moveBtn);
		moveBtnLabel.addActor(moveLabel);

		// initialize game settings button
		settingsBtn = new Image(Assets.getGameSettingsBtn());
		float settingsBtnScale = .8f;
		settingsBtn.setOrigin(settingsBtn.getWidth()/2*settingsBtnScale, settingsBtn.getHeight()/2*settingsBtnScale);
		settingsBtn.setBounds(20, hudStage.getHeight() - 120, settingsBtn.getWidth()*settingsBtnScale, 
				settingsBtn.getHeight()*settingsBtnScale);
		settingsBtn.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			// scene2d ui elements cannot be rotated
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (gridBoxClearAnimTime == 0f) {
					game.setGameState(GameState.PAUSED);
				}
			}
		});

		// DEBUG: fps logger
		if (game.showFPS) {
			debugFont = new BitmapFont();
			debugFont.setScale(1.5f);
			fpsLabel = new Label("FPS: ", new LabelStyle(debugFont, com.badlogic.gdx.graphics.Color.WHITE));
			fpsLabel.setX(hudStage.getWidth()/2);
			fpsLabel.setY(hudStage.getHeight() - 50);
			hudStage.addActor(fpsLabel);
		}

		// initialize game menu dialog
		dialogFontParam = new FreeTypeFontParameter();
		dialogFontParam.minFilter = Texture.TextureFilter.Nearest;
		dialogFontParam.magFilter = Texture.TextureFilter.MipMapLinearNearest;
		dialogFontParam.size = (int)Math.ceil(45);
		fontGenerator.scaleForPixelHeight((int)Math.ceil(45));
		dialogButtonFont = fontGenerator.generateFont(dialogFontParam);
		dialogButtonFont.setScale(.5f, 1.2f);

		dialogContentFont = fontGenerator.generateFont(dialogFontParam);
		dialogContentFont.setScale(.5f, .9f);

		dialogTitleFont = fontGenerator.generateFont(dialogFontParam);
		dialogTitleFont.setScale(1.2f, 1.9f);

		gameDialogTitleFont = fontGenerator.generateFont(dialogFontParam);
		gameDialogTitleFont.setScale(1f, 1.7f);

		fontGenerator.dispose();

		pauseMenu = new GameDialog(PAUSE_MENU_TITLE, Assets.getSkin(), dialogTitleFont, dialogContentFont,
				dialogButtonFont, "default") {

			protected void result(Object object) {
				String cmd = (String) object;
				Gdx.app.log("PAUSE", "Chosen: " + object);
				if (cmd.equals("level_select"))
					return;
				else if (cmd.equals("resume")) {
					hidePauseMenu();
					game.setGameState(GameState.PLAYING);
				}
				else if (cmd.equals("settings"))
					return;
			}
		};

		float btnHeight = hudStage.getHeight()*.1f;
		float btnWidth = hudStage.getWidth()*1f;

		pauseMenu.padTop(100);
		pauseMenu.getButtonTable().row().width(btnWidth).height(btnHeight);
		pauseMenu.button("Level Select", "level_select", -1f);
		pauseMenu.getButtonTable().row().width(btnWidth).height(btnHeight);
		pauseMenu.button("Settings", "settings", -1f);
		pauseMenu.getButtonTable().row().width(btnWidth).height(btnHeight);
		pauseMenu.button("Resume", "resume", -1f); 
		pauseMenu.key(Keys.BACK, "resume");

		pauseMenu.setModal(true);
		pauseMenu.setMovable(false);

		// initialize gameover dialog
		gameOverMenu = new GameDialog(GAMEOVER_MENU_TITLE, Assets.getSkin(), gameDialogTitleFont, 
				dialogContentFont, dialogButtonFont, "default") {

			protected void result(Object object) {
				String cmd = (String) object;
				if (cmd.equals("level_select"))
					return;
				else if (cmd.equals("retry")) {
					hideGameOverMenu();
					restartLevel();
					game.setGameState(GameState.PLAYING); 
				}
			}
		};

		btnWidth = hudStage.getWidth() *.5f;

		gameOverMenu.padTop(100);
		gameOverMenu.text("Nice try. You ran out of moves. \nRetry?", hudStage.getWidth()*.85f);
		gameOverMenu.button("Retry", "retry", btnWidth);
		gameOverMenu.button("Level Select", "level_select", btnWidth);

		gameOverMenu.setModal(true);
		gameOverMenu.setMovable(false);

		// initialize level complete dialog
		levelCompleteGoldTitles = new Array<String>();
		levelCompleteGoldTitles.addAll("Perfect!", "Excellent!", "Outstanding!", "Well-played!", 
				"Genius!");
		levelCompleteSilverTitles.addAll("Great Job!", "Awesome!", "Nice!");
		levelCompleteBronzeTitles.addAll("Good Try", "Passable", "You Got It", "Finally");

		levelCompleteLabel = new Label("Congratulations", new LabelStyle(gameDialogTitleFont,
				com.badlogic.gdx.graphics.Color.WHITE));
		
		levelCompleteDialog = new GameDialog("", Assets.getSkin(), gameDialogTitleFont, 
				dialogContentFont, dialogButtonFont, "default") {

			protected void result(Object object) {
				String cmd = (String) object;
				if (cmd.equals("retry")) {
					hideLevelCompleteMenu();
					restartLevel();
					game.setGameState(GameState.PLAYING); 
				}
			}
		};

		btnWidth = hudStage.getWidth() *.5f;
		btnHeight = hudStage.getHeight() *.07f;

		levelCompleteDialog.text("Some random text goes here\nUp for a new challenge?",
				hudStage.getWidth()*.85f);
		levelCompleteDialog.button("Retry", "retry", btnWidth);
		levelCompleteDialog.button("Level Select", "level_select", btnWidth);
		levelCompleteDialog.getButtonTable().row().width(levelCompleteDialog.getWidth()).height(btnHeight);
		levelCompleteDialog.button("Continue", "continue", btnWidth);

		levelCompleteDialog.setModal(true);
		levelCompleteDialog.setMovable(false); 

		// add all actor and group to the stage
		btnGroup = new Group();
		btnGroup.addActor(resetBtn);
		btnGroup.addActor(moveBtnLabel);
		btnGroup.addActor(undoBtnLabel);
		btnGroup.addActor(swapBtnLabel);
		btnGroup.addActor(settingsBtn);

		hudStage.addActor(btnGroup);

		// add this stage to the multiplexer
		inMultiplexer.addProcessor(hudStage);

		Gdx.app.log("Stage", "HUD stage initialized");
	}

	private static com.badlogic.gdx.graphics.Color parseColor(String hex) {  
		String s1 = hex.substring(0, 2);  
		int v1 = Integer.parseInt(s1, 16);  
		float f1 = (float) v1 / 255f;  
		String s2 = hex.substring(2, 4);  
		int v2 = Integer.parseInt(s2, 16);  
		float f2 = (float) v2 / 255f;  
		String s3 = hex.substring(4, 6);  
		int v3 = Integer.parseInt(s3, 16);  
		float f3 = (float) v3 / 255f;  
		return new com.badlogic.gdx.graphics.Color(f1, f2, f3, 1);  
	}  

	/**
	 * Reset all the UIs and grid boxes to initial loaded level
	 */
	private void restartLevel() {
		if (game.getGameMode() == GameMode.CHALLENGE) {
			removeAllColoredGridBoxes();
			grid.restoreDefaultState();
			firstSwapID = -1;
			secondSwapID = -1;
			forceRender = true;
			labelPosChanged = false;
			game.actionResolver.showShortToast("Restarted level");
		}
	}

	/**
	 * Undo the current move and restore previous game state before the
	 * move occured
	 * //TODO
	 */
	private void undoMove() {
		grid.undoMove();
	}

	/**
	 * Initialize the Game stage
	 * @param batch The SpriteBatch created from HUD stage
	 */
	private void initGameStage(Batch batch) {
		gameStage = new Stage( new ExtendViewport(width, height, maxWidth, maxHeight, camera), batch );
		Gdx.app.log("Stage", "Setting up game stage of size " + gameStage.getWidth() + " x " + 
				gameStage.getHeight());
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
				if (isGamePlaying) {
					startDragX = x;
					startDragY = y;
				}
				else 
					this.cancel();
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
		listener.setTapSquareSize(10);
		gameStage.addListener(listener);

		// set input processor 
		inMultiplexer.addProcessor(gameStage);
		Gdx.app.log("Stage", "Game stage initialized");
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
				com.badlogic.gdx.graphics.Color c = parseColor(blankBoxColor);
				blankBox.setColor(c.r, c.g, c.b, .22f);
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

	private int getNumSwaps() {
		return grid.getNumSwapsLeft();
	}

	private TextureRegion getBlurredScreenshotTexture() {

		//Create a frame buffer that is 25% of the original screen
		int fbWidth = Gdx.graphics.getWidth()/4;
		int fbHeight = Gdx.graphics.getHeight()/4;

		// initialize necessary fbo's 
		blurTargetA = new FrameBuffer(Pixmap.Format.RGBA8888, fbWidth, fbHeight, false);
		blurTargetB = new FrameBuffer(Pixmap.Format.RGBA8888, fbWidth, fbHeight, false);
		blurTargetC = new FrameBuffer(Pixmap.Format.RGBA8888, fbWidth, fbHeight, false);

		// setup default uniforms for the overlay shader
		overlayShader.begin();
		overlayShader.setUniformf("grayscale", .3f);
		overlayShader.end();

		// begin drawing onto the first fbo
		// this fbo contains original unaltered screen
		batch.setShader(overlayShader);
		blurTargetA.begin();

		Gdx.gl20.glClearColor(0f, 0.0f, 0.5f, 0.0f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameStage.draw();
		hudStage.draw();

		//		screenTexture = blurTargetA.getColorBufferTexture();

		//		// get the screenshot of the current framebuffer
		//		Pixmap orig = ScreenshotFactory.getScreenshot(0, 0, 
		//				Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		blurTargetA.end();
		batch.setShader(null);

		//		int origWidth = orig.getWidth();
		//		int origHeight = orig.getHeight();
		//
		//		// blur parameters
		//		int blurRadius = 4;
		//		int iterations = 3;
		//
		//		// blur the image at 25% of original size
		//		// also specify disposePixmap=true to dispose the original Pixmap
		//		Pixmap blurred = BlurUtils.blur(orig, 0, 0, origWidth, origHeight,
		//				0, 0, origWidth/4, origHeight/4,
		//				blurRadius, iterations, true);

		// setup default uniforms for the blur shader
		blurShader.begin();
		blurShader.setUniformf("dir", 0f, 0f);
		blurShader.setUniformf("radius", 1f);
		blurShader.end();

		// upload the blurred texture to GL
		screenTexture = blurTargetA.getColorBufferTexture();
		//		screenTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		//		screenTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		fboRegion = new TextureRegion(screenTexture);
		fboRegion.flip(false, true);

		//dispose blur after uploading
		//		blurred.dispose();

		return fboRegion;
	}

	float deltaTime = 0f;		// accumulated delta time used in PLAYING mode
	float endDeltaTime = 0f;	// accumulated delta time used in COMPLETE mode
	float endTime = 0f;
	float stateChangeWaitTime = .7f;
	Swipe prevSwipeDir = null;
	int prevMovesLeft = -1;
	boolean justSwiped = false;
	boolean hasBlurred = false;
	@Override
	/**
	 * Calls upon World instance to update its entities, then 
	 * renders them by WorldView
	 */
	public void render(float delta) {

		// clear screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		switch(game.getCurrentState()) {

		case PLAYING:

			batch.setShader(null);
			cleanup();
			setGameElementsTouchable(true);

			// render grid for the first time
			if (forceRender) {
				drawGrid(grid);
				forceRender = false;
			}

			// process input
			if ( (swipeDir != null || grid.getNumColorGroups() > 0 || hasSwapped) 

					//disable swipe directions
					//proceed when animation finishes for all gridboxes (after animationTime)
					&& deltaTime >= (gridBoxMoveAnimTime + gridBoxClearAnimTime) ) {

				// update gridbox - swipe
				if (!hasSwapped) {
					if (grid.getNumColorGroups() > 0 && prevSwipeDir != null) {
						if (justSwiped) {
							grid.updateMoveCount();
							justSwiped = false;
						}
						grid.update(delta, prevSwipeDir);
					}
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
					prevSwipeDir = null;
					justSwiped = true;
				}

				// process moves to be rendered
				// and set necessary actions for gameStage to draw
				drawGrid(grid);

				// reset values
				swipeDir = null;
				hasSwapped = false;

				//				System.out.println(grid.getGrid());
				deltaTime = 0f;
				endTime = 0f;
			}
			else {
				// draw timer, draw undo count, draw swap count, draw retry, draw pop (make one tile disappear),
				// draw relocate (move one tile to empty tile)
				// check input for tap on the colored tiles
				// if there is a tap, then we don't process swipe direction events
				// until the colored tile is tapped again
				if (grid.updateGameState(endTime, stateChangeWaitTime)) {
					setGameElementsTouchable(false);
					endTime += delta;
				}

			}

			// update the gameStage and draw accordingly
			gameStage.act(delta);
			gameStage.draw();

			if (game.showFPS)
				fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

			// update and draw the HUD
			hudStage.act(delta);
			hudStage.draw();

			// update time
			deltaTime += delta;
			endDeltaTime = 0f;

			break;

		case TIMEOUT:

			break;

		case PAUSED:

			// disable input events ui buttons and game elements
			setGameElementsTouchable(false);

			// display popup dialog as pause menu
			showPauseMenu();

			gameStage.act(delta);
			gameStage.draw();

			hudStage.act(delta);
			hudStage.draw();

			if (game.showFPS)
				fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

			break;

		case GAMEOVER:

			// update the gameStage and draw accordingly
			// remove input processor 
			setGameElementsTouchable(false);

			// displays gameover dialog
			showGameOverMenu();

			gameStage.act(delta);
			gameStage.draw();

			if (game.showFPS)
				fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

			hudStage.act(delta);
			hudStage.draw();

			break;

		case COMPLETE:

			// update the gameStage and draw accordingly
			setGameElementsTouchable(false);

			// displays level complete sequence dialog
			showLevelCompleteMenu(endDeltaTime);

			gameStage.act(delta);
			gameStage.draw();

			if (game.showFPS)
				fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

			hudStage.act(delta);
			hudStage.draw();

			// if Continue is pressed, then end this screen
			// and reloads the next level
			endDeltaTime += delta;

			break;

		default:

			break;
		}
	}

	/**
	 * Displays the pause menu and blurs the screen
	 */
	private void showPauseMenu() {
		if (!hasBlurred) {

			// blur the screen with greyscale tint
			screen = getBlurredScreenshotTexture();
			screenshot = new BlurImage(screen, blurShader, overlayShader, blurTargetB, blurTargetC);
			com.badlogic.gdx.graphics.Color c = screenshot.getColor();
			screenshot.setColor(c.r, c.g, c.b, 0f);
			screenshot.addAction(
					alpha(1f, .2f, Interpolation.linear)
					);

			screenshot.setBounds(0, 0, hudStage.getWidth(), hudStage.getHeight());
			hudStage.addActor(screenshot);

			// now display the menu
			pauseMenu.show(hudStage, 
					sequence(Actions.alpha(0), Actions.alpha(.5f, 0.4f, Interpolation.fade))
					);
			pauseMenu.setPosition(Math.round((hudStage.getWidth() - pauseMenu.getWidth()) / 2), 
					Math.round((hudStage.getHeight() - pauseMenu.getHeight()) / 2));

			hasBlurred = true;
		}
	}

	/**
	 * Displays the game over dialog and blurs the screen
	 */
	private void showGameOverMenu() {
		if (!hasBlurred) {

			// blur the screen with greyscale tint
			screen = getBlurredScreenshotTexture();
			screenshot = new BlurImage(screen, blurShader, overlayShader, blurTargetB, blurTargetC);
			com.badlogic.gdx.graphics.Color c = screenshot.getColor();
			screenshot.setColor(c.r, c.g, c.b, 0f);
			screenshot.addAction(
					alpha(1f, .2f, Interpolation.linear)
					);

			screenshot.setBounds(0, 0, hudStage.getWidth(), hudStage.getHeight());
			hudStage.addActor(screenshot);

			// now display the menu
			gameOverMenu.show(hudStage, 
					sequence(Actions.alpha(0), Actions.alpha(.5f, 0.4f, Interpolation.fade))
					);
			gameOverMenu.setPosition(Math.round((hudStage.getWidth() - gameOverMenu.getWidth()) / 2), 
					Math.round((hudStage.getHeight() - gameOverMenu.getHeight()) / 2));
			gameOverMenu.setWidth(hudStage.getWidth());

			hasBlurred = true;
		}
	}

	boolean labelPosChanged = false;
	/**
	 * Displays level complete dialog
	 * Call this method in the update to continue updating the dialog and subsequent animations
	 */
	private void showLevelCompleteMenu(float deltaTime) {
		
		// if we haven't displayed the label, display it
		if (levelCompleteLabel.getStage() == null) {

			// capture current screen, and blurs it
			screen = getBlurredScreenshotTexture();
			screenshot = new BlurImage(screen, blurShader, overlayShader, blurTargetB, blurTargetC);
			com.badlogic.gdx.graphics.Color c = screenshot.getColor();
			screenshot.setColor(c.r, c.g, c.b, 0f);
			screenshot.setBounds(0, 0, hudStage.getWidth(), hudStage.getHeight());
			hudStage.addActor(screenshot);
			
			// show the level complete text
			hudStage.addActor(levelCompleteLabel);

			String title = "You Win!";
			if (grid.getCoinType() == CoinType.GOLD) 
				title = levelCompleteGoldTitles.random();
			else if (grid.getCoinType() == CoinType.SILVER) 
				title = levelCompleteSilverTitles.random();
			else 
				title = levelCompleteBronzeTitles.random();

			levelCompleteLabel.setText(title);
			levelCompleteLabel.pack();
			levelCompleteLabel.setPosition(Math.round((hudStage.getWidth() - levelCompleteLabel.getWidth()) / 2), 
					hudStage.getHeight() * .5f);
			levelCompleteLabel.addAction(
					sequence(Actions.alpha(0), Actions.alpha(1f, 0.4f, Interpolation.fade))
					);
		}
		
		// if we already displayed the label, we transition it up, blurs the screen,
		// and show the dialog
		else {
			if (deltaTime > 1f && !labelPosChanged) {
				levelCompleteLabel.addAction(moveTo(levelCompleteLabel.getX(), hudStage.getHeight() *.75f, .3f, 
						Interpolation.sineOut));
				labelPosChanged = true;
			}
			
			// blur the image when action is finished and shows dialog
			else if (deltaTime > 1.1f && !hasBlurred) {
				screenshot.addAction(
						alpha(1f, .2f, Interpolation.linear)
						);
				levelCompleteDialog.show(hudStage, 
						sequence(Actions.alpha(0), Actions.alpha(.5f, 0.4f, Interpolation.fade))
						);
				levelCompleteDialog.setPosition(
						Math.round((hudStage.getWidth() - levelCompleteDialog.getWidth()) / 2), 
						Math.round((hudStage.getHeight() - levelCompleteDialog.getHeight()) / 2));
				levelCompleteDialog.setWidth(hudStage.getWidth());
				hasBlurred = true;
			}
		}
	}

	/**
	 * Hides the pause menu and unblurs the screen
	 */
	private void hidePauseMenu() {
		if (hasBlurred) {
			screenshot.addAction( sequence(
					//					alpha(0f, .2f, Interpolation.linear),
					removeActor(screenshot)
					));
			blurTargetA.dispose();
			blurTargetB.dispose();
			blurTargetC.dispose();

			// hide the pause menu
			pauseMenu.hide();

			hasBlurred = false;
		}
	}

	/**
	 * Hides the game over menu and unblurs the screen
	 */
	private void hideGameOverMenu() {
		if (hasBlurred) {
			screenshot.addAction( sequence(
					//					alpha(0f, .2f, Interpolation.linear),
					removeActor(screenshot)
					));
			blurTargetA.dispose();
			blurTargetB.dispose();
			blurTargetC.dispose();

			// hide the pause menu
			gameOverMenu.hide();

			hasBlurred = false;
		}
	}

	/**
	 * Hides the level complate menu and unblurs the screen
	 */
	private void hideLevelCompleteMenu() {
		if (hasBlurred) {
			screenshot.addAction( sequence(
					//					alpha(0f, .2f, Interpolation.linear),
					removeActor(screenshot)
					));
			blurTargetA.dispose();
			blurTargetB.dispose();
			blurTargetC.dispose();

			// hide the dialog
			levelCompleteDialog.hide();
			
			// hide the label
			levelCompleteLabel.addAction(
						sequence(Actions.fadeOut(0.4f), Actions.removeActor())
					);

			hasBlurred = false;
		}
	}

	/**
	 * Dispose stuff while rendering
	 */
	private void cleanup() {
		if (screenshot != null) {
			if (screenshot.getParent() == null) {
				screenshot = null;
				if (screenTexture != null) {
					screenTexture.dispose();
				}
			}
		}
	}


	/**
	 * Enable or disable game elements to be touchable
	 * 
	 * @param isTouchable Whether or not game elements and UI buttons are responsive to 
	 * touch events
	 */
	private void setGameElementsTouchable(boolean isTouchable) {
		if (isTouchable) {
			isGamePlaying = true;
			if (gridGroup.getTouchable() == Touchable.disabled) 
				gridGroup.setTouchable(Touchable.enabled);

			if (btnGroup.getTouchable() == Touchable.disabled) {
				btnGroup.setTouchable(Touchable.enabled);
			}
		}
		else {
			isGamePlaying = false;
			if (gridGroup.getTouchable() == Touchable.enabled) 
				gridGroup.setTouchable(Touchable.disabled);

			if (btnGroup.getTouchable() == Touchable.enabled) {
				btnGroup.setTouchable(Touchable.disabled);
			}

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
							if (getNumSwaps() > 0) {

								Image img = (Image) event.getListenerActor();
								int touchID = ((GridBox)img.getUserObject()).getId();

								// if player has not selected any other tile before this
								// make this image ID the first swap image ID
								if (firstSwapID == -1 && !hasDragged) {
									img.addAction(
											alpha (.3f, .2f)
											);
									firstSwapID = touchID;
									hasDragged = false;
									firstSwapImg = img;
								}

								// if player has selected the same image that was selected as the first swap,
								// reset the first swap image
								else if (firstSwapID == touchID) {
									img.addAction(
											alpha (1f, .2f)
											);
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
		gameStage.getViewport().update(width, height, true);
		hudStage.getViewport().update(width, height, true);
		Gdx.app.log("Screen", "Screen resized to " + width + " and " + height);
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
		cleanup();
		gameStage.dispose();
		hudStage.dispose();
		moveBtnFont.dispose();
		undoBtnFont.dispose();
		swapBtnFont.dispose();
		dialogTitleFont.dispose();
		dialogContentFont.dispose();
		dialogButtonFont.dispose();
		gameDialogTitleFont.dispose();
		if (debugFont != null) debugFont.dispose();
		blurShader.dispose();
		overlayShader.dispose();
		batch.dispose();
		if (blurTargetA != null) blurTargetA.dispose();
		if (blurTargetB != null) blurTargetB.dispose();
		if (blurTargetC != null) blurTargetB.dispose();
		Gdx.input.setInputProcessor(null);
		Gdx.app.log("DISPOSE", "GAMESCREEN DISPOSED");
	}

}

