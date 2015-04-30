package net.bryanhaley.butterblitz;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/* Every non-level object is a gameobject. Game Objects have an image,
 * and a box2d body. Every gameobject can be created, updated,
 * and rendered.
 */

public class GameObject
{
	protected TextureRegion tex;
	protected Body body;
	protected float width, height, originX, originY, scaleX, scaleY, rotation;
	protected ArrayList<GameObject> collidingWith; //list of objects this is currently colliding with
	protected boolean isOnGround;
	protected World world;
	
	public GameObject() { }
	
	public GameObject(World world)
	{
		this(world, "example_object.png", new Rectangle(200,200,32,32));
	}
	
	public GameObject(World world, String img, Rectangle collisionBox)
	{
		tex = new TextureRegion(new Texture(Gdx.files.internal(img)));
		this.create(world, tex, collisionBox);
	}
	
	public GameObject(World world, TextureRegion tex, Body body)
	{
		this.create(world, tex, body);
	}
	
	//Create the box2D body
	public void create(World world, TextureRegion tex, Rectangle collisionBox)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set((collisionBox.x+(collisionBox.width/2))*Level.PIXELS_TO_METERS, (collisionBox.y+(collisionBox.height/2))*Level.PIXELS_TO_METERS);
		
		Body body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(collisionBox.width/2*Level.PIXELS_TO_METERS, collisionBox.height/2*Level.PIXELS_TO_METERS);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 0.3f;
		fixtureDef.density = 1f;
		
		body.createFixture(fixtureDef);
		body.setUserData(this);
		
		shape.dispose();
		
		this.create(world, tex, body);
	}
	
	public void create(World world, TextureRegion tex, Body body)
	{
		this.tex = tex;
		this.body = body;
		
		this.width = tex.getRegionWidth();
		this.height = tex.getRegionHeight();
		
		originX = 0; originY = 0;
		scaleX = 1; scaleY = 1;
		rotation = 0;
		
		collidingWith = new ArrayList<GameObject>();
		
		this.world = world;
		
		// Add self to list of GameObjects in Level
		Level.gameObjs.add(this);
	}
	
	//Right now update just checks for collisions
	public void update()
	{
		for (GameObject collision : collidingWith)
		{
			checkCollision(collision);
		}
	}
	
	//Behavior in response to collisions goes in this method
	protected void checkCollision(GameObject collision) { }
	
	public void render(SpriteBatch batch)
	{
		batch.draw(tex, body.getPosition().x*Level.METERS_TO_PIXELS-(width/2),
				body.getPosition().y*Level.METERS_TO_PIXELS-(height/2),
				width/2, height/2, width, height, scaleX, scaleY,
				body.getTransform().getRotation()*57.2957795f);
	}
	
	//The following two methods handle the list of objects being collided with
	public void collisionWith(GameObject obj)
	{
		collidingWith.add(obj);
	}
	
	public void stopCollisionWith(GameObject obj)
	{
		collidingWith.remove(obj);
	}
	
	
	public boolean findIsOnGround()
	{
		Array<Contact> contactList = world.getContactList();
		
		for (Contact contact : contactList)
		{
			if (contact.isTouching() && (contact.getFixtureA() == body.getFixtureList().get(0) ||
					contact.getFixtureB() == body.getFixtureList().get(0)))
			{
				isOnGround = true;
				return true;
			}
		}
		
		isOnGround = false;
		return false;
	}
	
	//Ray trace to find if the object is on the ground (not 100% accurate)
	//Note that the origin of Box2D bodies are in the center, not the bottom left corner
	protected boolean findIsOnGroundRayTrace()
	{
		//this acts strange, replaced it with something more simple
		isOnGround = false;
		
		World world = body.getWorld();
		
		RayCastCallback callback = new RayCastCallback()
		{
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
			{
				/* This five pixel allowance is used in case of ramps and rotations and so on since our results aren't 100% accurate.
				 * But it does cause a bug - if the player or an enemy or etc is constantly jumping, the five px allowance will cause
				 * them to jump without actually touching the object below it, so a collision is not detected.
				 * We could manually send a collision to both objects, but we would also have to manually remove the objects from
				 * each other's collision list. There are a couple way to fix this, but I'm not worried about it at the moment.
				 */
				if (body.getPosition().y - point.y < (height/2+5) * Level.PIXELS_TO_METERS)
				{
					isOnGround = true;
				}
				return 0;
			}
		};

		//Raycast from the bottom corners of the object downwards
		Vector2 point1 = new Vector2(body.getPosition().x - ((width/2)*Level.PIXELS_TO_METERS), body.getPosition().y);
		Vector2 point2 = new Vector2(point1.x, -200);

		world.rayCast(callback, point1, point2);

		point1.set(body.getPosition().x + ((width/2) * Level.PIXELS_TO_METERS), point1.y);
		point2.set(point1.x, -200);

		world.rayCast(callback, point1, point2);
		
		return isOnGround;
	}
	
	//Box2D holds positions in "meters", so we have to convert to and from pixels for rendering
	//and other matters where using pixels is preferable.
	public Vector2 getPositionPixels()
	{
		Vector2 newPos = new Vector2(body.getPosition());
		newPos.set((newPos.x*Level.METERS_TO_PIXELS)-(width/2), (newPos.y*Level.METERS_TO_PIXELS)-(height/2));
		return newPos;
	}
	
	public Vector2 getPositionPixelsCentered()
	{
		Vector2 newPos = new Vector2(body.getPosition());
		newPos.set(newPos.x*Level.METERS_TO_PIXELS, newPos.y*Level.METERS_TO_PIXELS);
		return newPos;
	}
	
	public Body getBody()
	{
		return body;
	}
	
	public Vector2 getPositionMeters()
	{
		return body.getPosition();
	}
	
	//Add this object to a list of objects to be destroyed.
	public void destroy()
	{
		Level.destroyObject(this);
	}
	
	//Need an asset manager, will implement soon
	public void dispose()
	{
		tex.getTexture().dispose();
	}
}
