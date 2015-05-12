package net.bryanhaley.butterblitz;

import java.util.ArrayList;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/* Level Objects handle the Tiled Map we import from assets */

public class Level
{
	private TiledMap map; // holds the TMX file
	private OrthogonalTiledMapRenderer renderer; // renders the level tiles
	private World world; // reference to world created in GameMain
	public static final float PIXELS_TO_METERS = 0.15625f, METERS_TO_PIXELS = 6.4f;
	public static ArrayList<GameObject> gameObjs; // List of all gameobjects in the level
	private static ArrayList<GameObject> destroy; // List of objects to be destroyed
	private OrthographicCamera camera;
	private Player player;
	private TextureRegion healthText, healthOrb;
	private String mapName, nextMap;
	private boolean setReset;
	private Music soundtrack;

	// constructors; forward to create to keep them tidy
	public Level(World world, OrthographicCamera camera)
	{
		this.create(world, camera, "level_1.tmx");
	}

	public Level(World world, OrthographicCamera camera, String tilemap)
	{
		this.create(world, camera, tilemap);
	}

	// Load the TMX file, initialize renderer & parse collision objects
	public void create(World boxWorld, OrthographicCamera camera, String tilemap)
	{
		this.mapName = tilemap;
		this.nextMap = tilemap;
		map = new TmxMapLoader().load(tilemap);
		renderer = new OrthogonalTiledMapRenderer(map, 1);
		this.world = boxWorld;

		healthText = new TextureRegion(new Texture("example_health_text.png"));
		healthOrb = new TextureRegion(new Texture("example_health.png"));

		// These are static because only one level will exist at a time and it's easier to access them this way
		gameObjs = new ArrayList<GameObject>();
		destroy = new ArrayList<GameObject>();

		// Create parser. Originally I wrote my own code for this, but I found
		// this had been created recently.
		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.setUnitScale(Level.PIXELS_TO_METERS);
		parser.load(world, map);

		// report number of collision objects found for debug purposes
		Gdx.app.log("Number of objects in layer CollisionMap", "" + map.getLayers().get("CollisionMap").getObjects().getCount());

		this.camera = camera;

		// Find and create objects recorded on the Entities layer of the map
		interpretMapEntities();
		
		Gdx.app.log("Map "+mapName+" loaded", "-----------------------\n\n");
	}

	public void interpretMapEntities()
	{
		MapObjects mapObjects = map.getLayers().get("Entities").getObjects();
		
		Gdx.app.log("Number of objects in layer Entities", ""+mapObjects.getCount());
		
		//find the player and load him first
		for (MapObject object : mapObjects)
		{
			if (((String) object.getProperties().get("type", String.class)).equals("player_spawn"))
			{
				Gdx.app.log("Player", "created");
				player = new Player(world, new Vector2(object.getProperties().get("x", float.class), object
						.getProperties().get("y", float.class)));
			}
		}
		
		if (player == null)
		{
			Gdx.app.log("ERROR", "No player_spawn entity found!");
			player = new Player(world, new Vector2(128,128));
		}
		
		// Find the type, which defines what object the entity is, then find the position
		// and any other data necessary to create the object.
		for (MapObject object : mapObjects)
		{
			// create the example enemy
			if (((String) object.getProperties().get("type", String.class)).equals("example_enemy"))
			{
				Gdx.app.log("Enemy", "created");
				new Enemy(world, player, "example_enemy.png", new Rectangle(object.getProperties()
						.get("x", float.class), object.getProperties().get("y", float.class), 32, 32));
			}

			if (((String) object.getProperties().get("type", String.class)).equals("killzone"))
			{
				Gdx.app.log("Killzone", "created");
				new Killzone(world, new Rectangle(object.getProperties().get("x", float.class), object.getProperties()
						.get("y", float.class), object.getProperties().get("width", float.class), object
						.getProperties().get("height", float.class)));
			}

			if (((String) object.getProperties().get("type", String.class)).equals("changelevel"))
			{
				Gdx.app.log("Changelevel trigger", "created");
				new Changelevel(world, this, object.getProperties().get("load", String.class) + ".tmx", new Rectangle(
						object.getProperties().get("x", float.class), object.getProperties().get("y", float.class),
						object.getProperties().get("width", float.class), object.getProperties().get("height",
								float.class)));
			}
			
			if (((String) object.getProperties().get("type", String.class)).equals("music"))
			{
				soundtrack = Gdx.audio.newMusic(Gdx.files.internal("MAPS/"+object.getName()+".ogg"));
				soundtrack.setLooping(true);
				soundtrack.play();
			}
		}
	}

	public void update()
	{
		// always update the player first
		player.update();

		// update all game objects
		for (GameObject gameObject : gameObjs)
		{
			gameObject.update();
		}

		// clear the list of destroyed objects. The garbage collector and asset manager should handle
		// things from here.
		// destroy.clear();

		if (player.getHealth() <= 0)
		{
			setReset = true;
		}

		if (!nextMap.equals(mapName))
		{
			this.loadNewMap(nextMap);
		}
	}

	public void destroyBodies()
	{
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		
		for (Body body : bodies)
		{
			GameObject destructObj = (GameObject) body.getUserData();
			if (destructObj != null && destructObj.markedForDestruction)
			{
				gameObjs.remove(destructObj);
				world.destroyBody(destructObj.getBody());
			}
		}
		
		if (setReset)
		{
			setReset = false; 
			for (GameObject gameObj : gameObjs)
			{
				gameObj.markedForDestruction = true;
			}
			
			destroyBodies();
			
			loadNewMap(mapName);
		}
	}

	// render the level
	public void render(OrthographicCamera camera)
	{
		renderer.setView(camera);
		renderer.render();
	}

	// render object in the level
	public void renderObjects(SpriteBatch batch)
	{
		for (GameObject gameObject : gameObjs)
		{
			gameObject.render(batch);
		}

		// always render the player & GUI stuff last so that the player is always on top
		player.render(batch);

		Vector2 guiZero = new Vector2(camera.position.x - (camera.viewportWidth / 2), camera.position.y
				- (camera.viewportHeight / 2));

		batch.draw(healthText, guiZero.x + 3, guiZero.y + 704);

		switch (player.getHealth())
		{
			case 3:
				batch.draw(healthOrb, guiZero.x + 97, guiZero.y + 704);
			case 2:
				batch.draw(healthOrb, guiZero.x + 81, guiZero.y + 704);
			case 1:
				batch.draw(healthOrb, guiZero.x + 65, guiZero.y + 704);
		}
	}

	public void reset()
	{
		loadNewMap(mapName);
	}

	public void loadNewMap(String newMap)
	{
		// reset
		gameObjs.clear();
		destroy.clear();
		map.dispose();
		renderer.dispose();

		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);

		for (Body body : bodies)
		{
			world.destroyBody(body);
		}
		
		soundtrack.setLooping(false);
		soundtrack.stop();
		soundtrack.dispose();

		// load
		this.mapName = newMap;
		this.nextMap = newMap;
		map = new TmxMapLoader().load(newMap);
		renderer = new OrthogonalTiledMapRenderer(map, 1);

		healthText = new TextureRegion(new Texture("example_health_text.png"));
		healthOrb = new TextureRegion(new Texture("example_health.png"));

		// These are static because only one level will exist at a time and it's easier to access them this way
		gameObjs = new ArrayList<GameObject>();
		destroy = new ArrayList<GameObject>();

		// Create parser. Originally I wrote my own code for this, but I found
		// this had been created recently.
		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.setUnitScale(Level.PIXELS_TO_METERS);
		parser.load(world, map);

		// report number of collision objects found for debug purposes
		Gdx.app.log("Number of objects in layer CollisionMap", "" + map.getLayers().get("CollisionMap").getObjects().getCount());

		// Find and create objects recorded on the Entities layer of the map
		interpretMapEntities();
		
		Gdx.app.log("Map "+mapName+" loaded", "-----------------------\n\n");
	}

	public void setNextMap(String map)
	{
		this.nextMap = map;
	}

	public Player getPlayer()
	{
		return player;
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

	// Dispose of everything used. Need to implement the assets manager!
	public void dispose()
	{
		map.dispose();
		renderer.dispose();
		if (soundtrack != null)
		{
			soundtrack.setLooping(false);
			soundtrack.stop();
			soundtrack.dispose();
		}
	}
}
