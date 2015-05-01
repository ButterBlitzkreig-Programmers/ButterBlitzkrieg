package net.bryanhaley.butterblitz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class GameState implements State
{
	private SpriteBatch batch; // Sends all images to be rendered at once;
	// opengl/opengl es likes it this way
	private OrthographicCamera camera; // manages scaling and coordinates
	// internal resolution; 720p HD. Could be changed.
	public static final int RESOLUTION_WIDTH = 1280, RESOLUTION_HEIGHT = 720;
	private World world; // Box2D world
	private Level level; // Tiled level
	private Box2DDebugRenderer debugRenderer;
	private String firstMap;

	@Override
	public void create(String[] args)
	{
		// TODO Auto-generated method stub
		for (String str : args)
		{
			if (str.startsWith("-map "))
			{
				firstMap = str.substring(str.indexOf(" ")+1);
			}
		}
		
		this.create();
	}

	@Override
	public void create(String arg)
	{
		if (arg.startsWith("-map "))
		{
			firstMap = arg.substring(arg.indexOf(" ")+1);
		}

		this.create();
	}
	
	// initialize game
	@Override
	public void create()
	{
		batch = new SpriteBatch(); // initialize spritebatch

		// create 2D camera. Using common SNES resolution since we
		// agreed on 16 bit style art, but we can change this later.
		camera = new OrthographicCamera(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
		// camera.setToOrtho(false, RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
		camera.update(); // this updates any changes to the camera

		/*--temporary test--*/
		camera.position.set(camera.viewportWidth * 0.5f, camera.viewportHeight * 0.5f, 0);
		camera.update();
		/*--temporary test--*/

		// Initialize Box2D
		world = new World(new Vector2(0, -50), true); // create world with normal
		// gravity
		world.setContactListener(new GameObjCollision()); // See GameObjCollision class

		// Debug renderer for vieweing collision objects
		debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
		
		if (firstMap == null)
		{ firstMap = "example.tmx"; }

		// Initialize level.
		level = new Level(world, camera, firstMap);
	}

	// increment all actions and interactions; test for collisions
	public void update()
	{
		level.update();

		camera.position.set(level.getPlayer().getPositionPixelsCentered().x, level.getPlayer()
				.getPositionPixelsCentered().y, 0);
		camera.update();

		// not gonna bother with a proper timestep since it's just a level demo
		// upate Box2D interactions
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);

		level.destroyBodies();
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
		{
			GameMain.switchState(new MainMenu());
		}
	}

	@Override
	public void render()
	{
		// clear the screen to neutral grey (RGBA)
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Level uses its own spritebatch
		level.render(camera);

		// make the spritebatch use our camera
		batch.setProjectionMatrix(camera.combined);

		// render all sprites between begin and end
		batch.begin();

		// render box2d bodies for debug purposes
		if (Gdx.input.isKeyPressed(Keys.P))
		{
			Matrix4 scaledCamera = new Matrix4(camera.combined);
			scaledCamera.scale(Level.METERS_TO_PIXELS, Level.METERS_TO_PIXELS, Level.METERS_TO_PIXELS);
			debugRenderer.render(world, scaledCamera);
		}

		level.renderObjects(batch);

		batch.end();
	}

	public void dispose()
	{
		world.dispose();
		level.dispose();
	}
}
