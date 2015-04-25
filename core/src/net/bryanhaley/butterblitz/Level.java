package net.bryanhaley.butterblitz;

import java.util.ArrayList;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/* Level Objects handle the Tiled Map we import from assets */

public class Level
{
	private TiledMap map; // holds the TMX file
	private OrthogonalTiledMapRenderer renderer; // renders the level tiles
	private World world; // reference to world created in GameMain
	public static final float PIXELS_TO_METERS = 0.15625f, METERS_TO_PIXELS = 6.4f;
	public static ArrayList<GameObject> gameObjs; //List of all gameobjects in the level
	private static ArrayList<GameObject> destroy; //list of objects to be destroyed
	private Player player;

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
		
		//These are static because only one level will exist at a time and it's easier to access them this way
		gameObjs = new ArrayList<GameObject>();
		destroy = new ArrayList<GameObject>();

		// Create parser. Originally I wrote my own code for this, but I found
		// this had been created recently.
		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.setUnitScale(Level.PIXELS_TO_METERS);
		parser.load(world, map);

		// report number of collision objects found for debug purposes
		Gdx.app.log("Num Map Objects", "" + map.getLayers().get("CollisionMap").getObjects().getCount());

		//Find and create objects recorded on the Entities layer of the map
		interpretMapEntities();
	}
	
	public void interpretMapEntities()
	{
		MapObjects mapObjects = map.getLayers().get("Entities").getObjects();
		
		//Find the type, which defines what object the entity is, then find the position
		//and any other data necessary to create the object.
		for (MapObject object : mapObjects)
		{
			//create the player
			if (((String) object.getProperties().get("type", String.class)).equals("player_spawn"))
			{
				player = new Player(world, new Vector2(
						object.getProperties().get("x", float.class),
						object.getProperties().get("y", float.class)));
			}
			
			//create the example enemy
			if (((String) object.getProperties().get("type", String.class)).equals("example_enemy"))
			{
				new Enemy(world, player, "example_enemy.png", new Rectangle(object.getProperties().get("x", float.class),
																			object.getProperties().get("y", float.class),
																			32, 32));
			}
		}
	}

	public void update()
	{
		//always update the player first
		player.update();
		
		//update all game objects
		for (GameObject gameObject : gameObjs)
		{
			gameObject.update();
		}
		
		//remove game objects that need to be destroyed.
		//we can't do that from inside the prior for loop, so keep a list of objects to be destroyed
		//and destroy them here
		for (GameObject destructObj : destroy)
		{
			gameObjs.remove(destructObj);
			world.destroyBody(destructObj.body);
		}
		
		//clear the list of destroyed objects. The garbage collector and asset manager should handle
		//things from here.
		destroy.clear();
	}

	//render the level
	public void render(OrthographicCamera camera)
	{
		renderer.setView(camera);
		renderer.render();
	}
	
	//render object in the level
	public void renderObjects(SpriteBatch batch)
	{
		for (GameObject gameObject : gameObjs)
		{
			gameObject.render(batch);
		}
		
		//always render the player last so that the player is always on top
		player.render(batch);
	}
	
	public static void destroyObject(GameObject obj)
	{
		destroy.add(obj);
	}

	// if we need to manipulate the map elsewhere for whatever reason
	public TiledMap getMap()
	{
		return map;
	}
	
	//Dispose of everything used. Need to implement the assets manager!
	public void dispose()
	{
		map.dispose();
		renderer.dispose();
	}
}
