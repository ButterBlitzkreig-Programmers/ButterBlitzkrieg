package net.bryanhaley.butterblitz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
		bodyDef.type = BodyType.DynamicBody;
		PolygonShape shape = new PolygonShape();
		
		//report number of collision objects found for debug purposes
		Gdx.app.log("Num Map Objects", ""+map.getLayers().get("CollisionMap").getObjects().getCount());
		
		//cycle through all collision objects
		for (int i = 0; i < map.getLayers().get("CollisionMap").getObjects().getCount(); i++)
		{
			MapObject collisionObj = map.getLayers().get("CollisionMap").getObjects().get(i);
			
			//if it's a rectangle, create a body based on its width, height, and position
			if (collisionObj instanceof RectangleMapObject)
			{
				Rectangle rect = ((RectangleMapObject) collisionObj).getRectangle();
	            /*shape.setAsBox((rect.width / 2) / 10, (rect.height / 2) / 10);

	            def.position.x = (rect.x / 10) + ((rect.width / 10) / 2);
	            def.position.y = (rect.y / 10) + ((rect.height / 10) / 2);*/
				
				bodyDef.position.set(rect.x, rect.y);
				Body body = world.createBody(bodyDef);
				
				shape.setAsBox(rect.width, rect.height);
				
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = shape;
				
				body.createFixture(fixtureDef);
	         }

			//if it's a polygon, create a shape based on its vertices
			else if (collisionObj instanceof PolygonMapObject)
			{
				Polygon poly = ((PolygonMapObject) collisionObj).getPolygon();
	            
	            bodyDef.position.x = poly.getOriginX();
	            bodyDef.position.y = poly.getOriginY();

	            bodyDef.position.set(poly.getOriginX(), poly.getOriginY());
				Body body = world.createBody(bodyDef);
				
				float vertices[] = poly.getTransformedVertices();
	            shape.set(vertices);
				
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = shape;
				
				body.createFixture(fixtureDef);
			}
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
