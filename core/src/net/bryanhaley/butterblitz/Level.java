package net.bryanhaley.butterblitz;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;

/* Level Objects handle the Tiled Map we import from assets */

public class Level
{
	private TiledMap map; // holds the TMX file
	private OrthogonalTiledMapRenderer renderer; // renders the level tiles
	private World world; // reference to world created in GameMain
	public static final float PIXELS_TO_METERS = 0.15625f, METERS_TO_PIXELS = 6.4f;
	
	private GameObject test;
	private GameObject playerTest;

	// constructors; forward to create to keep them tidy
	public Level(World world)
	{
		this.create(world, "example.tmx");
	}

	public Level(World world, String tilemap)
	{
		this.create(world, tilemap);
	}

	// Load the TMX file, initialize renderer & parse collision objects
	public void create(World boxWorld, String tilemap)
	{
		map = new TmxMapLoader().load(tilemap);
		renderer = new OrthogonalTiledMapRenderer(map, 1);
		this.world = boxWorld;

		// Create parser. Originally I wrote my own code for this, but I found
		// this had been created recently.
		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.setUnitScale(this.PIXELS_TO_METERS);
		parser.load(world, map);

		// report number of collision objects found for debug purposes
		Gdx.app.log("Num Map Objects", "" + map.getLayers().get("CollisionMap").getObjects().getCount());

		test = new GameObject(world);
		playerTest = new Player(world);
	}

	public void update()
	{
		test.update();
		playerTest.update();
	}

	public void render(OrthographicCamera camera)
	{
		renderer.setView(camera);
		renderer.render();
	}
	
	public void renderObjects(SpriteBatch batch)
	{
		test.render(batch);
		playerTest.render(batch);
	}

	// if we need to manipulate the map file elsewhere for whatever reason
	public TiledMap getMap()
	{
		return map;
	}
	
	public void dispose()
	{
		map.dispose();
		renderer.dispose();
		test.dispose();
		playerTest.dispose();
	}
}
