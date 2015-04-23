package net.bryanhaley.butterblitz;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

/* Level Objects handle the Tiled Map we import from assets */

public class Level
{
	private TiledMap map; // holds the TMX file
	private OrthogonalTiledMapRenderer renderer; // renders the level tiles

	// constructors; forward to create to keep them tidy
	public Level()
	{
		this.create("example.tmx");
	}

	public Level(String tilemap)
	{
		this.create(tilemap);
	}

	// Load the TMX file, initialize renderer
	public void create(String tilemap)
	{
		map = new TmxMapLoader().load(tilemap);
		renderer = new OrthogonalTiledMapRenderer(map, 1);
	}

	//get the collision objects from the CollisionMap layer in the map file
	public void addCollisionObjects(World world)
	{
		//create empty body definition and polygon shape for box2d
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		
		//Create parser. Originally I wrote my own code for this, but I found
		//this had been created recently.
		Box2DMapObjectParser parser = new Box2DMapObjectParser();
		parser.assignProperties(bodyDef, map.getProperties());
		
		//report number of collision objects found for debug purposes
		Gdx.app.log("Num Map Objects", ""+map.getLayers().get("CollisionMap").getObjects().getCount());
		
		//cycle through all collision objects
		for (int i = 0; i < map.getLayers().get("CollisionMap").getObjects().getCount(); i++)
		{
			//parse collisionObjects
			parser.createBody(world, map.getLayers().get("CollisionMap").getObjects().get(i));
		}
	}

	public void update()
	{

	}

	public void render(OrthographicCamera camera)
	{
		renderer.setView(camera);
		renderer.render();
	}

	// if we need to manipulate the map file elsewhere for whatever reason
	public TiledMap getMap()
	{
		return map;
	}

}
